package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private ListView listTeam;
    private MaterialEditText editSearch;
    private ImageButton btnSearch;

    private TextView textNoResult;
    private CustomTeamListAdapter adapter;

    private User user;
    private Profile profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        user = getIntent().getParcelableExtra("login_user");
        profile = Profile.getCurrentProfile();

        listTeam = (ListView)findViewById(R.id.list_team);
        editSearch = (MaterialEditText)findViewById(R.id.edit_search);
        btnSearch = (ImageButton)findViewById(R.id.btn_search);
        textNoResult = (TextView)findViewById(R.id.text_no_search);

        adapter = new CustomTeamListAdapter(this);
        listTeam.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editSearch.length() > 0){
                    HashMap<String, String> values = new HashMap<>();
                    values.put("nickname", user.getNickname());
                    values.put("teamName", editSearch.getText().toString());

                    HttpPostManager httpPostManager = new HttpPostManager(SearchActivity.this, values, httpCallBack);
                    httpPostManager.setCheckSession(true);
                    httpPostManager.setMode(HttpPostManager.MODE_TEAM_SEARCH);
                    httpPostManager.execute();

                } else {

                }
            }
        });
    }

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try{
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if(mode == HttpPostManager.MODE_TEAM_SEARCH){
                    textNoResult.setVisibility(View.GONE);
                    if (resultCode == Constant.SUCCESS){
                        ArrayList<Team> teamList = new ArrayList<>();

                        int count = jsonObject.getInt("count");

                        for(int i=1; i<=count; i++){
                            teamList.add(new Team(jsonObject.getString(i+"_teamName"), jsonObject.getString(i+"_master"), jsonObject.getInt(i+"_level")));
                        }

                        adapter.setTeamList(teamList);
                        adapter.notifyDataSetChanged();
                    } else {
                        textNoResult.setVisibility(View.VISIBLE);
                    }
                }
                Log.d(TAG, jsonObject.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
}
