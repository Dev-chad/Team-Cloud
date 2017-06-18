package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class PdfViewerActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    Integer pageNumber = 0;
    PDFView pdfView;
    Uri uri;
    String pdfFileName;
    String fileUrl;

    ProgressDialog asyncDialog;
    private RelativeLayout layoutTop, layoutBottom;
    private ImageView imageBack, imageDownload;
    private TextView textFileName, textPage;

    private final static int REQUEST_CODE = 42;
    public static final int PERMISSION_CODE = 42042;
    private static final String TAG = PdfViewerActivity.class.getSimpleName();

    private boolean isViewMenu = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfFileName = getIntent().getStringExtra("file_name");
        fileUrl = getIntent().getStringExtra("file_url");

        layoutTop = (RelativeLayout)findViewById(R.id.layout_top);
        layoutBottom = (RelativeLayout)findViewById(R.id.layout_bottom);

        imageBack = (ImageView)findViewById(R.id.image_back);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageDownload = (ImageView)findViewById(R.id.image_download);
        imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, null);
                finish();
            }
        });

        textFileName= (TextView)findViewById(R.id.text_file_name);
        textFileName.setText(pdfFileName);

        textPage = (TextView)findViewById(R.id.text_page);

        pdfView = (PDFView) findViewById(R.id.pdfViewer);
        pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isViewMenu){
                    isViewMenu = false;

                    layoutTop.setVisibility(View.GONE);
                    layoutBottom.setVisibility((View.GONE));
                } else {
                    layoutTop.setVisibility(View.VISIBLE);
                    layoutBottom.setVisibility(View.VISIBLE);
                }
            }
        });

        asyncDialog = new ProgressDialog(this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("파일을 가져오는 중...");
        asyncDialog.show();

        PdfDownloadThread pdfDownloadThread = new PdfDownloadThread();
        pdfDownloadThread.start();
    }

    void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            //alert user that file manager not working
        }
    }

/*    void afterViews() {
        pdfView.setBackgroundColor(Color.LTGRAY);
        if (uri != null) {
            displayFromUri(uri);
        }
        setTitle(pdfFileName);
    }*/

    private void displayFromUri(Uri uri) {
        pdfFileName = getFileName(uri);

        pdfView.fromUri(uri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            uri = intent.getData();
            displayFromUri(uri);
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;

        textPage.setText(String.format("%s / %s", page + 1, pageCount));
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    /**
     * Listener for response to user permission request
     *
     * @param requestCode  Check that permission request code matches
     * @param permissions  Permissions that requested
     * @param grantResults Whether permissions granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPicker();
            }
        }
    }

    class PdfDownloadThread extends Thread {

        @Override
        public void run() {
            try {
                int count;

                File file = new File(Environment.getExternalStorageDirectory().toString() + "/TeamCloud/tmp");

                if (!file.exists()) {
                    file.mkdirs();
                }

                URL url = new URL(fileUrl);
                Log.d(TAG, url.toString());
                URLConnection connect = url.openConnection();
                connect.connect();

                int lengthOfFile = connect.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/TeamCloud/tmp/" + pdfFileName);
                Log.d(TAG, output.toString());
                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                handler.sendEmptyMessage(1);
                asyncDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {   // Message id 가 0 이면
                } else if (msg.what == 1) {
                    File file = new File(Environment.getExternalStorageDirectory().toString() + "/TeamCloud/tmp/" + pdfFileName);
                    displayFromUri(Uri.fromFile(file));
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/TeamCloud/tmp/" + pdfFileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
