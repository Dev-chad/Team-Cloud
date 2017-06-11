package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class ContentDetailActivity extends AppCompatActivity {
    private final static String TAG = "ContentDetailActivity";

    private Board board;
    private Content content;
    private User user;
    private Team team;

    private RelativeLayout layoutMyContent;
    private RelativeLayout layoutFile;

    private TextView textTitle;
    private TextView textWriter;
    private TextView textDate;
    private TextView textDesc;
    private TextView textFileName;
    private TextView textFileSize;
    private TextView textNoFile;

    private ImageView imageFile;
    private ImageView imageEdit;
    private ImageView imageDelete;
    private ImageView imageDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        board = intent.getParcelableExtra("board");
        content = intent.getParcelableExtra("content");
        user = intent.getParcelableExtra("login_user");
        team = intent.getParcelableExtra("team");

        if(content.getWriter().equals(user.getNickname())){
            layoutMyContent = (RelativeLayout)findViewById(R.id.layout_my_content);
            layoutMyContent.setVisibility(View.VISIBLE);

            imageDelete = (ImageView)findViewById(R.id.image_delete);
            imageEdit = (ImageView)findViewById(R.id.image_edit);
            imageEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder checkDialog = new AlertDialog.Builder(ContentDetailActivity.this);

                    checkDialog
                            .setTitle("게시물 수정")
                            .setMessage("해당 게시물을 수정하시겠습니까?");

                    checkDialog.setPositiveButton("수정하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ContentDetailActivity.this, UploadActivity.class);
                            intent.putExtra("board", board);
                            intent.putExtra("login_user", user);
                            intent.putExtra("content", content);
                            intent.putExtra("team", team);

                            startActivityForResult(intent, 1);

                            dialog.dismiss();
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
            });
        }

        if(content.getFileName() != null){
            layoutFile = (RelativeLayout)findViewById(R.id.layout_file);
            layoutFile.setVisibility(View.VISIBLE);

            imageFile = (ImageView)findViewById(R.id.image_file);
            imageDownload = (ImageView)findViewById(R.id.image_download);
            imageDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncFileDownload asyncFileDownload = new AsyncFileDownload(ContentDetailActivity.this);
                    asyncFileDownload.execute("http://appcode.cafe24.com/"+content.getFileUrl());
                }
            });

            textFileName = (TextView)findViewById(R.id.text_file_name);
            textFileName.setText(content.getFileName());

            textFileSize = (TextView)findViewById(R.id.text_file_size);
            textFileSize.setText(getSize(content.getFileSize()));
        } else {
            textNoFile = (TextView)findViewById(R.id.text_no_file);
            textNoFile.setVisibility(View.VISIBLE);
        }

        textTitle = (TextView)findViewById(R.id.text_title);
        textTitle.setText(content.getTitle());

        textWriter = (TextView)findViewById(R.id.text_writer);
        textWriter.setText(content.getWriter());

        textDate = (TextView)findViewById(R.id.text_date);
        textDate.setText(content.getWriteDate());

        textDesc = (TextView)findViewById(R.id.text_desc);
        textDesc.setText(content.getDesc());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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

    class AsyncFileDownload extends AsyncTask<String, String, String>{

        private ProgressDialog mDlg;
        private Context mContext;

        public AsyncFileDownload(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            File file = new File(Environment.getExternalStorageDirectory().toString()+"/TeamCloud/"+team.getName());

            if(!file.exists()){
                file.mkdirs();
            }

            mDlg = new ProgressDialog(mContext);
            mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDlg.setMessage("Start");
            mDlg.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            int count = 0;

            try {
                Thread.sleep(100);
                URL url = new URL(params[0]);
                Log.d(TAG, url.toString());
                URLConnection connect = url.openConnection();
                connect.connect();

                int lengthOfFile = connect.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lengthOfFile);

                publishProgress("max", String.valueOf(lengthOfFile));

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/TeamCloud/"+team.getName()+"/"+content.getFileName());
                Log.d(TAG, output.toString());
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("progress", "" + (int)total, total+"/"+lengthOfFile);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                // 작업이 진행되면서 호출하며 화면의 업그레이드를 담당하게 된다
                //publishProgress("progress", 1, "Task " + 1 + " number");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 수행이 끝나고 리턴하는 값은 다음에 수행될 onProgressUpdate 의 파라미터가 된다
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (progress[0].equals("progress")) {
                mDlg.setProgress(Integer.parseInt(progress[1]));
                mDlg.setMessage(progress[2]);
            } else if (progress[0].equals("max")) {
                mDlg.setMax(Integer.parseInt(progress[1]));
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(String unused) {
            mDlg.dismiss();
            Toast.makeText(mContext, "다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
