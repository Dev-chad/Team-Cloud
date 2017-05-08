package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TeamPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TeamPageActivity";

    private Profile profile;

    private FrameLayout frameContent;
    private User user;

    private ListView listBoard;

    private TextView textNickname;
    private TextView textLevel;

    private String teamName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_page);

        user = getIntent().getParcelableExtra("login_user");
        teamName = getIntent().getStringExtra("teamName");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(teamName);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        textNickname = (TextView)findViewById(R.id.text_nickname);
        textNickname.setText(user.getNickname());
        textLevel = (TextView)findViewById(R.id.text_level);

        HomeFragment homeFragment = new HomeFragment();

        listBoard = (ListView) findViewById(R.id.list_board);

        HashMap<String, String> values = new HashMap<>();
        values.put("teamName", teamName);
        values.put("nickname", user.getNickname());

        HttpConnection httpConnection = new HttpConnection(this, values, httpCallBack);
        httpConnection.setMode(HttpConnection.MODE_GET_BOARD);
        httpConnection.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
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
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {

                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == HttpConnection.MODE_GET_BOARD) {
                    if (resultCode == Constant.SUCCESS) {
                        int count = jsonObject.getInt("count");
                        BoardListAdapter boardListAdapter = new BoardListAdapter(TeamPageActivity.this, new ArrayList<Board>());

                        for (int i = 0; i < count; i++) {
                            Board board = new Board(jsonObject.getString(i + "_name"), jsonObject.getInt(i + "_write_auth"), jsonObject.getInt(i + "_read_auth"));
                            boardListAdapter.add(board);
                        }

                        listBoard.setAdapter(boardListAdapter);
                        setListViewHeightBasedOnItems(listBoard, boardListAdapter.getCount());

                        user.setLevel(jsonObject.getInt("level"));
                        if(user.getLevel() == 1){
                            textLevel.setText("일반 멤버");
                        } else if(user.getLevel() == 2){
                            textLevel.setText("관리자 멤버");
                        } else if(user.getLevel() == 3){
                            textLevel.setText("마스터 멤버");
                        }

                        HomeFragment homeFragment = new HomeFragment();

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("login_user", user);
                        bundle.putString("teamName", teamName);

                        homeFragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.frame_content, homeFragment)
                                .commit();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void setListViewHeightBasedOnItems(ListView listView, int count) {

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
    }
}
