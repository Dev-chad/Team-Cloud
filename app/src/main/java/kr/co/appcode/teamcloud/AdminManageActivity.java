package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminManageActivity extends AppCompatActivity {
    private static final String TAG = "AdminManageActivity";
    private static final int MODE_GET_ADMIN = 1;

    private ListView listAdmin;
    private AdminListAdapter adapter;

    private MaterialEditText editSearch;

    private ImageView imageSearch;

    private TextView textNoAdmin;

    private Team team;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");

        adapter = new AdminListAdapter(this, new ArrayList<Member>(), team.getIdx(), 2);
        listAdmin = (ListView)findViewById(R.id.list_admin);
        listAdmin.setAdapter(adapter);

        editSearch = (MaterialEditText)findViewById(R.id.edit_search_admin);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageSearch = (ImageView)findViewById(R.id.image_search_admin);
        imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        textNoAdmin = (TextView)findViewById(R.id.text_no_admin);
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpConnection httpConnection = new HttpConnection(this, "teamIdx="+team.getIdx(), "getAdmin.php", httpCallBack);
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

                if(mode == MODE_GET_ADMIN){
                    if(resultCode == Constant.SUCCESS){
                        int count = jsonObject.getInt("count");

                        for(int i=0; i<count; i++){
                            Member member = new Member(jsonObject.getString(i+"_nickname"), jsonObject.getInt(i+"_level"), jsonObject.getInt(i+"_isManageMember"), jsonObject.getInt(i+"_isManageBoard"), jsonObject.getInt(i+"_isManageContents"), jsonObject.getString(i+"_accessDate"), jsonObject.getString(i+"_joinDate"));
                            adapter.add(member);
                        }

                        adapter.notifyDataSetChanged();

                        if(count > 0){
                            textNoAdmin.setVisibility(View.GONE);
                        } else {
                            textNoAdmin.setVisibility(View.VISIBLE);
                        }
                    } else {

                    }
                }
                Log.d(TAG, jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
