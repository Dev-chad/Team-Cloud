package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AddNickname extends AppCompatActivity {

    private MaterialEditText editNickname;

    private boolean isCheckedNickname;
    private Button btnCheckNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nickname);

        editNickname = (MaterialEditText) findViewById(R.id.edit_nickname);
        editNickname.addValidator(new RegexpValidator("영문을 포함한 4~15자의 숫자, _, - 가능.", "^(?=.*[a-zA-Z])([_a-z0-9-]){4,15}$"));
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

        btnCheckNickname = (Button)findViewById(R.id.btn_check_nickname);
        btnCheckNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCheckedNickname) {
                    String nickname = editNickname.getText().toString();

                    if (editNickname.length() == 0) {
                        editNickname.setError("닉네임을 입력해주세요.");
                    } else if (editNickname.validate()) {
                        HashMap<String, String> values = new HashMap<>();
                        values.put("nickname", nickname);


                    } else {
                        editNickname.setError("닉네임이 올바르지 않습니다.");
                    }
                } else {
                    Snackbar.make(v, "이미 닉네임 중복검사를 완료하였습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private HttpCallBack httpCallBack = new HttpCallBack(){
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            if (jsonObject != null) {
                try {
                    if (jsonObject.getInt("return_code") == Constant.DUPLICATED) {
                        editNickname.setError("이미 등록된 닉네임입니다.");
                    } else {
                        btnCheckNickname.setText("확인완료");
                        isCheckedNickname = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    };
}
