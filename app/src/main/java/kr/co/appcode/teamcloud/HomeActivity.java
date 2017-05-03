package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "HomeActivity";

    private User user;
    private Profile profile;
    private ProgressBar capacityBar;
    private TextView textUsedCapacity;
    private TextView textMaxCapacity;
    private LinearLayout layoutNoTeam;

    private GridView gridTeamList;

    private ImageView imgTeamSetting;

    private HttpConnection httpConnection;
    private TextView textDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = Profile.getCurrentProfile();
        user = getIntent().getParcelableExtra("loginUser");

        TextView textNickname = (TextView)findViewById(R.id.text_nickname);
        textNickname.setText(user.getNickname());
        textDetail = (TextView)findViewById(R.id.text_detail);

        layoutNoTeam = (LinearLayout)findViewById(R.id.layout_no_team);

        capacityBar = (ProgressBar) findViewById(R.id.progress_capacity);
        capacityBar.setMax(user.getMaxCapacity());
        capacityBar.setProgress(user.getUsedCapacity());

        textUsedCapacity = (TextView) findViewById(R.id.text_used_capacity);
        textUsedCapacity.setText(String.valueOf(user.getUsedCapacity()));

        textMaxCapacity = (TextView) findViewById(R.id.text_max_capacity);
        textMaxCapacity.setText(String.valueOf(user.getMaxCapacity()));

        gridTeamList = (GridView) findViewById(R.id.grid_teamlist);
        gridTeamList.setOnItemClickListener(this);

        imgTeamSetting = (ImageView)findViewById(R.id.img_team_setting);
        imgTeamSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Team setting", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCreateTeam = (Button) findViewById(R.id.btn_create_team);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateTeamActivity.class);
                intent.putExtra("login_user", user);
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(

                android.R.color.holo_red_light
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserData();
                getTeamList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        textDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void getTeamList() {
        HashMap<String, String> values = new HashMap<>();
        values.put("nickname", user.getNickname());
        values.put("sessionInfo", user.getSessionInfo());

        httpConnection = new HttpConnection(this, values, httpCallBack);
        httpConnection.setMode(HttpConnection.MODE_GET_TEAM_LIST);
        httpConnection.setCheckSession(true);
        httpConnection.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
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

    public void setGridViewHeightBasedOnItems(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();

        int totalItemsHeight = 0;
        int numberOfItems;
        // Get list adpter of listview;
        if (listAdapter == null) return;

        if(listAdapter.getCount() <= 4){
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
        } else if(id==R.id.menu_search){
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

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == HttpConnection.MODE_REFRESH_USER) {
                    if (resultCode == Constant.SUCCESS) {
                        user.setMaxCapacity(jsonObject.getInt("maxCapacity"));
                        user.setUsedCapacity(jsonObject.getInt("usedCapacity"));
                        user.setAvailableCapacity(jsonObject.getInt("maxCapacity") - jsonObject.getInt("usedCapacity"));

                        refreshActivity();
                    }
                } else if (mode == HttpConnection.MODE_GET_TEAM_LIST) {
                    if (resultCode == Constant.SUCCESS) {
                        layoutNoTeam.setVisibility(View.GONE);
                        int totalCount = jsonObject.getInt("totalCount");
                        int count;

                        if(totalCount >= 8){
                            count = 8;
                        } else {
                            count = totalCount;
                        }

                        ArrayList<String> list = new ArrayList<>();

                        for (int i = 1; i <= count; i++) {
                            list.add(jsonObject.getString(String.valueOf(i)));
                        }

                        CustomGridAdapter adapter = new CustomGridAdapter(HomeActivity.this, list);
                        gridTeamList.setAdapter(adapter);

                        if(totalCount>8){
                            textDetail.setVisibility(View.VISIBLE);
                        } else {
                            textDetail.setVisibility(View.GONE);
                        }

                        setGridViewHeightBasedOnItems(gridTeamList);
                    } else {
                        layoutNoTeam.setVisibility(View.VISIBLE);
                    }
                }

                Log.d(TAG, "mode: " + mode + "  resultcode: " + resultCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void refreshActivity() {
        capacityBar.setMax(user.getMaxCapacity());
        capacityBar.setProgress(user.getUsedCapacity());
        textUsedCapacity.setText(String.valueOf(user.getUsedCapacity()));
        textMaxCapacity.setText(String.valueOf(user.getMaxCapacity()));
    }

    private void getUserData() {
        HashMap<String, String> values = new HashMap<>();
        values.put("nickname", user.getNickname());
        values.put("sessionInfo", user.getSessionInfo());

        httpConnection = new HttpConnection(this, values, httpCallBack);
        httpConnection.setMode(HttpConnection.MODE_REFRESH_USER);
        httpConnection.setCheckSession(true);
        httpConnection.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserData();
        getTeamList();
    }
}
