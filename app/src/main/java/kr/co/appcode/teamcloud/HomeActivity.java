package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private User user;
    private TextView textEmail;
    private TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = getIntent().getParcelableExtra("login_user");

        TextView textId = (TextView)findViewById(R.id.text_id);
        textEmail = (TextView)findViewById(R.id.text_email);
        textName = (TextView)findViewById(R.id.text_name);


        HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask();
        httpPostAsyncTask.execute();

        textId.setText("id : " + user.getId());

        Button btn = (Button)findViewById(R.id.btn_refresh);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask();
                httpPostAsyncTask.execute();
            }
        });

    }

    private class HttpPostAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private URL url;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                url = new URL("http://appcode.co.kr/TeamCloud/main.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("잠시 기다려주세요");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Cookie", user.getSessionKey());
                conn.setDoInput(true);
                conn.setDoInput(true);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                return br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, result);
            textEmail.setText("Session result : "+result);
            textName.setText(Calendar.getInstance().getTime().toString());
            progressDialog.dismiss();
        }
    }

    /*private class HttpPostThread extends Thread {
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
    }*/


}
