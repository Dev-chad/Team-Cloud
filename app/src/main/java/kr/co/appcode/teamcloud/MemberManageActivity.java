package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MemberManageActivity extends AppCompatActivity {
    private static final String TAG = "MemberManageActivity";
    private static final int MODE_GET_MEMBER = 1;
    private static final int MODE_EDIT_MEMBER = 2;

    private ListView listAdmin;
    private ListView listMember;

    private TextView textNoMember;
    private TextView textNoAdmin;
    private TextView textMasterName;

    private Team team;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memeber_manage);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");

        listAdmin = (ListView) findViewById(R.id.list_admin);
        listMember = (ListView) findViewById(R.id.list_member);

        textMasterName = (TextView) findViewById(R.id.text_master_name);
        textMasterName.setText(team.getMaster());

        textNoAdmin = (TextView) findViewById(R.id.text_no_admin);
        textNoMember = (TextView) findViewById(R.id.text_no_member);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

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

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                int mode = jsonObject.getInt("mode");

                if(mode == MODE_GET_MEMBER){

                }else if(mode == MODE_EDIT_MEMBER){

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
