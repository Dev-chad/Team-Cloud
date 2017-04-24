/*
package kr.co.appcode.teamcloud;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

*/
/**
 * Created by Chad on 2017-04-19.
 *//*


public class HttpPost extends Thread {
    //region Constant
    private static final String TAG = "HttpPostClass";

    public static final int JOIN_COMPLETE = 1;
    public static final int JOIN_EMPTY_EMAIL = 2;
    public static final int JOIN_DUPLICATE_EMAIL = 3;
    public static final int JOIN_EMPTY_PASSWORD = 4;
    public static final int JOIN_EMPTY_NICKNAME = 5;
    public static final int JOIN_DUPLICATE_NICKNAME = 6;
    public static final int JOIN_EMPTY_NAME = 7;
    public static final int JOIN_QUERY_ERROR = -1;

    public static final int DUPLICATED = 2;
    public static final int NOT_DUPLICATED = 1;

    public static final int MODE_CHECK_NICKNAME = 2;
    public static final int MODE_AUTH_EMAIL = 3;
    public static final int MODE_JOIN_SUBMIT = 4;

    public static final int MAIL_SEND_COMPLETE = 1;
    public static final int MAIL_SEND_ERROR = 2;

    //endregion

    private HashMap<String, String> values;
    private URL url;
    private int resultCode;
    private int mode;
    private String body;
    private String sessionKey;

    public HttpPost(HashMap<String, String> values, int mode){
        setMode(values, mode);
    }

    public int getResultCode() {
        return resultCode;
    }

    public int getMode() {
        return mode;
    }

    private void setMode(HashMap<String, String> values, int mode){
        String strUrl = "http://appcode.co.kr/TeamCloud/";
        this.mode = mode;
        this.values = values;
        boolean isOk = false;

        if (values != null) {
            if (mode == MODE_CHECK_EMAIL) {
                if (!values.containsKey("email")) {
                    Log.e("TAG", "values does not have 'email' key.");
                    return;
                } else {
                    body = "email=" + values.get("email");
                    strUrl += "duplicateCheck.php";
                    isOk = true;
                }
            } else if (mode == MODE_AUTH_EMAIL) {
                if (!values.containsKey("email")) {
                    Log.e("TAG", "values does not have 'email' key.");
                } else if (!values.containsKey("authCode")) {
                    Log.e("TAG", "values does not have 'authCode' key.");
                } else {
                    body = "email=" + values.get("email") + "&auth_code=" + values.get("authCode");
                    strUrl += "emailAuth.php";
                    isOk = true;
                }
            } else if (mode == MODE_CHECK_NICKNAME) {
                if(!values.containsKey("nickname")){
                    Log.e("TAG", "values does not have 'nickname' key.");
                } else {
                    body = "nickname=" + values.get("nickname");
                    strUrl += "duplicateCheck.php";
                    isOk = true;
                }
            } else if (mode == MODE_JOIN_SUBMIT) {
                if(!values.containsKey("email")){
                    Log.e("TAG", "values does not have 'email' key.");

                } else if(!values.containsKey("password")){
                    Log.e("TAG", "values does not have 'password' key.");

                } else if(!values.containsKey("nickname")){
                    Log.e("TAG", "values does not have 'nickname' key.");

                } else if(!values.containsKey("name")){
                    Log.e("TAG", "values does not have 'name' key.");

                } else {
                    body = "email=" + values.get("email") + "&password=" + values.get("password") + "&nickname=" + values.get("nickname") + "&name=" + values.get("name");
                    strUrl += "join.php";
                    isOk = true;
                }
            } else {
                Log.e(TAG, "Invalid mode. [mode: " + mode + "]");
                this.mode = 0;
            }

            if(isOk){
                try {
                    url = new URL(strUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.e("TAG", "values is null.");
        }

    }

    public String getSessionInfo() {
        return sessionKey;
    }

    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes("UTF-8"));
            os.flush();
            os.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            resultCode = Integer.valueOf(br.readLine());
            Log.d(TAG, "mode: " + mode + "  result: " + resultCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/
