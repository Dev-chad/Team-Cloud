package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class JoinActivity extends AppCompatActivity {
    private static final String TAG = "JoinActivity";

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
    private boolean isRunningEmailCheck;

    private int authCode;

    //    TimerAsyncTask timerAsyncTask = new TimerAsyncTask();
    TimerThread timerThread = new TimerThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        layoutEmailAuth = (LinearLayout) findViewById(R.id.layout_emailAuth);

        editEmail = (MaterialEditText) findViewById(R.id.edit_email);
        editEmail.addValidator(new RegexpValidator("이메일을 입력해주세요.", "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"));

        editPassword = (MaterialEditText) findViewById(R.id.edit_password);
        editPassword.addValidator(new RegexpValidator("최소 1개의 숫자 혹은 특수문자를 포함한 6~20자리 문자", "^(?=.*[0-9])(?=.*[a-z])(?=[\\S]+$).{6,20}$"));

        editCheckPassword = (MaterialEditText) findViewById(R.id.edit_check_password);
        editNickname = (MaterialEditText) findViewById(R.id.edit_nickname);
        editNickname.addValidator(new RegexpValidator("4~15자리의 영문, 숫자만 가능", "^[a-z0-9]{4,15}$"));

        editName = (MaterialEditText) findViewById(R.id.edit_name);
        editName.addValidator(new RegexpValidator("최소 2자 최대 20자", "^[가-힣a-zA-Z]{2,10}$"));

        editAuth = (EditText) findViewById(R.id.edit_auth);

        textTime = (TextView) findViewById(R.id.text_time);

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

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

                if (s.length() == 0) {
                    editEmail.setError(null);
                }
            }

        });

        editNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isCheckedNickname) {
                    btnCheckNickname.setText("중복확인");
                    isCheckedNickname = false;
                }

                if (s.length() == 0) {
                    editNickname.setError(null);
                }
            }
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    editPassword.setError(null);
                }
            }
        });

        editCheckPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    editCheckPassword.setError(null);
                } else if (!s.toString().equals(editPassword.getText().toString())) {
                    isSamePassword = false;
                    editCheckPassword.setError("비밀번호가 같지 않습니다.");
                } else if (s.length() > 0 && s.toString().equals(editPassword.getText().toString())) {
                    isSamePassword = true;
                    editCheckPassword.setError(null);
                }
            }
        });

        btnCheckEmail = (Button)

                findViewById(R.id.btn_check_email);
        btnCheckEmail.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (!isCheckedEmail) {
                    if (editEmail.validate()) {
                        String email = editEmail.getText().toString();

                        setAuthCode();
                        HashMap<String, String> values = new HashMap<>();
                        values.put("email", email);

                        HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(JoinActivity.this, values, Constant.MODE_AUTH_EMAIL);
                        httpPostAsyncTask.execute();
                    }
                } else {
                    Snackbar.make(v, "이미 이메일 인증을 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnCheckNickname = (Button)

                findViewById(R.id.btn_check_nickname);
        btnCheckNickname.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (!isCheckedNickname) {
                    String nickname = editNickname.getText().toString();

                    if (editNickname.validate()) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("nickname", nickname);

                        HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(JoinActivity.this, values, Constant.MODE_CHECK_NICKNAME);
                        httpPostAsyncTask.execute();

                    } else {

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
                    if (editAuth.getText().toString().equals(String.valueOf(authCode))) {
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
                    editEmail.setError("이메일 인증을 해주세요");
                } else if (editPassword.length() == 0) {
                    editPassword.setError("비밀번호를 입력해주세요");
                } else if (!editPassword.validate()) {

                } else if (editCheckPassword.length() == 0) {
                    editCheckPassword.setError("비밀번호 확인을 입력해주세요");
                } else if (!isSamePassword) {
                    editCheckPassword.setError("비밀번호가 같지 않습니다.");
                } else if (!isCheckedNickname) {
                    editNickname.setError("닉네임 중복검사를 해주세요");
                } else if (!editName.validate()) {

                } else {
                    HashMap<String, String> values = new HashMap<>();

                    values.put("email", editEmail.getText().toString());
                    values.put("password", editPassword.getText().toString());
                    values.put("nickname", editNickname.getText().toString());
                    values.put("name", editName.getText().toString());

                    HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask(JoinActivity.this, values, Constant.MODE_JOIN_SUBMIT);
                    httpPostAsyncTask.execute();
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
            second = 30;
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
                } else {
                    textTime.setText(msg.getData().getString("time"));
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

    private class HttpPostAsyncTask extends AsyncTask<Void, Void, Integer> {

        private int mode;
        private HashMap<String, String> values;
        private String body;
        private URL url;
        private Context context;
        private ProgressDialog progressDialog;

        private HttpPostAsyncTask(Context context, HashMap<String, String> values, int mode) {
            super();
            this.values = values;
            this.mode = mode;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.show();

            if (mode == Constant.MODE_AUTH_EMAIL) {
                body = "email=" + values.get("email") + "&auth_code=" + authCode;
            } else if (mode == Constant.MODE_CHECK_NICKNAME) {
                body = "nickname=" + values.get("nickname");
            } else if (mode == Constant.MODE_JOIN_SUBMIT) {
                body = "email=" + values.get("email") + "&password=" + values.get("password") + "&nickname=" + values.get("nickname") + "&name=" + values.get("name");
            }

            try {
                url = new URL("http://appcode.co.kr/TeamCloud/join.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
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

                return Integer.valueOf(br.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if (mode == Constant.MODE_AUTH_EMAIL) {
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
                    /*if (timerAsyncTask != null && timerAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                        timerAsyncTask.cancel(false);
                    }
                    timerAsyncTask = new TimerAsyncTask();
                    timerAsyncTask.execute();*/
                }
            } else if (mode == Constant.MODE_CHECK_NICKNAME) {
                if (result == Constant.DUPLICATED) {
                    editNickname.setError("이미 등록된 닉네임입니다.");
                } else {
                    btnCheckNickname.setText("확인완료");
                    isCheckedNickname = true;


                }
            } else if (mode == Constant.MODE_JOIN_SUBMIT) {
                if (result == Constant.JOIN_COMPLETE) {
                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    Toast.makeText(JoinActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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