package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

public class CreateTeamActivity extends AppCompatActivity {

    private MaterialEditText editTeamName;
    private EditText editCapacity;
    private SeekBar seekBarCapacity;
    private TextView textMaxCapacity;

    private boolean isCheckTeamName;
    private boolean isSetCapacity = true;
    private int max = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        textMaxCapacity = (TextView)findViewById(R.id.text_max_capacity);
        textMaxCapacity.setText(max+" GB");

        editTeamName = (MaterialEditText)findViewById(R.id.edit_team_name);
        editTeamName.addValidator(new RegexpValidator("2~20자 -, _ 가능", "^[-a-zA-Z가-힛_\\s]{2,20}$"));

        editTeamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(editTeamName.validate()){
                }
            }
        });

        editCapacity = (EditText)findViewById(R.id.edit_capacity);
        editCapacity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0){
                    isSetCapacity = true;
                    int value = Integer.valueOf(s.toString());

                    if(value>max){
                        editCapacity.setText(String.valueOf(max));
                        value = max;
                    }

                    seekBarCapacity.setProgress(value);
                } else {
                    isSetCapacity = false;
                }
            }
        });

        seekBarCapacity = (SeekBar)findViewById(R.id.seekbar_capacity);
        seekBarCapacity.setMax(max);
        seekBarCapacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(progress == 0){
                    progress = 1;
                    seekBar.setProgress(progress);
                }

                editCapacity.setText(String.valueOf(progress));
                isSetCapacity = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        Button btnCheckTeamName = (Button)findViewById(R.id.btn_check_team_name);
        btnCheckTeamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button btnCreateTeam = (Button)findViewById(R.id.btn_create_team);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
