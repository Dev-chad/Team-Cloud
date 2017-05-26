package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberManageActivity extends AppCompatActivity {
    private static final String TAG = "MemberManageActivity";
    private static final int MODE_GET_MEMBER = 1;
    private static final int MODE_EDIT_MEMBER = 2;

    private ListView listAdmin;
    private ListView listMember;

    private TextView textMasterName;
    private TextView textNoAdmin;
    private TextView textNoMember;

    private Team team;
    private User user;

    private MemberListAdapter normalMemberAdapter;
    private MemberListAdapter adminMemberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memeber_manage);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");

        normalMemberAdapter = new MemberListAdapter(this, new ArrayList<Member>(), 1);
        adminMemberAdapter = new MemberListAdapter(this, new ArrayList<Member>(), 2);

        listAdmin = (ListView) findViewById(R.id.list_admin);
        listAdmin.setAdapter(adminMemberAdapter);
        listMember = (ListView) findViewById(R.id.list_member);
        listMember.setAdapter(normalMemberAdapter);

        textMasterName = (TextView) findViewById(R.id.text_master_name);
        textMasterName.setText(team.getMaster());

        textNoAdmin = (TextView) findViewById(R.id.text_no_admin);
        textNoMember = (TextView) findViewById(R.id.text_no_member);
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpConnection httpConnection = new HttpConnection(this, "teamIdx=" + team.getIdx(), "getMember.php", httpCallBack);
        httpConnection.execute();
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

                normalMemberAdapter.getMemberList().clear();
                adminMemberAdapter.getMemberList().clear();

                if (mode == MODE_GET_MEMBER) {
                    if (resultCode == 1) {
                        int count = jsonObject.getInt("count");

                        if (count > 0) {
                            for (int i = 0; i < count; i++) {
                                Member member = new Member(jsonObject.getString(i + "_nickname"), jsonObject.getInt(i + "_level"), jsonObject.getInt(i + "_isManageMember"), jsonObject.getInt(i + "_isManageBoard"), jsonObject.getInt(i + "_isManageContents"), jsonObject.getString(i + "_accessDate"), jsonObject.getString(i + "_joinDate"));
                                if (member.getLevel() == 1) {
                                    normalMemberAdapter.add(member);
                                } else {
                                    adminMemberAdapter.add(member);
                                }
                            }

                            normalMemberAdapter.notifyDataSetChanged();
                            adminMemberAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnItems(listMember, listMember.getCount());
                            textNoMember.setVisibility(View.GONE);

                            if (adminMemberAdapter.getCount() == 0) {
                                ViewGroup.LayoutParams params = listAdmin.getLayoutParams();
                                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                                listAdmin.setLayoutParams(params);
                                listAdmin.requestLayout();
                                textNoAdmin.setVisibility(View.VISIBLE);
                            } else {
                                setListViewHeightBasedOnItems(listAdmin, listAdmin.getCount());
                                textNoAdmin.setVisibility(View.GONE);
                            }
                        } else {
                            normalMemberAdapter.getMemberList().clear();
                            adminMemberAdapter.getMemberList().clear();
                            normalMemberAdapter.notifyDataSetChanged();
                            adminMemberAdapter.notifyDataSetChanged();

                            ViewGroup.LayoutParams params = listMember.getLayoutParams();
                            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                            listMember.setLayoutParams(params);
                            listMember.requestLayout();

                            params = listAdmin.getLayoutParams();
                            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                            listAdmin.setLayoutParams(params);
                            listAdmin.requestLayout();

                            textNoMember.setVisibility(View.VISIBLE);
                            textNoAdmin.setVisibility(View.VISIBLE);
                        }
                    } else {

                    }
                } else if (mode == MODE_EDIT_MEMBER) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}