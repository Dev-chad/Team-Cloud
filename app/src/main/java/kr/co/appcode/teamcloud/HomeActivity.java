package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "HomeActivity";

    private User user;
    private Profile profile;
    private ListView teamList;
    private ProgressBar capacityBar;
    private TextView textUsedCapacity;
    private TextView textMaxCapacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = Profile.getCurrentProfile();
        user = getIntent().getParcelableExtra("loginUser");

        capacityBar = (ProgressBar)findViewById(R.id.progress_capacity);
        capacityBar.setMax(user.getMax_capacity());
        capacityBar.setProgress(user.getUsed_capacity());

        textUsedCapacity = (TextView)findViewById(R.id.text_used_capacity);
        textUsedCapacity.setText(""+user.getUsed_capacity());

        textMaxCapacity = (TextView)findViewById(R.id.text_max_capacity);
        textMaxCapacity.setText(""+user.getMax_capacity());

        teamList = (ListView)findViewById(R.id.list_team);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for(int i=0; i<10; i++){
            adapter.add("팀 "+(i+1));
        }

        teamList.setAdapter(adapter);
        teamList.setOnItemClickListener(this);

        setListViewHeightBasedOnItems(teamList, 6);

        final TextView textDetail = (TextView)findViewById(R.id.text_detail);
        textDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textDetail.getText().toString().equals("더 보기...")){
                    setListViewHeightBasedOnItems(teamList, 0);
                    textDetail.setText("접기");
                } else{
                    setListViewHeightBasedOnItems(teamList, 6);
                    textDetail.setText("더 보기...");
                }
            }
        });*/

        Button btnCreateTeam = (Button)findViewById(R.id.btn_create_team);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, ""+position, Toast.LENGTH_SHORT).show();
    }

    public void setListViewHeightBasedOnItems(ListView listView, int count) {

        // Get list adpter of listview;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)  return;

        int numberOfItems;
        if(count == 0){
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
        int totalDividersHeight = listView.getDividerHeight() *  (numberOfItems - 1);

        // Set list height.
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalItemsHeight + totalDividersHeight;
        listView.setLayoutParams(params);
        listView.requestLayout();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
}
