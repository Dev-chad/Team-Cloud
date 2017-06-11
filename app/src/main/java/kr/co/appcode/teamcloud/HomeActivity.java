package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "HomeActivity";
    private static final int MODE_GET_USER = 1;
    private static final int MODE_GET_TEAM_LIST = 2;

    private User user;
    private Profile profile;

    private ProgressBar capacityBar;

    private TextView textUsedCapacity;
    private TextView textMaxCapacity;
    private TextView textMessage;

    private LinearLayout layoutNoTeam;

    private GridView gridTeamList;

    private HttpConnection httpConnection;

    private Button btnCreateTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = Profile.getCurrentProfile();
        user = getIntent().getParcelableExtra("login_user");

        layoutNoTeam = (LinearLayout) findViewById(R.id.layout_no_team);

        textMessage = (TextView) findViewById(R.id.text_message);

        TextView textNickname = (TextView) findViewById(R.id.text_nickname);
        textNickname.setText(user.getNickname());

        capacityBar = (ProgressBar) findViewById(R.id.progress_capacity);
        capacityBar.setMax(user.getMaxCapacity());
        capacityBar.setProgress(user.getUsedCapacity());

        textUsedCapacity = (TextView) findViewById(R.id.text_used_capacity);
        textUsedCapacity.setText(String.valueOf(user.getUsedCapacity()));

        textMaxCapacity = (TextView) findViewById(R.id.text_max_capacity);
        textMaxCapacity.setText(String.valueOf(user.getMaxCapacity()));

        gridTeamList = (GridView) findViewById(R.id.grid_teamlist);
        gridTeamList.setOnItemClickListener(this);

        ImageView imgTeamSetting = (ImageView) findViewById(R.id.img_team_setting);
        imgTeamSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Team setting", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreateTeam = (Button) findViewById(R.id.btn_create_team);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnCreateTeam.getText().equals("팀 만들기")) {
                    Intent intent = new Intent(HomeActivity.this, CreateTeamActivity.class);
                    intent.putExtra("login_user", user);
                    startActivity(intent);
                } else {
                    //TODO: 저장공간 구매 페이지로 이동
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUser();
                getTeamList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUser();
        getTeamList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            if (profile != null) {
                LoginManager.getInstance().logOut();
            } else {
                SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                SharedPreferences.Editor spEditor = sp.edit();

                spEditor.remove("id")
                        .remove("password")
                        .remove("type");

                spEditor.apply();
            }

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            finish();
        } else if (id == R.id.menu_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("login_user", user);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, TeamPageActivity.class);
        intent.putExtra("team", (Team)gridTeamList.getItemAtPosition(position));
        intent.putExtra("login_user", user);
        startActivity(intent);
    }

    private void getUser() {
        String body = "nickname=" + user.getNickname();

        httpConnection = new HttpConnection(this, body, "getUser.php", httpCallBack);
        httpConnection.execute();
    }

    private void getTeamList() {
        String body = "nickname=" + user.getNickname();

        httpConnection = new HttpConnection(this, body, "getTeamList.php", httpCallBack);
        httpConnection.execute();
    }

    public void setGridViewHeightBasedOnItems(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();

        int totalItemsHeight = 0;
        int numberOfItems;
        // Get list adpter of listview;
        if (listAdapter == null) return;

        if (listAdapter.getCount() <= 4) {
            numberOfItems = 1;
        } else {
            numberOfItems = 2;
        }

        // Get total height of all items.
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, gridView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = gridView.getVerticalSpacing() * (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }

    /*public void setListViewHeightBasedOnItems(ListView listView, int count) {

        // Get list adpter of listview;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int numberOfItems;
        if (count == 0) {
            numberOfItems = listAdapter.getCount();
        } else {
            numberOfItems = count;
        }

        // Get total height of all items.
        int totalItemsHeight = 0;
        for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
            View item = listAdapter.getView(itemPos, null, listView);
            item.measure(0, 0);
            totalItemsHeight += item.getMeasuredHeight();
        }

        // Get total height of all item dividers.
        int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }*/

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == MODE_GET_USER) {
                    if (resultCode == Constant.SUCCESS) {
                        user.update(jsonObject);

                        capacityBar.setMax(user.getMaxCapacity());
                        capacityBar.setProgress(user.getUsedCapacity());
                        textUsedCapacity.setText(String.valueOf(user.getUsedCapacity()));
                        textMaxCapacity.setText(String.valueOf(user.getMaxCapacity()));

                        if (user.getUsedCapacity() < user.getMaxCapacity()) {
                            textMessage.setText("팀을 만들어 보세요");
                            btnCreateTeam.setText("팀 만들기");
                        } else {
                            textMessage.setText("사용가능한 용량이 없습니다");
                            btnCreateTeam.setText("용량 구매하기");
                        }
                    }
                } else if (mode == MODE_GET_TEAM_LIST) {

                    if (resultCode == Constant.SUCCESS) {
                        layoutNoTeam.setVisibility(View.GONE);
                        int totalCount = jsonObject.getInt("totalCount");

                        ArrayList<Team> teamList = new ArrayList<>();

                        for (int i = 0; i < totalCount; i++) {
                            Team team = new Team(jsonObject.getString(i + "_idx"), jsonObject.getString(i + "_name"), jsonObject.getString(i + "_master"), jsonObject.getLong(i + "_usedCapacity"), jsonObject.getInt(i + "_maxCapacity"), jsonObject.getInt(i + "_isPublic"), jsonObject.getInt(i + "_isAutoJoin"), jsonObject.getInt(i + "_isAdminManageMember"), jsonObject.getInt(i + "_isAdminManageBoard"), jsonObject.getInt(i + "_isAdminManageContents"), jsonObject.getString(i + "_teamMarkUrl"));
                            teamList.add(team);
                        }

                        TeamGridAdapter adapter = new TeamGridAdapter(HomeActivity.this, teamList);
                        gridTeamList.setAdapter(adapter);

                        setGridViewHeightBasedOnItems(gridTeamList);
                    } else {
                        layoutNoTeam.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
