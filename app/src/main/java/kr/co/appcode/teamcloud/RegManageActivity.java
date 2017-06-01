package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegManageActivity extends AppCompatActivity {
    private static final String TAG = "AdminManageActivity";
    private static final int MODE_GET_REG_MEMBER = 1;
    private static final int MODE_ACCEPT_MEMBER = 2;
    private static final int MODE_DENY_MEMBER = 3;

    private ListView listReg;
    private RegListAdapter adapter;

    private TextView textNoMember;

    private Team team;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_manage);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");

        adapter = new RegListAdapter(this, new ArrayList<Member>(), team.getIdx(), 2);
        listReg = (ListView) findViewById(R.id.list_reg);
        listReg.setAdapter(adapter);

        textNoMember = (TextView) findViewById(R.id.text_no_member);
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpConnection httpConnection = new HttpConnection(this, "teamIdx=" + team.getIdx(), "getRegMember.php", httpCallBack);
        httpConnection.execute();
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        finish();
        return super.onOptionsItemSelected(item);
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == MODE_GET_REG_MEMBER) {
                    if (resultCode == Constant.SUCCESS) {
                        int count = jsonObject.getInt("count");

                        for (int i = 0; i < count; i++) {
                            Member member = new Member(jsonObject.getString(i + "_nickname"), jsonObject.getInt(i + "_level"), jsonObject.getInt(i + "_isManageMember"), jsonObject.getInt(i + "_isManageBoard"), jsonObject.getInt(i + "_isManageContents"), jsonObject.getString(i + "_accessDate"), jsonObject.getString(i + "_joinDate"));
                            adapter.add(member);
                        }

                        adapter.notifyDataSetChanged();

                        if (count > 0) {
                            textNoMember.setVisibility(View.GONE);
                        } else {
                            textNoMember.setVisibility(View.VISIBLE);
                        }
                    } else {

                    }
                } else if (mode == MODE_ACCEPT_MEMBER) {
                    if (resultCode == Constant.SUCCESS){
                        adapter.getMemberList().remove(adapter.getCurrentPos());
                        adapter.notifyDataSetChanged();

                        if(adapter.getCount() == 0){
                            textNoMember.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(RegManageActivity.this, "가입을 승인했습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else if(mode == MODE_DENY_MEMBER){
                    if(resultCode == Constant.SUCCESS){
                        adapter.getMemberList().remove(adapter.getCurrentPos());
                        adapter.notifyDataSetChanged();

                        if(adapter.getCount() == 0){
                            textNoMember.setVisibility(View.VISIBLE);
                        }
                        Toast.makeText(RegManageActivity.this, "가입을 거절했습니다.", Toast.LENGTH_SHORT).show();

                    }
                }
                Log.d(TAG, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
