package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangeTeamNameActivity extends AppCompatActivity {
    private static final String TAG = "ChangeTeamNameActivity";
    private static final int MODE_SAVE = 2;
    private static final int MODE_CHECK_TEAM_NAME = 8;

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

        final String originTeamName = getIntent().getStringExtra("teamName");

        editTeamName = (MaterialEditText) findViewById(R.id.edit_team_name);
        btnCheckTeamName = (Button) findViewById(R.id.btn_check_team_name);
        btnCheckTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCheckTeamName) {
                    Snackbar.make(v, "이미 팀 이름 중복확인을 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                } else if (editTeamName.length() == 0) {
                    editTeamName.setError("팀 이름을 입력해주세요.");
                } else if (editTeamName.getText().toString().toCharArray()[0] == ' ' || editTeamName.getText().toString().toCharArray()[editTeamName.length() - 1] == ' ') {
                    editTeamName.setError("팀 이름의 시작과 끝은 공백이 허용되지 않습니다.");
                } else {
                    HttpConnection httpConnection = new HttpConnection(ChangeTeamNameActivity.this, "teamName="+editTeamName.getText().toString(), "duplicateCheck.php", httpCallBack);
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
                } else{
                    HttpConnection httpConnection = new HttpConnection(ChangeTeamNameActivity.this, "modifiedTeamName="+editTeamName.getText().toString()+"&teamName="+originTeamName, "saveTeamSetting.php", httpCallBack);
                    httpConnection.execute();
                }
            }
        });

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

        editTeamName.setText(originTeamName);
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
                    if(mode == MODE_CHECK_TEAM_NAME){
                        isCheckTeamName = true;
                        btnCheckTeamName.setText("확인완료");
                        editTeamName.setHelperText("사용 가능한 팀 이름입니다.");
                        if(editTeamName.getError() != null){
                            editTeamName.setError(null);
                        }
                    } else if(mode == MODE_SAVE){
                        Toast.makeText(ChangeTeamNameActivity.this, "팀 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    if(mode == MODE_CHECK_TEAM_NAME){
                        editTeamName.setError("이미 사용중인 팀 이름입니다.");
                    } else if(mode == MODE_SAVE){
                        Toast.makeText(ChangeTeamNameActivity.this, "팀 이름 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
