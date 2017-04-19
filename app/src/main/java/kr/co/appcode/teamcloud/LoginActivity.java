package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layoutEditId;
    private TextInputLayout layoutEditPassword;
    private EditText editId;
    private EditText editPassword;

    private CallbackManager callbackManager;
    private String cookie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        layoutEditId = (TextInputLayout) findViewById(R.id.layout_editId);
        layoutEditPassword = (TextInputLayout) findViewById(R.id.layout_editPassword);
        editId = (EditText) findViewById(R.id.edit_id);
        editPassword = (EditText) findViewById(R.id.edit_password);

        Button btnSignIn = (Button) findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String password = editPassword.getText().toString();

                // hide error message
                layoutEditId.setErrorEnabled(false);
                layoutEditPassword.setErrorEnabled(false);

                // check input value
                if(id.length() == 0){
                    layoutEditId.setError("아이디를 입력해주세요.");
                } else if(id.contains(" ")){
                    layoutEditId.setError("아이디에 공백은 허용하지 않습니다.");
                } else if(password.length() == 0){
                    layoutEditPassword.setError("비밀번호를 입력해주세요.");
                } else if(password.contains(" ")){
                    layoutEditPassword.setError("비밀번호에 공백은 허용하지 않습니다.");
                }else {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("id", id);
                    values.put("password", password);
                    try {
                        HttpPostThread httpPostThread = new HttpPostThread(values, new URL("http://appcode.co.kr/TeamCloud/login.php"), 1);
                        httpPostThread.start();
                        httpPostThread.join();
                        Log.d("return", ""+httpPostThread.getResultCode());

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("cookie", cookie);
                        startActivity(intent);
                    } catch (MalformedURLException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });

        LoginButton loginButton = (LoginButton)findViewById(R.id.btn_facebook_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback(){
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("LoginError", error.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class HttpPostThread extends Thread {
        private HashMap<String, String> values;
        private URL url;
        private int resultCode;
        private int mode;

        private HttpPostThread(HashMap<String, String> values, URL url, int mode) {
            this.values = values;
            this.url = url;
            this.mode = mode;
        }

        public void run() {
            try {
                String body = "id="+values.get("id")+"&password="+values.get("password");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes("UTF-8"));
                os.flush();
                os.close();

                cookie = conn.getHeaderField("Set-Cookie");
                if(cookie != null){
                    Log.d("cookie", cookie);
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                resultCode = Integer.valueOf(br.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getResultCode() {
            return resultCode;
        }
    }
}
