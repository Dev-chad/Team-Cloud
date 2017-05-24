package kr.co.appcode.teamcloud;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeTeamNameActivity extends AppCompatActivity {
    private static final String TAG = "ChangeTeamNameActivity";
    private static final int MODE_SAVE = 1;
    private static final int MODE_CHECK_TEAM_NAME = 2;

    private MaterialEditText editTeamName;
    private Button btnCheckTeamName;
    private Button btnChange;

    private boolean isCheckTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_team_name);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        final Team team = getIntent().getParcelableExtra("team");

        btnCheckTeamName = (Button) findViewById(R.id.btn_check_team_name);
        btnCheckTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (isCheckTeamName) {
                    Snackbar.make(v, "이미 팀 이름 중복확인을 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                } else if (editTeamName.length() == 0) {
                    editTeamName.setError("팀 이름을 입력해주세요.");
                } else if (editTeamName.getText().toString().toCharArray()[0] == ' ' || editTeamName.getText().toString().toCharArray()[editTeamName.length() - 1] == ' ') {
                    editTeamName.setError("팀 이름의 시작과 끝은 공백이 허용되지 않습니다.");
                } else {
                    String body = "teamName=" + editTeamName.getText().toString();
                    HttpConnection httpConnection = new HttpConnection(ChangeTeamNameActivity.this, body, "duplicateCheck.php", httpCallBack);
                    httpConnection.execute();
                }
            }
        });

        btnChange = (Button) findViewById(R.id.btn_change);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckTeamName) {
                    editTeamName.setError("팀 이름 중복확인을 해주세요.");
                } else {
                    String body = "modifiedTeamName=" + editTeamName.getText().toString() + "&teamIdx=" + team.getIdx();
                    HttpConnection httpConnection = new HttpConnection(ChangeTeamNameActivity.this, body, "saveTeamSetting.php", httpCallBack);
                    httpConnection.execute();
                }
            }
        });

        editTeamName = (MaterialEditText) findViewById(R.id.edit_team_name);
        editTeamName.setText(team.getName());
        editTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (s.toString().toCharArray()[0] == ' ' || s.toString().toCharArray()[s.length() - 1] == ' ') {
                        editTeamName.setError("팀 이름의 시작과 끝은 공백이 허용되지 않습니다.");
                    } else {
                        editTeamName.setError(null);
                    }
                }

                if (isCheckTeamName) {
                    isCheckTeamName = false;
                    btnCheckTeamName.setText("중복확인");
                    editTeamName.setHelperText(null);
                }
            }
        });
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                int mode = jsonObject.getInt("mode");

                if (resultCode == Constant.SUCCESS) {
                    if (mode == MODE_CHECK_TEAM_NAME) {
                        isCheckTeamName = true;
                        btnCheckTeamName.setText("확인완료");
                        editTeamName.setHelperText("사용 가능한 팀 이름입니다.");
                        if (editTeamName.getError() != null) {
                            editTeamName.setError(null);
                        }
                    } else if (mode == MODE_SAVE) {
                        Toast.makeText(ChangeTeamNameActivity.this, "팀 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    if (mode == MODE_CHECK_TEAM_NAME) {
                        editTeamName.setError("이미 사용중인 팀 이름입니다.");
                    } else if (mode == MODE_SAVE) {
                        Toast.makeText(ChangeTeamNameActivity.this, "팀 이름 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
