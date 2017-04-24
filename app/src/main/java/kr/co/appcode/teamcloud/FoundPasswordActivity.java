package kr.co.appcode.teamcloud;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class FoundPasswordActivity extends AppCompatActivity {
    private static final String TAG = "FoundPasswordActivity";

    private MaterialEditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_account);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editEmail = (MaterialEditText) findViewById(R.id.edit_email);
        editEmail.addValidator(new RegexpValidator("이메일 형식이 올바르지 않습니다", "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"));

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
            }
        });

        Button btnReissue = (Button) findViewById(R.id.btn_reissue);
        btnReissue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                String email = editEmail.getText().toString();

                if (email.length() == 0) {
                    editEmail.setError("사용중인 이메일을 입력해주세요");
                } else if (editEmail.validate()) {
                    /*HttpPostAsyncTask httpPost = new HttpPostAsyncTask();
                    httpPost.execute(email);*/
                }
            }
        });
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


   /* private class HttpPostAsyncTask extends AsyncTask<String, Void, Integer> {

        private URL url;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(FoundPasswordActivity.this);
            progressDialog.setMessage("잠시 기다려주세요...");
            progressDialog.show();
            try {
                url = new URL(Constant.SERVER_URL+"reissue.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String body = "id=" + params[0];

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

                return jsonObject.getInt("resultCode");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null) {
                if (result == 1) {
                    Toast.makeText(FoundPasswordActivity.this, "이메일로 새로운 비밀번호를 보내드렸습니다", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FoundPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (result == 2) {
                    editEmail.setError("이메일 형식이 올바르지 않습니다.");
                } else if (result == 3) {
                    editEmail.setError("회원으로 등록된 이메일이 아닙니다");
                } else if (result == 4) {
                    Snackbar.make(editEmail, "재발급 회수가 초과되었습니다. 다음에 이용해주세요", Snackbar.LENGTH_SHORT).show();
                } else if (result == -1) {
                    Snackbar.make(editEmail, "이메일 전송을 실패했습니다. 관리자에게 문의하세요", Snackbar.LENGTH_SHORT).show();
                } else if (result == -2) {
                    Snackbar.make(editEmail, "비밀번호 재발급을 실패했습니다. 관리자에게 문의하세요", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(editEmail, "데이터를 가져오지 못했습니다. 관리자에게 문의하세요", Snackbar.LENGTH_SHORT).show();
            }

            progressDialog.dismiss();
        }
    }*/
}
