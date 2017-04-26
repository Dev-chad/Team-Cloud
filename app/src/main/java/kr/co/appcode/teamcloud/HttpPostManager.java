package kr.co.appcode.teamcloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class HttpPostManager extends AsyncTask<Void, Void, JSONObject> {
    //region Constant
    private static final String TAG = "HttpPostManager";
    private static final String SERVER_URL = "http://appcode.cafe24.com/";
    public static final int MODE_LOGIN = 1;
    public static final int MODE_AUTO_LOGIN = 2;
    public static final int MODE_JOIN = 3;
    public static final int MODE_EMAIL_CHECK = 4;
    public static final int MODE_NICKNAME_CHECK = 5;
    public static final int MODE_JOIN_FACEBOOK = 6;
    public static final int MODE_REISSUE = 7;

    //endregion

    private HashMap<String, String> values;
    private URL url;
    private String body;
    private HttpCallBack httpCallBack;
    private int mode;
    private ProgressDialog progressDialog;

    public HttpPostManager(Activity context, HashMap<String, String> values, HttpCallBack httpCallBack) {
        this.values = values;
        this.httpCallBack = httpCallBack;
        progressDialog = new ProgressDialog(context);
    }

    public void setMode(int mode) {
        this.mode = mode;

        try {
            if(mode == MODE_LOGIN || mode == MODE_AUTO_LOGIN){
                url = new URL(SERVER_URL+"login.php");
                body = "id="+values.get("id")+"&password="+values.get("password");
                if(mode == MODE_AUTO_LOGIN){
                    body+="&type=auto";
                } else {
                    body+="&type=normal";
                }
            }else if(mode == MODE_JOIN || mode == MODE_EMAIL_CHECK || mode == MODE_NICKNAME_CHECK){
                url = new URL(SERVER_URL+"join.php");
                if(mode == MODE_NICKNAME_CHECK){
                    body = "nickname=" + values.get("nickname");
                } else if(mode == MODE_EMAIL_CHECK){
                    body = "email=" + values.get("email") + "&auth_code=" + values.get("auth_code");
                } else if(mode == MODE_JOIN){
                    body = "id=" + values.get("id") + "&password=" + values.get("password") + "&nickname=" + values.get("nickname") + "&name=" + values.get("name");
                }
            }else if(mode == MODE_JOIN_FACEBOOK){
                url = new URL(SERVER_URL+"joinFacebook.php");
                body = "id=" + values.get("id") + "&token="+values.get("token")+"&nickname="+values.get("nickname")+"&name="+values.get("name");
            }else if(mode == MODE_REISSUE){
                url = new URL(SERVER_URL+"reissue.php");
                body = "id="+values.get("email");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setMessage("잠시 기다려주세요");
        progressDialog.show();

    }

    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);

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

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            jsonObject = jsonArray.getJSONObject(0);

            Log.d(TAG, jsonObject.toString());

            if(!jsonObject.has("error")){
                if(mode == MODE_LOGIN || mode==MODE_AUTO_LOGIN){
                    String sessionInfo = conn.getHeaderField("Set-Cookie");
                    if(sessionInfo != null){
                        jsonObject.put("sessionInfo", sessionInfo);
                    }
                }
            }

            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        progressDialog.dismiss();
        httpCallBack.CallBackResult(result);
    }

}
