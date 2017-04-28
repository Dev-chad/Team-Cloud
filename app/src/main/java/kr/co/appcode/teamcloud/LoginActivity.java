package kr.co.appcode.teamcloud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    //region Constant
    private static final String TAG = "LoginActivity";
    private static final int LOGIN_ERROR = 2;
    private static final int LOGIN_EMPTY_ID = 3;
    private static final int LOGIN_EMPTY_PASSWORD = 4;
    //endregion

    private MaterialEditText editEmail;
    private MaterialEditText editPassword;

    private TextView textError;

    private CallbackManager callbackManager;
    private HttpPostManager httpPostManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            HashMap<String, String> values = new HashMap<>();
            values.put("id", profile.getId());
            values.put("loginType", "facebook");

            httpPostManager = new HttpPostManager(this, values, httpCallBack);
            httpPostManager.setMode(HttpPostManager.MODE_LOGIN);
            httpPostManager.execute();

        } else if (sp.contains("id")) {
            HashMap<String, String> values = new HashMap<>();
            values.put("id", sp.getString("id", ""));
            values.put("password", sp.getString("password", ""));
            values.put("loginType", "auto");

            Log.d(TAG, "Saved id: " + sp.getString("id", ""));
            Log.d(TAG, "Saved password: " + sp.getString("password", ""));

            httpPostManager = new HttpPostManager(this, values, httpCallBack);
            httpPostManager.setMode(HttpPostManager.MODE_LOGIN);
            httpPostManager.execute();
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
                } else if (editPassword.validate()) {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("id", email);
                    values.put("password", password);
                    values.put("loginType", "normal");

                    httpPostManager = new HttpPostManager(LoginActivity.this, values, httpCallBack);
                    httpPostManager.setMode(HttpPostManager.MODE_LOGIN);
                    httpPostManager.execute();
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
                        Log.d(TAG, "onCompleted");
                        Log.d(TAG, object.toString());

                        try {
                            HashMap<String, String> values = new HashMap<>();

                            values.put("id", object.getString("id"));
                            values.put("loginType", "facebook");

                            httpPostManager = new HttpPostManager(LoginActivity.this, values, httpCallBack);
                            httpPostManager.setMode(HttpPostManager.MODE_LOGIN);
                            httpPostManager.execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        Log.d(TAG, "onActivityResult");
    }

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                Log.d(TAG, jsonObject.toString());
                if (jsonObject.getInt("resultCode") == Constant.SUCCESS) {
                    if (jsonObject.getString("mode").equals("normal")) {
                        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sp.edit();
                        spEditor.putString("id", jsonObject.getString("id"));
                        spEditor.putString("password", md5(editPassword.getText().toString()));

                        spEditor.apply();
                    }

                    User user = new User(jsonObject);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("loginUser", user);
                    startActivity(intent);

                    finish();
                } else if(jsonObject.getInt("resultCode") == Constant.LOGIN_FAILED){
                    if(jsonObject.getString("mode").equals("facebook")){
                        Profile profile = Profile.getCurrentProfile();
                        if(profile != null){
                            Intent intent = new Intent(LoginActivity.this, AddNickname.class);
                            startActivity(intent);
                        }
                    } else {
                        textError.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

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
