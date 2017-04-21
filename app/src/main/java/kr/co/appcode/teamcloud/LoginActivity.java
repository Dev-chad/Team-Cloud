package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
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
    private static final String TAG = "LoginActivity";

    private TextInputLayout layoutEditId;
    private TextInputLayout layoutEditPassword;
    private EditText editId;
    private EditText editPassword;

    private CallbackManager callbackManager;

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
                closingSoftKeyboard();

                String id = editId.getText().toString();
                String password = editPassword.getText().toString();

                // hide error message
                layoutEditId.setErrorEnabled(false);
                layoutEditPassword.setErrorEnabled(false);

                // check input value
                if (id.length() == 0) {
                    layoutEditId.setError("아이디를 입력해주세요.");
                } else if (id.contains(" ")) {
                    layoutEditId.setError("아이디에 공백은 허용하지 않습니다.");
                } else if (password.length() == 0) {
                    layoutEditPassword.setError("비밀번호를 입력해주세요.");
                } else if (password.contains(" ")) {
                    layoutEditPassword.setError("비밀번호에 공백은 허용하지 않습니다.");
                } else {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("id", id);
                    values.put("password", password);

                    HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(values);
                    httpPostAsyncTask.execute();
                    /*try {
                        HttpPostThread httpPostThread = new HttpPostThread(values, new URL("http://appcode.co.kr/TeamCloud/login.php"), 1);
                        httpPostThread.start();
                        httpPostThread.join();
                        Log.d("return", ""+httpPostThread.getResultCode());

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("cookie", cookie);
                        startActivity(intent);
                    } catch (MalformedURLException | InterruptedException e) {
                        e.printStackTrace();
                    }*/
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

        LoginButton loginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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

    private class HttpPostAsyncTask extends AsyncTask<Void, Void, User> {

        private int errorCode;
        private HashMap<String, String> values;
        private URL url;
        private Context context;
        private ProgressDialog progressDialog;
        private String body;

        private HttpPostAsyncTask(HashMap<String, String> values) {
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("잠시 기다려주세요.");
            progressDialog.show();
            try {
                url = new URL("http://appcode.co.kr/TeamCloud/login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            body = "id=" + editId.getText().toString() + "&password=" + editPassword.getText().toString();
        }

        @Override
        protected User doInBackground(Void... params) {
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
                Log.d(TAG, sb.toString());

                JSONObject jsonObject = new JSONObject(sb.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.has("error")){
                    errorCode = jsonObject.getInt("error");
                    Log.d(TAG, "Error code : " + errorCode);
                } else {
                    Log.d(TAG, "Json => " + jsonObject.toString());
                    User user = new User(jsonObject);
                    String cookie = conn.getHeaderField("Set-Cookie");
                    if (cookie != null) {
                        Log.d("cookie", cookie);
                        user.setSessionKey(cookie);
                    }

                    return user;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User result) {
            if(result != null){
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("login_user", result);
                startActivity(intent);
            } else {
                if(errorCode == Constant.LOGIN_ERROR){
                    Snackbar.make(layoutEditId, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                }else if(errorCode == Constant.LOGIN_EMPTY_ID){
                    layoutEditId.setError("이메일을 입력하지 않았습니다.");
                }else if(errorCode == Constant.LOGIN_EMPTY_PASSWORD){
                    layoutEditPassword.setError("비밀번호를 입력하지 않았습니다.");
                }
            }

            progressDialog.dismiss();
        }
    }

    private void closingSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
