package kr.co.appcode.teamcloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class HttpConnection extends AsyncTask<Void, Void, JSONObject> {
    //region Constant
    private static final String TAG = "HttpConnection";
    private static final String SERVER_URL = "http://appcode.cafe24.com/";
    public static final int MODE_LOGIN = 1;
    public static final int MODE_AUTO_LOGIN = 2;
    public static final int MODE_JOIN = 3;
    public static final int MODE_AUTH_EMAIL = 4;
    public static final int MODE_NICKNAME_CHECK = 5;
    public static final int MODE_JOIN_FACEBOOK = 6;
    public static final int MODE_REISSUE = 7;
    public static final int MODE_TEAMNAME_CHECK = 8;
    public static final int MODE_CREATE_TEAM = 9;
    public static final int MODE_REFRESH_USER = 10;
    public static final int MODE_GET_TEAM_LIST = 11;
    public static final int MODE_TEAM_SEARCH = 12;
    public static final int MODE_JOIN_TEAM = 13;
    public static final int MODE_JOIN_CANCEL= 14;
    public static final int MODE_TEAM_HOME = 15;
    public static final int MODE_GET_BOARD = 16;

    private boolean isCheckSession;
    //endregion

    private HashMap<String, String> values;
    private URL url;
    private String body;
    private HttpCallBack httpCallBack;
    private int mode;
    private ProgressDialog progressDialog;

    public HttpConnection(Activity activity, HashMap<String, String> values, HttpCallBack httpCallBack) {
        this.values = values;
        this.httpCallBack = httpCallBack;
        progressDialog = new ProgressDialog(activity);
    }

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

    public void setMode(int mode) {
        this.mode = mode;
        try {
            if (mode == MODE_LOGIN) {
                url = new URL(SERVER_URL + "login.php");
                body = "id=" + values.get("id") + "&password=" + values.get("password") + "&loginType=" + values.get("loginType");
                Log.d(TAG, "body: " + body);
            } else if (mode == MODE_JOIN) {
                url = new URL(SERVER_URL + "join.php");
                body = "id=" + values.get("id") + "&password=" + values.get("password") + "&nickname=" + values.get("nickname") + "&name=" + values.get("name") + "&joinType=" + values.get("joinType");
            } else if (mode == MODE_AUTH_EMAIL) {
                url = new URL(SERVER_URL + "emailAuth.php");
                body = "id=" + values.get("id") + "&authCode=" + values.get("authCode");
            } else if (mode == MODE_NICKNAME_CHECK) {
                url = new URL(SERVER_URL + "duplicateCheck.php");
                body = "nickname=" + values.get("nickname");
            } else if (mode == MODE_REISSUE) {
                url = new URL(SERVER_URL + "reissue.php");
                body = "id=" + values.get("id");
            } else if (mode == MODE_TEAMNAME_CHECK) {
                url = new URL(SERVER_URL + "duplicateCheck.php");
                body = "teamName=" + values.get("teamName");
            } else if (mode == MODE_CREATE_TEAM) {
                url = new URL(SERVER_URL + "createTeam.php");
                body = "teamName=" + values.get("teamName") + "&master=" + values.get("master") + "&maxCapacity=" + values.get("maxCapacity") + "&isPublic=" + values.get("isPublic") + "&isAutoJoin=" + values.get("isAutoJoin");
            } else if (mode == MODE_REFRESH_USER) {
                url = new URL(SERVER_URL + "refreshUserData.php");
                body = "nickname=" + values.get("nickname");
            } else if (mode == MODE_GET_TEAM_LIST) {
                url = new URL(SERVER_URL + "getTeamList.php");
                body = "nickname=" + values.get("nickname");
            } else if (mode == MODE_TEAM_SEARCH) {
                url = new URL(SERVER_URL + "searchTeam.php");
                body = "teamName=" + values.get("teamName") + "&nickname=" + values.get("nickname") + "&start=" +values.get("start");
            } else if(mode == MODE_JOIN_TEAM){
                url = new URL(SERVER_URL+"joinTeam.php");
                body = "nickname="+values.get("nickname")+"&teamName="+values.get("teamName");
            } else if(mode == MODE_JOIN_CANCEL){
                url = new URL(SERVER_URL+"joinCancel.php");
                body = "nickname="+values.get("nickname")+"&teamName="+values.get("teamName");
            } else if(mode == MODE_TEAM_HOME){
                url = new URL(SERVER_URL+"home.php");
                body = "nickname="+values.get("nickname")+"&teamName="+values.get("teamName");
            } else if(mode == MODE_GET_BOARD){
                url = new URL(SERVER_URL + "getBoard.php");
                body = "teamName="+values.get("teamName")+"&nickname="+values.get("nickname");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setBody(String body){
        this.body = body;
    }

    public void setUrl(String url){
        try {
            this.url = new URL(SERVER_URL + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setCheckSession(boolean isCheckSession) {
        this.isCheckSession = isCheckSession;
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
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (isCheckSession) {
                conn.setRequestProperty("Cookie", values.get("sessionInfo"));
            }
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

            Log.d(TAG, jsonObject.toString());

            if (mode == MODE_LOGIN) {
                String sessionInfo = conn.getHeaderField("Set-Cookie");
                if (sessionInfo != null) {
                    jsonObject.put("sessionInfo", sessionInfo);
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
//        progressDialog.dismiss();
        httpCallBack.CallBackResult(result);
    }

}