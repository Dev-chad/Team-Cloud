package kr.co.appcode.teamcloud;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Chad on 2017-05-28.
 */

public class BoardAddDialog extends Dialog {
    private Board board;
    private String teamIdx;

    private SeekBar seekBarReadAuth;
    private SeekBar seekBarWriteAuth;

    private TextView textReadAuth;
    private TextView textWriteAuth;

    private MaterialEditText editBoardName;

    private Button btnApply;
    private Button btnClose;

    private ArrayList<Board> boardList;

    public BoardAddDialog(Context context, String teamIdx, ArrayList<Board> boardList) {
        super(context);
        this.boardList = boardList;
        this.teamIdx = teamIdx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_board_add);

        textReadAuth = (TextView) findViewById(R.id.text_read_auth);
        textWriteAuth = (TextView) findViewById(R.id.text_write_auth);

        seekBarReadAuth = (SeekBar) findViewById(R.id.seekbar_read_auth);
        seekBarReadAuth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 2) {
                    textReadAuth.setText("마스터");
                    textReadAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                } else if (progress == 1) {
                    textReadAuth.setText("관리자");
                    textReadAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.darkgreen));

                } else if (progress == 0) {
                    textReadAuth.setText("일반멤버");
                    textReadAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarWriteAuth = (SeekBar) findViewById(R.id.seekbar_write_auth);
        seekBarWriteAuth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 2) {
                    textWriteAuth.setText("마스터");
                    textWriteAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
                } else if (progress == 1) {
                    textWriteAuth.setText("관리자");
                    textWriteAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.darkgreen));

                } else if (progress == 0) {
                    textWriteAuth.setText("일반멤버");
                    textWriteAuth.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editBoardName = (MaterialEditText) findViewById(R.id.edit_board_name);
        editBoardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editBoardName.getError() != null) {
                    editBoardName.setError(null);
                }
            }
        });

        btnApply = (Button) findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editBoardName.length() == 0) {
                    editBoardName.setError("게시판 이름을 입력해주세요.");
                } else {
                    String body = "teamIdx=" + teamIdx + "&boardName=" + editBoardName.getText().toString() + "&writeAuth=" + (seekBarWriteAuth.getProgress() + 1) + "&readAuth=" + (seekBarReadAuth.getProgress() + 1) + "&mode=2";
                    HttpConnection httpConnection = new HttpConnection(body, "setBoard.php", httpCallBack);
                    httpConnection.execute();
                }
            }
        });

        btnClose = (Button)findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");

                if (resultCode == Constant.SUCCESS) {
                    boardList.add(new Board(editBoardName.getText().toString(), seekBarWriteAuth.getProgress()+1, seekBarReadAuth.getProgress()+1));
                    Toast.makeText(getContext(), "게시판 추가하였습니다.", Toast.LENGTH_SHORT).show();

                    dismiss();
                } else if (resultCode == 2) {
                    editBoardName.setError("이미 존재하는 게시판입니다.");
                } else {
                }
                Log.d("error", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
