package kr.co.appcode.teamcloud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {

    private PhotoView photoView;
    private RelativeLayout layoutTop, layoutBottom;
    private ImageView imageBack, imageDownload;
    private TextView textFileName;
    private boolean isMenuView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        final String url = getIntent().getStringExtra("url");
        final String fileName = getIntent().getStringExtra("file_name");

        photoView = (PhotoView)findViewById(R.id.photoView);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if(isMenuView){
                    isMenuView = false;
                    layoutTop.setVisibility(View.GONE);
                    layoutBottom.setVisibility(View.GONE);
                } else {
                    isMenuView = true;
                    layoutTop.setVisibility(View.VISIBLE);
                    layoutBottom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });

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

        textFileName = (TextView)findViewById(R.id.text_file_name);
        textFileName.setText(fileName);

        ImageDownloadThread imageDownloadThread = new ImageDownloadThread(url);
        imageDownloadThread.start();
    }

    class ImageDownloadThread extends Thread {
        Bitmap bmp;
        String url;

        public ImageDownloadThread(String url){
            this.url = url;
        }

        @Override
        public void run() {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                bmp = BitmapFactory.decodeStream(is);
                handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {   // Message id 가 0 이면
                    photoView.setImageBitmap(bmp);
                }
            }
        };
    }


}
