package kr.co.appcode.teamcloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
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

    private URL url;
    private HttpCallBack httpCallBack;
    private ProgressDialog progressDialog;

    private String body;

    private boolean isUploadMode;
    private HashMap<String, String> values;

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

    public HttpConnection(HashMap<String, String> values, String url, HttpCallBack httpCallBack) {
        try {
            this.values = values;
            this.url = new URL(SERVER_URL + url);
            this.httpCallBack = httpCallBack;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setUploadMode(boolean isUploadMode) {
        this.isUploadMode = isUploadMode;
    }

    public void setValues(HashMap<String, String> values) {
        this.values = values;
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

            if (isUploadMode) {
                String boundary = "boundary";

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"teamIdx\"\r\n\r\n" + values.get("teamIdx"));
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"nickname\"\r\n\r\n" + values.get("nickname"));
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"boardIdx\"\r\n\r\n" + values.get("boardIdx"));
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"title\"\r\n\r\n" + values.get("title"));
                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"description\"\r\n\r\n" + values.get("description"));

                wr.writeBytes("\r\n--" + boundary + "\r\n");
                wr.writeBytes("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" + values.get("fileName") + "\"\r\n");
                wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

                FileInputStream fileInputStream = new FileInputStream(values.get("fileUri"));
                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
                    dataWrite.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                fileInputStream.close();

                wr.writeBytes("\r\n--" + boundary + "--\r\n");
                wr.flush();
            } else {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

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