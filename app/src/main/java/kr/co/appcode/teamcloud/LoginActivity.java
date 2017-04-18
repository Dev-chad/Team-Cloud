package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout layoutEditId;
    private TextInputLayout layoutEditPassword;
    private EditText editId;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layoutEditId = (TextInputLayout) findViewById(R.id.layout_editId);
        layoutEditPassword = (TextInputLayout) findViewById(R.id.layout_editPassword);
        editId = (EditText) findViewById(R.id.edit_id);
        editPassword = (EditText) findViewById(R.id.edit_password);

        Button btnSignIn = (Button) findViewById(R.id.btn_signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String password = editPassword.getText().toString();

                // hide error message
                layoutEditId.setErrorEnabled(false);
                layoutEditPassword.setErrorEnabled(false);

                // check input value
                if(id.length() == 0){
                    layoutEditId.setError("아이디를 입력해주세요.");
                } else if(id.contains(" ")){
                    layoutEditId.setError("아이디에 공백은 허용하지 않습니다.");
                } else if(password.length() == 0){
                    layoutEditPassword.setError("비밀번호를 입력해주세요.");
                } else if(password.contains(" ")){
                    layoutEditPassword.setError("비밀번호에 공백은 허용하지 않습니다.");
                }else {
                    Toast.makeText(LoginActivity.this, "Sign in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }
}
