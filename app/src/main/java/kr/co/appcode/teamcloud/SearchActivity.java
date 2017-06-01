package kr.co.appcode.teamcloud;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final int MODE_SEARCH_TEAM = 1;
    private static final int MODE_JOIN_TEAM = 2;
    private static final int MODE_LEAVE_TEAM = 3;

    private ListView listTeam;
    private MaterialEditText editSearch;
    private ImageButton btnSearch;

    private TextView textNoResult;
    private TeamListAdapter adapter;

    private User user;
    private Profile profile;

    private boolean searchFlag;
    private String targetTeamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        user = getIntent().getParcelableExtra("login_user");
        profile = Profile.getCurrentProfile();

        editSearch = (MaterialEditText) findViewById(R.id.edit_search);
        textNoResult = (TextView) findViewById(R.id.text_no_search);

        adapter = new TeamListAdapter(this, new ArrayList<Team>(), 0, user);
        listTeam = (ListView) findViewById(R.id.list_team);
        listTeam.setAdapter(adapter);
        listTeam.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (!searchFlag) {
                        if (adapter.getMax() > adapter.getCount()) {
                            Log.d(TAG, "total: " + adapter.getMax() + " count: " + adapter.getCount());
                            String body = "nickname=" + user.getNickname() + "&teamName=" + targetTeamName + "&start=" + adapter.getCount();

                            HttpConnection httpConnection = new HttpConnection(SearchActivity.this, body, "searchTeam.php", httpCallBack);
                            httpConnection.execute();
                            searchFlag = true;
                        }
                    }
                }
            }
        });

        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();
                if (editSearch.length() > 0) {
                    if (adapter.getCount() > 0) {
                        adapter.getTeamList().clear();
                        adapter.notifyDataSetChanged();
                        adapter.setMax(0);
                    }
                    targetTeamName = editSearch.getText().toString();

                    String body = "nickname=" + user.getNickname() + "&teamName=" + targetTeamName + "&start=" + adapter.getCount();

                    HttpConnection httpConnection = new HttpConnection(SearchActivity.this, body, "searchTeam.php", httpCallBack);
                    httpConnection.execute();
                } else {
                    editSearch.setError("팀 이름을 입력해주세요.");
                }
            }
        });
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");
                Log.d(TAG, jsonObject.toString());

                if (mode == MODE_SEARCH_TEAM) {
                    textNoResult.setVisibility(View.GONE);
                    if (resultCode == Constant.SUCCESS) {
                        ArrayList<Team> teamList = new ArrayList<>();

                        int count = jsonObject.getInt("count");

                        for (int i = 0; i < count; i++) {
                            Team team = new Team();
                            team.setIdx(jsonObject.getString(i + "_idx"));
                            team.setName(jsonObject.getString(i + "_teamName"));
                            team.setMaster(jsonObject.getString(i + "_master"));
                            team.setLevel(jsonObject.getInt(i + "_level"));

                            teamList.add(team);
                        }

                        adapter.addTeamList(teamList);
                        adapter.setMax(jsonObject.getInt("totalCount"));
                        adapter.notifyDataSetChanged();

                        searchFlag = false;
                    } else {
                        adapter.getTeamList().clear();
                        adapter.notifyDataSetChanged();
                        textNoResult.setVisibility(View.VISIBLE);
                    }
                } else if (mode == MODE_JOIN_TEAM) {
                    if (resultCode == Constant.SUCCESS) {
                        ((Team) adapter.getItem(adapter.getCurrentPos())).setLevel(jsonObject.getInt("level"));
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                } else if (mode == MODE_LEAVE_TEAM) {
                    if (resultCode == Constant.SUCCESS) {
                        ((Team) adapter.getItem(adapter.getCurrentPos())).setLevel(-1);
                        adapter.notifyDataSetChanged();
                    } else if (resultCode == 2){
                        adapter.getTeamList().get(adapter.getCurrentPos()).setLevel(1);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(SearchActivity.this, "이미 가입되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
