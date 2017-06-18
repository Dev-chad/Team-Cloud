package kr.co.appcode.teamcloud;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class FileBrowserActivity extends AppCompatActivity {
    private final static String TAG = "FileBrowserActivity";
    private File file;

    private ListView listFile;
    private UploadFileListAdapter adapter;

    private LinearLayout layoutReturn;

    private TextView textNoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutReturn = (LinearLayout) findViewById(R.id.layout_return);
        layoutReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFileList(file.getParent());
            }
        });

        adapter = new UploadFileListAdapter(this, new ArrayList<UploadFile>());

        listFile = (ListView) findViewById(R.id.list_file);
        listFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UploadFile file = (UploadFile) adapter.getItem(position);

                Log.d(TAG, file.getFileUri());

                if (file.getFileType() == UploadFile.TYPE_DIRECTORY) {
                    setFileList(file.getFileUri());
                } else {
                    AlertDialog.Builder checkDialog = new AlertDialog.Builder(FileBrowserActivity.this);

                    checkDialog
                            .setTitle("파일 선택")
                            .setMessage(file.getFileName() + "\n해당 파일을 선택하시겠습니까?");

                    checkDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK, getIntent().putExtra("selectedFile", file));
                            dialog.dismiss();
                            finish();
                        }
                    });

                    checkDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    checkDialog.show();
                }
            }
        });
        listFile.setAdapter(adapter);

        textNoFile = (TextView) findViewById(R.id.text_no_file);

        setFileList(Environment.getExternalStorageDirectory().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setFileList(String dir) {
        ArrayList<UploadFile> dirList = new ArrayList<>();
        ArrayList<UploadFile> fileList = new ArrayList<>();

        file = new File(dir);

        File arrFileList[] = file.listFiles();

        for (File file : arrFileList) {
            if (file.isDirectory()) {
                File[] list = file.listFiles();
                if(list == null){

                    dirList.add(new UploadFile(file.getName(), file.getAbsolutePath(), 0));
                    Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
                } else {
                    dirList.add(new UploadFile(file.getName(), file.getAbsolutePath(), list.length));
                }
            } else {
                SimpleDateFormat format = new SimpleDateFormat("YY.mm.dd hh:mm", Locale.KOREA);
                Date date = new Date(file.lastModified());
                fileList.add(new UploadFile(file.getName(), file.getAbsolutePath(), getSize(file.length()), format.format(date)));
            }
        }

        Collections.sort(dirList, new Ascending());
        Collections.sort(fileList, new Ascending());
        dirList.addAll(fileList);

        adapter.setFileList(dirList);
        adapter.notifyDataSetChanged();

        if (file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().toString())) {
            layoutReturn.setVisibility(View.GONE);
        } else {
            layoutReturn.setVisibility(View.VISIBLE);
        }

        if (adapter.getCount() == 0) {
            textNoFile.setVisibility(View.VISIBLE);
        } else {
            textNoFile.setVisibility(View.GONE);
        }
    }

    private String getSize(long size) {
        double dSize = Double.parseDouble(String.valueOf(size));
        String unit;
        int count = 0;

        while (dSize >= 1024 && count < 5) {
            dSize /= 1024;
            count++;
        }

        if (count == 0) {
            unit = "B";
        } else if (count == 1) {
            unit = "KB";
        } else if (count == 2) {
            unit = "MB";
        } else if (count == 3) {
            unit = "GB";
        } else {
            unit = "TB";
        }

        return String.format(Locale.KOREA, "%.2f %s", dSize, unit);
    }

    private class Ascending implements Comparator<UploadFile> {

        @Override
        public int compare(UploadFile o1, UploadFile o2) {
            return o1.getFileName().toUpperCase().compareTo(o2.getFileName().toUpperCase());
        }
    }
}
