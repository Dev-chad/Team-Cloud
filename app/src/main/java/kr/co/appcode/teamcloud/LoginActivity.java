package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private MaterialEditText editEmail;
    private MaterialEditText editPassword;

    private TextView textError;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        if (sp.contains("id")) {
            HashMap<String, String> values = new HashMap<>();
            values.put("id", sp.getString("id", ""));
            values.put("password", sp.getString("password", ""));
            HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(values);
            httpPostAsyncTask.execute();
        }

        callbackManager = CallbackManager.Factory.create();

        editEmail = (MaterialEditText) findViewById(R.id.edit_email);
        editEmail.addValidator(new RegexpValidator("이메일 형식이 올바르지 않습니다", "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"));
        editPassword = (MaterialEditText) findViewById(R.id.edit_password);
        editPassword.addValidator(new RegexpValidator("비밀번호 형식이 올바르지 않습니다", "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,20}$"));

        textError = (TextView) findViewById(R.id.text_error);

        editEmail.addTextChangedListener(new TextWatcher() {
            //region Unused method
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //endregion

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    editEmail.setError(null);
                }

                if (textError.getVisibility() == View.VISIBLE) {
                    textError.setVisibility(View.GONE);
                }
            }
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            //region Unused method
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            //endregion

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    editPassword.setError(null);
                }

                if (textError.getVisibility() == View.VISIBLE) {
                    textError.setVisibility(View.GONE);
                }
            }
        });

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();

                // check input value
                if (email.length() == 0) {
                    editEmail.setError("이메일을 입력해주세요");
                } else if (!editEmail.validate()) {

                } else if (password.length() == 0) {
                    editPassword.setError("비밀번호를 입력해주세요");
                } else if (!editPassword.validate()) {

                } else {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("id", email);
                    values.put("password", password);

                    HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(values);
                    httpPostAsyncTask.execute();
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
            public void onSuccess(final LoginResult loginResult) {
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());
                        User user = new User(object, "facebook");
                        user.setSessionKey(loginResult.getAccessToken().getToken());
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("login_user", user);
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
                Log.e(TAG, error.toString());
            }
        });

        TextView textForgotPassword = (TextView) findViewById(R.id.text_forgotPassword);
        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FoundPasswordActivity.class);
                startActivity(intent);
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
            progressDialog.setMessage("로그인 중...");
            progressDialog.show();
            try {
                url = new URL(Constant.SERVER_URL + "login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            body = "id=" + values.get("id") + "&password=" + values.get("password");
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
                if (jsonObject.has("error")) {
                    errorCode = jsonObject.getInt("error");
                    Log.d(TAG, "Error code : " + errorCode);
                } else {
                    Log.d(TAG, "Json => " + jsonObject.toString());

                    User user = new User(jsonObject);
                    user.setSessionKey(conn.getHeaderField("Set-Cookie"));

                    return user;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(User result) {
            if (result != null) {
                SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = sp.edit();

                if(sp.contains("id")){
                    Toast.makeText(LoginActivity.this, "자동 로그인 성공", Toast.LENGTH_SHORT).show();
                } else {
                    spEditor.putString("id", result.getId());
                    spEditor.putString("password", md5(values.get("password")));
                    Log.d(TAG, values.get("password"));
                    Log.d(TAG,  md5(values.get("password")));
                    spEditor.putString("type", "teamcloud");
                    spEditor.apply();
                }


                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("login_user", result);
                startActivity(intent);
            } else {
                if (errorCode == Constant.LOGIN_ERROR) {
                    textError.setVisibility(View.VISIBLE);
                } else if (errorCode == Constant.LOGIN_EMPTY_ID) {

                } else if (errorCode == Constant.LOGIN_EMPTY_PASSWORD) {
                }
            }

            progressDialog.dismiss();
        }
    }

    private void closingSoftKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (editPassword.length() > 0) {
            editPassword.setText("");
        }
    }


    public String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
