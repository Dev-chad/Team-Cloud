package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String cookie = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cookie = getIntent().getStringExtra("cookie");
        Log.d("intent", cookie);
        TextView textId = (TextView)findViewById(R.id.text_id);
        TextView textEmail = (TextView)findViewById(R.id.text_email);
        TextView textName = (TextView)findViewById(R.id.text_name);

        try {
            HttpPostThread httpPostThread = new HttpPostThread(null, new URL("http://appcode.co.kr/TeamCloud/main.php"), 0);
            httpPostThread.start();
            httpPostThread.join();
            Log.d("id", httpPostThread.getResultCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class HttpPostThread extends Thread {
        private HashMap<String, String> values;
        private URL url;
        private String resultCode;
        private int mode;

        private HttpPostThread(HashMap<String, String> values, URL url, int mode) {
            this.values = values;
            this.url = url;
            this.mode = mode;
        }

        public void run() {
            try {

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Cookie", cookie);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                resultCode = br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getResultCode() {
            return resultCode;
        }
    }


}
