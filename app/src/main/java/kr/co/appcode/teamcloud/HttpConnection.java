package kr.co.appcode.teamcloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class HttpConnection extends AsyncTask<Void, Void, JSONObject> {
    private static final String TAG = "HttpConnection";
    private static final String SERVER_URL = "http://appcode.cafe24.com/";
    public static final int MODE_LOGIN = 1;

    private HashMap<String, String> values;
    private URL url;
    private String body;
    private HttpCallBack httpCallBack;
    private int mode;
    private ProgressDialog progressDialog;

/*    public HttpConnection(Activity activity, HashMap<String, String> values, HttpCallBack httpCallBack) {
        this.values = values;
        this.httpCallBack = httpCallBack;
        progressDialog = new ProgressDialog(activity);
    }*/

    public HttpConnection(Activity activity, String body, String url, HttpCallBack httpCallBack) {
        try {
            this.body = body;
            this.url = new URL(SERVER_URL + url);
            this.httpCallBack = httpCallBack;
            progressDialog = new ProgressDialog(activity);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public HttpConnection(String body, String url, HttpCallBack httpCallBack) {
        try {
            this.body = body;
            this.url = new URL(SERVER_URL + url);
            this.httpCallBack = httpCallBack;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        /*progressDialog.setMessage("잠시 기다려주세요");
        progressDialog.show();*/

    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String json;
            while ((json = br.readLine()) != null) {
                sb.append(json).append("\n");
            }

            return new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
//        progressDialog.dismiss();
        httpCallBack.CallBackResult(result);
    }

}