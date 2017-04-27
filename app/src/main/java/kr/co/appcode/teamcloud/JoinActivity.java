package kr.co.appcode.teamcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class JoinActivity extends AppCompatActivity {
    //region constant
    private static final String TAG = "JoinActivity";

    public static final int JOIN_EMPTY_EMAIL = 2;
    public static final int JOIN_INVALID_EMAIL = 3;
    public static final int JOIN_DUPLICATE_EMAIL = 4;
    public static final int JOIN_EMPTY_PASSWORD = 5;
    public static final int JOINT_INVALID_PASSWORD = 6;
    public static final int JOIN_EMPTY_NICKNAME = 7;
    public static final int JOIN_INVALID_NICKNAME = 8;
    public static final int JOIN_DUPLICATE_NICKNAME = 9;
    public static final int JOIN_EMPTY_NAME = 10;
    public static final int JOIN_INVALID_NAME = 11;

    //endregion
    private LinearLayout layoutEmailAuth;

    private MaterialEditText editEmail;
    private MaterialEditText editPassword;
    private MaterialEditText editCheckPassword;
    private MaterialEditText editNickname;
    private MaterialEditText editName;
    private EditText editAuth;

    private TextView textTime;

    private Button btnCheckEmail;
    private Button btnCheckNickname;

    private boolean isCheckedEmail;
    private boolean isCheckedNickname;
    private boolean isSamePassword;

    private HttpPostManager httpPostManager;

    private int authCode;

    //    TimerAsyncTask timerAsyncTask = new TimerAsyncTask();
    TimerThread timerThread = new TimerThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        layoutEmailAuth = (LinearLayout) findViewById(R.id.layout_emailAuth);

        editEmail = (MaterialEditText) findViewById(R.id.edit_email);
        editEmail.addValidator(new RegexpValidator("이메일 형식이 올바르지 않습니다.", "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"));

        editPassword = (MaterialEditText) findViewById(R.id.edit_password);
        editPassword.addValidator(new RegexpValidator("영문, 숫자, 특수문자를 포함한 6~20자의 문자.", "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,20}$"));

        editCheckPassword = (MaterialEditText) findViewById(R.id.edit_check_password);
        editNickname = (MaterialEditText) findViewById(R.id.edit_nickname);
        editNickname.addValidator(new RegexpValidator("영문을 포함한 4~15자의 숫자, _, - 가능.", "^(?=.*[a-zA-Z])([_a-z0-9-]){4,15}$"));
        editName = (MaterialEditText) findViewById(R.id.edit_name);
        editName.addValidator(new RegexpValidator("2~20자의 한글 또는 영문만 가능.", "^[a-zA-Z가-힛]{2,20}$"));

        editAuth = (EditText) findViewById(R.id.edit_auth);

        textTime = (TextView) findViewById(R.id.text_time);

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
                if (isCheckedEmail) {
                    btnCheckEmail.setText("인증");
                    isCheckedEmail = false;
                } else if (layoutEmailAuth.getVisibility() != View.GONE) {
                    layoutEmailAuth.setVisibility(View.GONE);
                    if (timerThread.isAlive()) {
                        timerThread.setStop();
                    }
                }

                if (btnCheckEmail.getText().toString().equals("재전송")) {
                    btnCheckEmail.setText("인증");
                }

                if (s.length() == 0) {
                    editEmail.setError(null);
                }
            }

        });

        editNickname.addTextChangedListener(new TextWatcher() {

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
                if (isCheckedNickname) {
                    btnCheckNickname.setText("중복확인");
                    isCheckedNickname = false;
                }

                if (editNickname.validate()) {
                    editNickname.setHelperText(null);
                } else {
                    if (editNickname.getHelperText() == null) {
                        editNickname.setHelperText("4~15자리의 영문, 숫자만 가능");
                    }
                }

                if (s.length() == 0) {
                    editNickname.setError(null);

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
                } else if (editPassword.validate()) {
                    editPassword.setHelperText(null);
                } else {
                    editPassword.setHelperText("영문, 숫자, 특수문자를 포함한 6~20자리 문자");
                }

                if (editCheckPassword.length() > 0) {
                    if (s.toString().equals(editCheckPassword.getText().toString())) {
                        isSamePassword = true;
                        editCheckPassword.setError(null);
                    } else {
                        isSamePassword = false;
                        editCheckPassword.setError("비밀번호가 같지 않습니다.");
                    }
                }
            }
        });

        editCheckPassword.addTextChangedListener(new TextWatcher() {

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
                    editCheckPassword.setError(null);
                } else if (!s.toString().equals(editPassword.getText().toString())) {
                    if (isSamePassword) {
                        isSamePassword = false;
                    }
                    editCheckPassword.setError("비밀번호가 같지 않습니다.");
                } else {
                    Log.d(TAG, "same");
                    isSamePassword = true;
                    editCheckPassword.setError(null);
                }
            }
        });

        editName.addTextChangedListener(new TextWatcher() {

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
                    editName.setError(null);
                } else if (editName.validate()) {
                    editName.setHelperText(null);
                } else {
                    if (editName.getHelperText() == null) {
                        editName.setHelperText("2~20자의 한글 또는 영문만 가능");
                    }
                }
            }
        });

        btnCheckEmail = (Button) findViewById(R.id.btn_check_email);
        btnCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (!isCheckedEmail) {
                    if (editEmail.length() == 0) {
                        editEmail.setError("이메일을 입력해주세요.");
                    } else if (editEmail.validate()) {
                        if (timerThread.isAlive()) {
                            Snackbar.make(v, "인증 메일을 다시 보내시겠습니까?", Snackbar.LENGTH_LONG)
                                    .setAction("Ok", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String email = editEmail.getText().toString();

                                            setAuthCode();
                                            HashMap<String, String> values = new HashMap<>();
                                            values.put("email", email);

                                            httpPostManager = new HttpPostManager(JoinActivity.this, values, httpCallBack);
                                            httpPostManager.setMode(HttpPostManager.MODE_AUTH_EMAIL);
                                            httpPostManager.execute();
                                        }
                                    }).show();
                        } else {
                            String email = editEmail.getText().toString();

                            setAuthCode();
                            HashMap<String, String> values = new HashMap<>();
                            values.put("id", email);
                            values.put("authCode", String.valueOf(authCode));

                            httpPostManager = new HttpPostManager(JoinActivity.this, values, httpCallBack);
                            httpPostManager.setMode(HttpPostManager.MODE_AUTH_EMAIL);
                            httpPostManager.execute();
                        }

                        if (editAuth.length() > 0) {
                            editAuth.setText("");
                        }

                        if (btnCheckEmail.getText().toString().equals("재전송")) {
                            btnCheckEmail.setText("인증");
                        }
                    }
                } else {
                    Snackbar.make(v, "이미 이메일 인증을 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnCheckNickname = (Button) findViewById(R.id.btn_check_nickname);
        btnCheckNickname.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (!isCheckedNickname) {
                    String nickname = editNickname.getText().toString();

                    if (editNickname.length() == 0) {
                        editNickname.setError("닉네임을 입력해주세요.");
                    } else if (editNickname.validate()) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("nickname", nickname);

                        httpPostManager = new HttpPostManager(JoinActivity.this, values, httpCallBack);
                        httpPostManager.setMode(HttpPostManager.MODE_NICKNAME_CHECK);
                        httpPostManager.execute();
                    } else {
                        editNickname.setError("닉네임이 올바르지 않습니다.");
                    }
                } else {
                    Snackbar.make(v, "이미 닉네임 중복검사를 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        Button btnAuth = (Button) findViewById(R.id.btn_auth);
        btnAuth.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (timerThread.isAlive()) {
                    if (editAuth.length() == 0) {
                        Snackbar.make(v, "인증번호를 입력해주세요.", Snackbar.LENGTH_SHORT).show();
                    } else if (editAuth.getText().toString().equals(String.valueOf(authCode))) {
                        isCheckedEmail = true;
                        editAuth.setText("");
                        layoutEmailAuth.setVisibility(View.GONE);
                        btnCheckEmail.setText("인증완료");
                        if (timerThread.isAlive()) {
                            timerThread.setStop();
                        }
                    } else {
                        Snackbar.make(v, "인증번호가 올바르지 않습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "인증시간이 초과되었습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        Button btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isCheckedEmail) {
                    editEmail.setError("이메일 인증을 해주세요.");
                } else if (editPassword.length() == 0) {
                    editPassword.setError("비밀번호를 입력해주세요.");
                } else if (!editPassword.validate()) {
                    editPassword.setError("비밀번호 형식이 올바르지 않습니다.");
                } else if (editCheckPassword.length() == 0) {
                    editCheckPassword.setError("비밀번호 확인을 입력해주세요.");
                } else if (!isSamePassword) {
                    editCheckPassword.setError("비밀번호가 같지 않습니다.");
                } else if (!isCheckedNickname) {
                    editNickname.setError("닉네임 중복검사를 해주세요.");
                } else if (editName.length() == 0) {
                    editName.setError("이름을 입력해주세요.");
                } else if (!editName.validate()) {
                    editName.setError("이름이 올바르지 않습니다.");
                } else {
                    HashMap<String, String> values = new HashMap<>();

                    values.put("id", editEmail.getText().toString());
                    values.put("password", editPassword.getText().toString());
                    values.put("nickname", editNickname.getText().toString());
                    values.put("name", editName.getText().toString());
                    values.put("joinType", "teamcloud");

                    httpPostManager = new HttpPostManager(JoinActivity.this, values, httpCallBack);
                    httpPostManager.setMode(HttpPostManager.MODE_JOIN);
                    httpPostManager.execute();
                }
            }
        });
    }

    private class TimerThread extends Thread {
        private int minute;
        private int second;
        private boolean isStop;

        public void setStop() {
            isStop = true;
        }

        @Override
        public void run() {
            Bundle data = new Bundle();
            Message msg = handler.obtainMessage();

            minute = 0;
            second = 15;
            handler.sendEmptyMessage(0);
            msg.what = 1;
            while ((minute > 0 || second > 0) && !isStop) {
                try {
                    sleep(1000);
                    if (second == 0) {
                        minute--;
                        second = 59;
                    } else {
                        second--;
                    }
                    msg = handler.obtainMessage();
                    msg.what = 1;
                    data.putString("time", minute + ":" + String.format(Locale.KOREAN, "%02d", second));
                    msg.setData(data);

                    if (!isStop) {
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!isStop) {
                setAuthCode();
                handler.sendEmptyMessage(2);
            }
        }

        // TODO: this handler can cause a memory leak.

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    if (layoutEmailAuth.getVisibility() == View.GONE) {
                        layoutEmailAuth.setVisibility(View.VISIBLE);
                    }
                    textTime.setText("3:00");
                } else if (msg.what == 1) {
                    textTime.setText(msg.getData().getString("time"));
                } else {
                    btnCheckEmail.setText("재전송");
                    Snackbar.make(btnCheckEmail, "인증시간이 초과되었습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void setAuthCode() {
        Random rand = new Random();
        int start = 100000;
        int end = 999999;
        double range = end - start + 1;

        authCode = (int) (rand.nextDouble() * range + start);
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int result = jsonObject.getInt("resultCode");

                if (mode == HttpPostManager.MODE_AUTH_EMAIL) {
                    if (result == Constant.DUPLICATED) {
                        editEmail.setError("이미 등록된 이메일입니다.");
                    } else {
                        Snackbar.make(btnCheckEmail, "이메일을 발송했습니다. 인증번호를 확인해주세요.", Snackbar.LENGTH_SHORT).show();
                        if (timerThread.isAlive()) {
                            timerThread.setStop();
                            Log.d(TAG, "Stopped timer thread.");
                        }
                        timerThread = new TimerThread();
                        timerThread.start();
                    }
                } else if (mode == HttpPostManager.MODE_NICKNAME_CHECK) {
                    if (result == Constant.DUPLICATED) {
                        editNickname.setError("이미 등록된 닉네임입니다.");
                    } else {
                        btnCheckNickname.setText("확인완료");
                        isCheckedNickname = true;
                    }
                } else if (mode == HttpPostManager.MODE_JOIN) {
                    if (result == Constant.SUCCESS) {
                        Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                        Toast.makeText(JoinActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void closingSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}