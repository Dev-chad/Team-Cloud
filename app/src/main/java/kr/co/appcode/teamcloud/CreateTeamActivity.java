package kr.co.appcode.teamcloud;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.Profile;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateTeamActivity extends AppCompatActivity {
    private static final String TAG = "CreateTeamActivity";
    private static final int MODE_CHECK_TEAM_NAME = 2;
    private static final int MODE_CREATE_TEAM = 3;

    private MaterialEditText editTeamName;
    private EditText editCapacity;
    private SeekBar seekBarCapacity;

    private TextView textTeamPublic;
    private TextView textAutoJoin;

    private SwitchCompat switchPublic;
    private SwitchCompat switchJoin;

    private Button btnCheckTeamName;

    private boolean isCheckTeamName;
    private boolean isSetCapacity = true;

    private HttpConnection httpConnection;

    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        user = getIntent().getParcelableExtra("login_user");
        if (user != null) {
            if (user.getAccountType().equals("facebook")) {
                if( Profile.getCurrentProfile() == null){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        switchJoin = (SwitchCompat) findViewById(R.id.switch_join);
        switchJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textAutoJoin.setText("켜짐");
                    textAutoJoin.setTextColor(ContextCompat.getColor(CreateTeamActivity.this, R.color.red));
                } else {
                    textAutoJoin.setText("꺼짐");
                    textAutoJoin.setTextColor(ContextCompat.getColor(CreateTeamActivity.this, R.color.gray));
                }
            }
        });

        switchPublic = (SwitchCompat) findViewById(R.id.switch_public);
        switchPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textTeamPublic.setText("공개");
                    textTeamPublic.setTextColor(ContextCompat.getColor(CreateTeamActivity.this, R.color.red));
                } else {
                    textTeamPublic.setText("비공개");
                    textTeamPublic.setTextColor(ContextCompat.getColor(CreateTeamActivity.this, R.color.gray));
                }
            }
        });

        editTeamName = (MaterialEditText) findViewById(R.id.edit_team_name);
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
                }
            }
        });

        editCapacity = (EditText) findViewById(R.id.edit_capacity);
        editCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    isSetCapacity = true;
                    int value = Integer.valueOf(s.toString());

                    if (value > user.getAvailableCapacity()) {
                        editCapacity.setText(String.valueOf(user.getAvailableCapacity()));
                        value = user.getAvailableCapacity();
                    }

                    seekBarCapacity.setProgress(value);
                } else {
                    isSetCapacity = false;
                }
            }
        });

        btnCheckTeamName = (Button) findViewById(R.id.btn_check_team_name);
        btnCheckTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (isCheckTeamName) {
                    Snackbar.make(v, "이미 팀 이름 중복확인을 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                } else if (editTeamName.length() == 0) {
                    editTeamName.setError("팀 이름을 입력해주세요.");
                } else {
                    String body = "teamName="+editTeamName.getText().toString();

                    httpConnection = new HttpConnection(CreateTeamActivity.this, body, "duplicateCheck.php", httpCallback);
                    httpConnection.execute();
                }
            }
        });

        Button btnCreateTeam = (Button) findViewById(R.id.btn_create_team);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();

                if (!isCheckTeamName) {
                    editTeamName.setError("팀 이름 중복확인을 해주세요.");
                } else if (!isSetCapacity) {
                    Snackbar.make(v, "용량 설정 값이 올바르지 않습니다.", Snackbar.LENGTH_LONG).show();
                } else {
                    String body = "teamName="+editTeamName.getText().toString()+"&master="+user.getNickname()+"&maxCapacity="+editCapacity.getText().toString()+"&isPublic="+String.valueOf(switchPublic.isChecked())+"&isAutoJoin="+String.valueOf(switchJoin.isChecked());

                    httpConnection = new HttpConnection(CreateTeamActivity.this, body, "createTeam.php", httpCallback);
                    httpConnection.execute();
                }
            }
        });

        seekBarCapacity = (SeekBar) findViewById(R.id.seekbar_capacity);
        seekBarCapacity.setMax(user.getAvailableCapacity());
        seekBarCapacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    if (progress == 0) {
                        progress = 1;
                        seekBar.setProgress(progress);
                    }

                    editCapacity.setText(String.valueOf(progress));
                    isSetCapacity = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        TextView textMaxCapacity = (TextView) findViewById(R.id.text_max_capacity);
        textMaxCapacity.setText(String.valueOf(user.getAvailableCapacity() + " GB"));

        textAutoJoin = (TextView) findViewById(R.id.text_is_auto_join);
        textTeamPublic = (TextView) findViewById(R.id.text_is_public);
    }

    private HttpCallBack httpCallback = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                Log.d(TAG, jsonObject.toString());
                if (mode == MODE_CHECK_TEAM_NAME) {
                    if (resultCode == Constant.SUCCESS) {
                        isCheckTeamName = true;
                        btnCheckTeamName.setText("확인완료");
                    } else if (resultCode == Constant.DUPLICATED) {
                        editTeamName.setError("이미 사용중인 팀 이름 입니다.");
                    } else if (resultCode == Constant.FAILED) {
                        Log.d(TAG, "mode: TeamNameCheck desc: Query error");
                    }
                } else if (mode == MODE_CREATE_TEAM) {
                    if (resultCode == Constant.SUCCESS) {
                        finish();
                    } else if (resultCode == Constant.DUPLICATED) {
                        editTeamName.setError("팀 이름 중복확인을 다시 진행해 주세요.");
                        isCheckTeamName = false;
                    } else if (resultCode == -1) {
                        Log.d(TAG, "mode: CreateTeam desc: Query error");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closingSoftKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
