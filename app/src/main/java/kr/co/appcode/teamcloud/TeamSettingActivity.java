package kr.co.appcode.teamcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class TeamSettingActivity extends AppCompatActivity {
    private static final String TAG = "TeamSettingActivity";
    private static final int MODE_GET_TEAM_INFO = 1;
    private static final int MODE_SAVE = 2;

    private ImageView imageTeamMark;

    private Button btnChangeTeamMark;

    private RelativeLayout layoutChangeTeamName;
    private RelativeLayout layoutManageMemeber;
    private RelativeLayout layoutManageBoard;
    private RelativeLayout layoutDissolution;

    private SwitchCompat switchPublicTeam;
    private SwitchCompat switchAutoJoin;
    private SwitchCompat switchManageMember;
    private SwitchCompat switchManageBoard;
    private SwitchCompat switchManageContents;

    private TextView textIsPublic;
    private TextView textIsJoin;
    private TextView textIsManageMember;
    private TextView textIsManageBoard;
    private TextView textIsManageContents;
    private TextView textTeamName;

    private String teamName;

    boolean isPublic;
    boolean isAutoJoin;
    boolean isManageMember;
    boolean isManageBoard;
    boolean isManageContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_setting);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        teamName = getIntent().getStringExtra("teamName");

        imageTeamMark = (ImageView) findViewById(R.id.image_team_mark);

        btnChangeTeamMark = (Button) findViewById(R.id.btn_team_mark_change);

        layoutChangeTeamName = (RelativeLayout) findViewById(R.id.layout_changeTeamName);
        layoutManageMemeber = (RelativeLayout) findViewById(R.id.layout_manage_member);
        layoutManageBoard = (RelativeLayout) findViewById(R.id.layout_manage_board);
        layoutDissolution = (RelativeLayout) findViewById(R.id.layout_dissolution);

        switchPublicTeam = (SwitchCompat) findViewById(R.id.switch_public);
        switchAutoJoin = (SwitchCompat) findViewById(R.id.switch_join);
        switchManageMember = (SwitchCompat) findViewById(R.id.switch_manage_member);
        switchManageBoard = (SwitchCompat) findViewById(R.id.switch_manage_board);
        switchManageContents = (SwitchCompat) findViewById(R.id.switch_manage_contents);

        textIsPublic = (TextView) findViewById(R.id.text_is_public);
        textIsJoin = (TextView) findViewById(R.id.text_is_auto_join);
        textIsManageMember = (TextView) findViewById(R.id.text_is_manage_member);
        textIsManageBoard = (TextView) findViewById(R.id.text_is_manage_board);
        textIsManageContents = (TextView) findViewById(R.id.text_is_manage_contents);
        textTeamName = (TextView) findViewById(R.id.text_team_name);

        textTeamName.setText(teamName);

        btnChangeTeamMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutChangeTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamSettingActivity.this, ChangeTeamNameActivity.class);
                intent.putExtra("teamName", teamName);
                startActivity(intent);
            }
        });

        layoutManageMemeber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutManageBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutDissolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        switchPublicTeam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsPublic.setText("공개");
                    textIsPublic.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.red));
                } else {
                    textIsPublic.setText("비공개");
                    textIsPublic.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.gray));
                }
            }
        });

        switchAutoJoin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsJoin.setText("켜짐");
                    textIsJoin.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.red));
                } else {
                    textIsJoin.setText("꺼짐");
                    textIsJoin.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.gray));
                }
            }
        });

        switchManageMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageMember.setText("켜짐");
                    textIsManageMember.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.red));
                } else {
                    textIsManageMember.setText("꺼짐");
                    textIsManageMember.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.gray));
                }
            }
        });

        switchManageBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageBoard.setText("켜짐");
                    textIsManageBoard.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.red));
                } else {
                    textIsManageBoard.setText("꺼짐");
                    textIsManageBoard.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.gray));
                }
            }
        });

        switchManageContents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageContents.setText("켜짐");
                    textIsManageContents.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.red));
                } else {
                    textIsManageContents.setText("꺼짐");
                    textIsManageContents.setTextColor(ContextCompat.getColor(TeamSettingActivity.this, R.color.gray));
                }
            }
        });

        HttpConnection httpConnection = new HttpConnection(this, "teamName=" + teamName, "getTeamInfo.php", httpCallBack);
        httpConnection.execute();
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                int mode = jsonObject.getInt("mode");

                if (resultCode == Constant.SUCCESS) {
                    if (mode == MODE_GET_TEAM_INFO) {
                        if (jsonObject.getInt("isPublic") == 1) {
                            switchPublicTeam.setChecked(true);
                            isPublic = true;
                        } else {
                            switchPublicTeam.setChecked(false);
                            isPublic = false;
                        }

                        if (jsonObject.getInt("isAutoJoin") == 1) {
                            switchAutoJoin.setChecked(true);
                            isAutoJoin = true;
                        } else {
                            switchAutoJoin.setChecked(false);
                            isAutoJoin = false;
                        }

                        if (jsonObject.getInt("isManageMember") == 1) {
                            switchManageMember.setChecked(true);
                            isManageMember = true;
                        } else {
                            switchManageMember.setChecked(false);
                            isManageMember = false;
                        }

                        if (jsonObject.getInt("isManageBoard") == 1) {
                            switchManageBoard.setChecked(true);
                            isManageBoard = true;
                        } else {
                            switchManageBoard.setChecked(false);
                            isManageBoard = false;
                        }

                        if (jsonObject.getInt("isManageContents") == 1) {
                            switchManageContents.setChecked(true);
                            isManageContents = true;
                        } else {
                            switchManageContents.setChecked(false);
                            isManageContents = false;
                        }
                    } else if (mode == MODE_SAVE) {
                        Toast.makeText(TeamSettingActivity.this, "변경사항을 저장하였습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_team_setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        if (item.getItemId() == R.id.menu_save) {
            if (switchPublicTeam.isChecked() == isPublic && switchAutoJoin.isChecked() == isAutoJoin && switchManageMember.isChecked() == isManageMember && switchManageBoard.isChecked() == isManageBoard && switchManageContents.isChecked() == isManageContents) {
                Toast.makeText(this, "변경사항이 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String body = "";

                if (switchPublicTeam.isChecked() != isPublic) {
                    body = "isPublic=" + switchPublicTeam.isChecked() + "&";
                }

                if (switchAutoJoin.isChecked() != isAutoJoin) {
                    body += "isAutoJoin=" + switchAutoJoin.isChecked() + "&";
                }

                if (switchManageMember.isChecked() != isManageMember) {
                    body += "isManageMember=" + switchManageMember.isChecked() + "&";
                }

                if (switchManageBoard.isChecked() != isManageBoard) {
                    body += "isManageBoard=" + switchManageBoard.isChecked() + "&";
                }

                if (switchManageContents.isChecked() != isManageContents) {
                    body += "isManageContents=" + switchManageContents.isChecked() + "&";
                }

                body += "teamName=" + teamName;

                Log.d(TAG, body);
                HttpConnection httpConnection = new HttpConnection(this, body, "saveTeamSetting.php", httpCallBack);
                httpConnection.execute();

            }
        } else {
            finishCheck();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finishCheck();
    }

    private void finishCheck() {
        if (switchPublicTeam.isChecked() == isPublic && switchAutoJoin.isChecked() == isAutoJoin && switchManageMember.isChecked() == isManageMember && switchManageBoard.isChecked() == isManageBoard && switchManageContents.isChecked() == isManageContents) {
            finish();
        } else {
            AlertDialog.Builder checkDialog = new AlertDialog.Builder(this);

            checkDialog
                    .setTitle("팀 설정 나가기")
                    .setMessage("변경사항을 저장하지 않고 나가시겠습니까?");

            checkDialog.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            checkDialog.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            checkDialog.show();
        }
    }
}
