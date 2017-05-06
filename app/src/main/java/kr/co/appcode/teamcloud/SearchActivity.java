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
    private TeamListAdapter adapter;

    private User user;
    private Profile profile;

    private boolean searchFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        user = getIntent().getParcelableExtra("login_user");
        profile = Profile.getCurrentProfile();

        listTeam = (ListView) findViewById(R.id.list_team);
        editSearch = (MaterialEditText) findViewById(R.id.edit_search);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        textNoResult = (TextView) findViewById(R.id.text_no_search);

        adapter = new TeamListAdapter(this, new ArrayList<SearchListItem>(), 0, user);
        listTeam.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closingSoftKeyboard();
                if (editSearch.length() > 0) {
                    if (adapter.getCount() > 0) {
                        adapter.getSearchListItemList().clear();
                        adapter.notifyDataSetChanged();
                        adapter.setMax(0);
                    }
                    HashMap<String, String> values = new HashMap<>();
                    values.put("nickname", user.getNickname());
                    values.put("teamName", editSearch.getText().toString());
                    values.put("start", String.valueOf(adapter.getCount()));
                    values.put("sessionInfo", user.getSessionInfo());

                    HttpConnection httpConnection = new HttpConnection(SearchActivity.this, values, httpCallBack);
                    httpConnection.setCheckSession(true);
                    httpConnection.setMode(HttpConnection.MODE_TEAM_SEARCH);
                    httpConnection.execute();

                } else {
                    editSearch.setError("팀 이름을 입력해주세요.");
                }
            }
        });

        listTeam.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (!searchFlag) {
                        if (adapter.getMax() > adapter.getCount()) {
                            HashMap<String, String> values = new HashMap<>();
                            values.put("nickname", user.getNickname());
                            values.put("teamName", editSearch.getText().toString());
                            values.put("start", String.valueOf(adapter.getCount()));
                            values.put("sessionInfo", user.getSessionInfo());

                            HttpConnection httpConnection = new HttpConnection(SearchActivity.this, values, httpCallBack);
                            httpConnection.setCheckSession(true);
                            httpConnection.setMode(HttpConnection.MODE_TEAM_SEARCH);
                            httpConnection.execute();
                            searchFlag = true;
                        }
                    }
                }
            }
        });

       /* listTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });*/
    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == HttpConnection.MODE_TEAM_SEARCH) {
                    textNoResult.setVisibility(View.GONE);
                    if (resultCode == Constant.SUCCESS) {
                        ArrayList<SearchListItem> searchListItemList = new ArrayList<>();

                        int count = jsonObject.getInt("count");

                        for (int i = 1; i <= count; i++) {
                            searchListItemList.add(new SearchListItem(jsonObject.getString(i + "_teamName"), jsonObject.getString(i + "_master"), jsonObject.getInt(i + "_level")));
                        }

                        adapter.addTeamList(searchListItemList);
                        adapter.setMax(jsonObject.getInt("totalCount"));
                        adapter.notifyDataSetChanged();

                        searchFlag = false;
                    } else {
                        adapter.getSearchListItemList().clear();
                        adapter.notifyDataSetChanged();
                        textNoResult.setVisibility(View.VISIBLE);
                    }
                } else if (mode == HttpConnection.MODE_JOIN_TEAM) {
                    if (resultCode == Constant.SUCCESS) {

                        adapter.getSearchListItemList().get(adapter.getCurrentPos()).setLevel(jsonObject.getInt("level"));

                        adapter.notifyDataSetChanged();
                    } else {

                    }
                } else if (mode == HttpConnection.MODE_JOIN_CANCEL) {
                    if (resultCode == Constant.SUCCESS) {
                        adapter.getSearchListItemList().get(adapter.getCurrentPos()).setLevel(-1);

                        adapter.notifyDataSetChanged();
                    } else {

                    }
                }
                Log.d(TAG, jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    private void closingSoftKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
