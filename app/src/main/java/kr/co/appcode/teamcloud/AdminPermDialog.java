package kr.co.appcode.teamcloud;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Chad on 2017-05-28.
 */

public class AdminPermDialog extends Dialog {
    private TextView textIsManageMember;
    private TextView textIsManageBoard;
    private TextView textIsManageContents;

    private SwitchCompat switchManageMember;
    private SwitchCompat switchManageBoard;
    private SwitchCompat switchManageContents;

    private String teamIdx;
    private Member member;

    private boolean isManageMember;
    private boolean isManageBoard;
    private boolean isManageContents;

    public AdminPermDialog(Context context, String teamIdx, Member member){
        super(context);
        this.teamIdx = teamIdx;
        this.member = member;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_admin_perm);

        textIsManageMember = (TextView)findViewById(R.id.text_is_manage_member);
        textIsManageBoard = (TextView)findViewById(R.id.text_is_manage_board);
        textIsManageContents = (TextView)findViewById(R.id.text_is_manage_contents);

        switchManageMember = (SwitchCompat) findViewById(R.id.switch_manage_member);
        switchManageMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageMember.setText("켜짐");
                    textIsManageMember.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    textIsManageMember.setText("꺼짐");
                    textIsManageMember.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                }
            }
        });

        switchManageBoard = (SwitchCompat) findViewById(R.id.switch_manage_board);
        switchManageBoard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageBoard.setText("켜짐");
                    textIsManageBoard.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    textIsManageBoard.setText("꺼짐");
                    textIsManageBoard.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                }
            }
        });

        switchManageContents = (SwitchCompat) findViewById(R.id.switch_manage_contents);
        switchManageContents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textIsManageContents.setText("켜짐");
                    textIsManageContents.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    textIsManageContents.setText("꺼짐");
                    textIsManageContents.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                }
            }
        });

        Button btnApply = (Button)findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchManageMember.isChecked() == isManageMember && switchManageBoard.isChecked() == isManageBoard && switchManageContents.isChecked() == isManageContents) {
                    Toast.makeText(getContext(), "변경사항이 없습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    String body = "";

                    if (switchManageMember.isChecked() != isManageMember) {
                        body += "isManageMember=" + switchManageMember.isChecked() + "&";
                    }

                    if (switchManageBoard.isChecked() != isManageBoard) {
                        body += "isManageBoard=" + switchManageBoard.isChecked() + "&";
                    }

                    if (switchManageContents.isChecked() != isManageContents) {
                        body += "isManageContents=" + switchManageContents.isChecked() + "&";
                    }

                    body = body + "teamIdx=" + teamIdx + "&nickname="+member.getNickname();

                    HttpConnection httpConnection = new HttpConnection(body, "setAdminPerm.php", httpCallBack);
                    httpConnection.execute();
                }
            }
        });

        switchManageMember.setChecked(member.isManageMember());
        isManageMember = member.isManageMember();
        switchManageBoard.setChecked(member.isManageBoard());
        isManageBoard = member.isManageBoard();
        switchManageContents.setChecked(member.isManageContents());
        isManageContents = member.isManageContents();
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                if(jsonObject.getInt("resultCode") == Constant.SUCCESS){
                    member.setManageMember(switchManageMember.isChecked());
                    member.setManageBoard(switchManageBoard.isChecked());
                    member.setManageContents(switchManageContents.isChecked());

                    Toast.makeText(getContext(), "변경사항을 적용했습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "변경사항을 적용했습니다.", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
