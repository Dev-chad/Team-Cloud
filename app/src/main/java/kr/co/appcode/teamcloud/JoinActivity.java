package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class JoinActivity extends AppCompatActivity {

    //region Constant
    private final String TAG = "JoinActivity";

    private final int JOIN_EMPTY_EMAIL = 2;
    private final int JOIN_DUPLICATE_EMAIL = 3;
    private final int JOIN_EMPTY_PASSWORD = 4;
    private final int JOIN_EMPTY_NICKNAME = 5;
    private final int JOIN_DUPLICATE_NICKNAME = 6;
    private final int JOIN_EMPTY_NAME = 7;
    private final int JOIN_QUERY_ERROR = -1;

    private final int DUPLICATED = 1;
    private final int NOT_DUPLICATED = 2;

    private final int MODE_CHECK_EMAIL = 1;
    private final int MODE_CHECK_NICKNAME = 2;
    private final int MODE_JOIN_SUBMIT = 3;
    //endregion

    private TextInputLayout layoutEditEmail;
    private TextInputLayout layoutEditPassword;
    private TextInputLayout layoutEditCheckPassword;
    private TextInputLayout layoutEditNickname;
    private TextInputLayout layoutEditName;

    private EditText editEmail;
    private EditText editPassword;
    private EditText editCheckPassword;
    private EditText editNickname;
    private EditText editName;

    private Button btnCheckEmail;
    private Button btnCheckNickname;

    private boolean isCheckedEmail = false;
    private boolean isCheckedNickname = false;
    private boolean isSamePassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        layoutEditEmail = (TextInputLayout) findViewById(R.id.layout_editEmail);
        layoutEditPassword = (TextInputLayout) findViewById(R.id.layout_editPassword);
        layoutEditCheckPassword = (TextInputLayout) findViewById(R.id.layout_editCheckPassword);
        layoutEditNickname = (TextInputLayout) findViewById(R.id.layout_editNickname);
        layoutEditName = (TextInputLayout) findViewById(R.id.layout_editName);

        editEmail = (EditText) findViewById(R.id.edit_email);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editCheckPassword = (EditText) findViewById(R.id.edit_check_password);
        editNickname = (EditText) findViewById(R.id.edit_nickname);
        editName = (EditText) findViewById(R.id.edit_name);

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
                if (!editPassword.getText().toString().equals(s.toString())) {
                    layoutEditCheckPassword.setError("비밀번호가 같지 않습니다.");
                    isSamePassword = false;
                } else {
                    layoutEditCheckPassword.setErrorEnabled(false);
                    isSamePassword = true;
                }
            }
        });

        btnCheckEmail = (Button) findViewById(R.id.btn_check_email);
        btnCheckEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();

                if (email.length() >= 3 && email.length() <= 15) {

                    HashMap<String, String> values = new HashMap<>();
                    values.put("email", email);

                    int resultCode = httpPost(values, MODE_CHECK_EMAIL);

                    if (resultCode == DUPLICATED) {
                        layoutEditEmail.setError("이미 등록된 이메일입니다.");
                    } else {
                        btnCheckEmail.setText("인증완료");
                        isCheckedEmail = true;

                        if (layoutEditEmail.isErrorEnabled()) {
                            layoutEditEmail.setErrorEnabled(false);
                        }
                    }
                } else {
                    layoutEditEmail.setError("이메일 형식이 올바르지 않습니다..");
                }
            }
        });

        btnCheckNickname = (Button) findViewById(R.id.btn_check_nickname);
        btnCheckNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editNickname.getText().toString();

                if (nickname.length() >= 3 && nickname.length() <= 15) {
                    HashMap<String, String> values = new HashMap<>();
                    values.put("nickname", nickname);

                    int resultCode = httpPost(values, MODE_CHECK_NICKNAME);

                    if (resultCode == DUPLICATED) {
                        layoutEditNickname.setError("이미 등록된 닉네임입니다.");
                    } else {
                        btnCheckNickname.setText("확인완료");
                        isCheckedNickname = true;

                        if (layoutEditNickname.isErrorEnabled()) {
                            layoutEditNickname.setErrorEnabled(false);
                        }
                    }
                } else {
                    layoutEditNickname.setError("닉네임은 3글자 이상 15글자 이하로 입력해주세요.");
                }
            }
        });

        Button btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initErrorMsg();

                if (!isCheckedEmail) {
                    layoutEditEmail.setError("이메일 인증을 해주세요.");
                } else if (editPassword.length() == 0) {
                    layoutEditPassword.setError("비밀번호를 입력해주세요.");
                } else if (editCheckPassword.length() == 0) {
                    layoutEditCheckPassword.setError("비밀번호 확인란을 입력해주세요.");
                } else if (!isSamePassword) {
                    layoutEditCheckPassword.setError("비밀번호가 같지 않습니다.");
                } else if (!isCheckedNickname) {
                    layoutEditNickname.setError("닉네임 중복검사를 해주세요.");
                } else if (editName.length() == 0) {
                    layoutEditName.setError("이름을 입력해주세요.");
                } else {
                    HashMap<String, String> values = new HashMap<>();

                    values.put("email", editEmail.getText().toString());
                    values.put("password", editPassword.getText().toString());
                    values.put("nickname", editNickname.getText().toString());
                    values.put("name", editNickname.getText().toString());

                    httpPost(values, MODE_JOIN_SUBMIT);
                }
            }
        });
    }

    private int httpPost(HashMap<String, String> values, int mode) {
        HttpPostThread httpPostThread = null;
        String url = "http://appcode.co.kr/TeamCloud/";

        if (mode == MODE_JOIN_SUBMIT) {
            url += "join.php";
        } else {
            url += "duplicateCheck.php";
        }

        try {
            httpPostThread = new HttpPostThread(values, new URL(url), mode);
            httpPostThread.start();
            httpPostThread.join();
        } catch (InterruptedException | MalformedURLException e) {
            e.printStackTrace();
        }

        if (httpPostThread != null) {
            return httpPostThread.getResultCode();
        } else {
            return 0;
        }
    }

    /*private boolean validCheck(int resultCode){

    }*/

    private void initErrorMsg() {
        layoutEditEmail.setErrorEnabled(false);
        layoutEditPassword.setErrorEnabled(false);
        layoutEditCheckPassword.setErrorEnabled(false);
        layoutEditNickname.setErrorEnabled(false);
        layoutEditName.setErrorEnabled(false);
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
                String body = "";

                if (mode == MODE_CHECK_EMAIL) {
                    body = "email=" + values.get("email");
                } else if (mode == MODE_CHECK_NICKNAME) {
                    body = "nickname=" + values.get("nickname");
                } else if (mode == MODE_JOIN_SUBMIT) {
                    body = "email=" + values.get("email") + "&password=" + values.get("password") + "&nickname=" + values.get("nickname") + "&name=" + values.get("name");
                }

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getResultCode() {
            return resultCode;
        }
    }
}
