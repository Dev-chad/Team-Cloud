package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MemberManageActivity extends AppCompatActivity {
    private static final String TAG = "MemberManageActivity";
    private static final int MODE_GET_MEMBER = 1;
    private static final int MODE_SET_ADMIN = 2;
    private static final int MODE_UNSET_ADMIN = 3;
    private static final int MODE_SET_MASTER = 4;
    private static final int MODE_BANISH = 5;

    private ListView listAdmin;
    private ListView listMember;

    private TextView textMasterName;
    private TextView textNoAdmin;
    private TextView textNoMember;

    private MaterialEditText editSearchAdmin;
    private MaterialEditText editSearchMember;

    private ImageView imageSearchAdmin;
    private ImageView imageSearchMember;

    private Team team;
    private User user;

    private MemberListAdapter normalMemberAdapter;
    private MemberListAdapter adminMemberAdapter;

    private ArrayList<Member> originMemberList;
    private ArrayList<Member> originAdminList;

    private Spinner spinnerAdminTarget;
    private Spinner spinnerAdminOrder;
    private Spinner spinnerMemberTarget;
    private Spinner spinnerMemberOrder;

    private boolean isSearchAdmin;
    private boolean isSearchMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memeber_manage);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");

        normalMemberAdapter = new MemberListAdapter(this, new ArrayList<Member>(), team.getIdx(), 1);
        adminMemberAdapter = new MemberListAdapter(this, new ArrayList<Member>(), team.getIdx(), 2);

        listAdmin = (ListView) findViewById(R.id.list_admin);
        listAdmin.setAdapter(adminMemberAdapter);
        listMember = (ListView) findViewById(R.id.list_member);
        listMember.setAdapter(normalMemberAdapter);

        textMasterName = (TextView) findViewById(R.id.text_master_name);
        textMasterName.setText(team.getMaster());

        textNoAdmin = (TextView) findViewById(R.id.text_no_admin);
        textNoMember = (TextView) findViewById(R.id.text_no_member);

        editSearchAdmin = (MaterialEditText) findViewById(R.id.edit_search_admin);
        editSearchAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editSearchAdmin.getError() != null) {
                    editSearchAdmin.setError(null);
                }

                if (s.length() == 0) {
                    adminMemberAdapter.setMemberList(originAdminList);
                    setListViewSize();
                }
            }
        });

        editSearchMember = (MaterialEditText) findViewById(R.id.edit_search_member);
        editSearchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editSearchMember.getError() != null) {
                    editSearchMember.setError(null);
                }

                if (s.length() == 0) {
                    normalMemberAdapter.setMemberList(originMemberList);
                    setListViewSize();
                }
            }
        });

        imageSearchAdmin = (ImageView) findViewById(R.id.image_search_admin);
        imageSearchAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSearchAdmin.length() > 0) {

                    ArrayList<Member> adminList = new ArrayList<>();

                    for (Member member : originAdminList) {
                        if (member.getNickname().contains(editSearchAdmin.getText().toString())) {
                            adminList.add(member);
                        }
                    }

                    adminMemberAdapter.setMemberList(adminList);

                    isSearchAdmin = true;
                    setListViewSize();
                } else {
                    editSearchAdmin.setError("닉네임을 입력해주세요.");
                }
            }
        });

        imageSearchMember = (ImageView) findViewById(R.id.image_search_member);
        imageSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editSearchMember.length() > 0) {
                    ArrayList<Member> memberList = new ArrayList<>();

                    for (Member member : originMemberList) {
                        if (member.getNickname().contains(editSearchMember.getText().toString())) {
                            memberList.add(member);
                        }
                    }

                    normalMemberAdapter.setMemberList(memberList);

                    isSearchMember = true;
                    setListViewSize();
                } else {
                    editSearchMember.setError("닉네임을 입력해주세요.");
                }
            }
        });

        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(this, R.array.order, android.R.layout.simple_spinner_dropdown_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> targetAdapter = ArrayAdapter.createFromResource(this, R.array.target, android.R.layout.simple_spinner_dropdown_item);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        spinnerAdminOrder = (Spinner)findViewById(R.id.spinner_order_admin);
        spinnerAdminOrder.setAdapter(orderAdapter);

        spinnerAdminTarget = (Spinner)findViewById(R.id.spinner_target_admin);
        spinnerAdminTarget.setAdapter(targetAdapter);

        spinnerMemberOrder = (Spinner)findViewById(R.id.spinner_order_member);
        spinnerMemberOrder.setAdapter(orderAdapter);

        spinnerMemberTarget = (Spinner)findViewById(R.id.spinner_target_member);
        spinnerMemberTarget.setAdapter(targetAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpConnection httpConnection = new HttpConnection(this, "teamIdx=" + team.getIdx(), "getMember.php", httpCallBack);
        httpConnection.execute();
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        Intent intent = new Intent(this, TeamSettingActivity.class);
        intent.putExtra("login_user", user);
        intent.putExtra("team", team);
        startActivity(intent);

        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, TeamSettingActivity.class);
        intent.putExtra("login_user", user);
        intent.putExtra("team", team);
        startActivity(intent);

        finish();
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

    private void setListViewSize() {
        if (adminMemberAdapter.getCount() > 0) {
            setListViewHeightBasedOnItems(listAdmin, listAdmin.getCount());

            if (textNoAdmin.getVisibility() == View.VISIBLE) {
                textNoAdmin.setVisibility(View.GONE);
            }
        } else {
            ViewGroup.LayoutParams params = listAdmin.getLayoutParams();
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            listAdmin.setLayoutParams(params);
            listAdmin.requestLayout();

            if (textNoAdmin.getVisibility() == View.GONE) {
                textNoAdmin.setVisibility(View.VISIBLE);

                if (isSearchAdmin) {
                    textNoAdmin.setText("검색 결과가 없습니다.");
                } else {
                    textNoAdmin.setText("관리자가 존재하지 않습니다.");
                }
            }
        }

        if (normalMemberAdapter.getCount() > 0) {
            setListViewHeightBasedOnItems(listMember, listMember.getCount());

            if (textNoMember.getVisibility() == View.VISIBLE) {
                textNoMember.setVisibility(View.GONE);
            }
        } else {
            ViewGroup.LayoutParams params = listMember.getLayoutParams();
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
            listMember.setLayoutParams(params);
            listMember.requestLayout();

            if (textNoMember.getVisibility() == View.GONE) {
                textNoMember.setVisibility(View.VISIBLE);

                if (isSearchAdmin) {
                    textNoMember.setText("검색 결과가 없습니다.");
                } else {
                    textNoMember.setText("일반 멤버가 존재하지 않습니다.");
                }
            }
        }

        if (isSearchMember) {
            isSearchMember = false;
        }

        if (isSearchAdmin) {
            isSearchAdmin = false;
        }
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");
                int mode = jsonObject.getInt("mode");

                if (mode == MODE_GET_MEMBER) {
                    normalMemberAdapter.getMemberList().clear();
                    adminMemberAdapter.getMemberList().clear();

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

                            originAdminList = adminMemberAdapter.getMemberList();
                            originMemberList = normalMemberAdapter.getMemberList();

                            normalMemberAdapter.notifyDataSetChanged();
                            adminMemberAdapter.notifyDataSetChanged();
                        }
                    } else {

                    }
                } else if (mode == MODE_SET_ADMIN) {
                    if (resultCode == Constant.SUCCESS) {
                        Member member = normalMemberAdapter.getMemberList().get(normalMemberAdapter.getCurrentPos());
                        member.setLevel(2);
                        adminMemberAdapter.add(member);
                        normalMemberAdapter.getMemberList().remove(normalMemberAdapter.getCurrentPos());
                        originMemberList = normalMemberAdapter.getMemberList();
                    } else {

                    }
                } else if (mode == MODE_UNSET_ADMIN) {
                    if (resultCode == Constant.SUCCESS) {
                        Member member = adminMemberAdapter.getMemberList().get(adminMemberAdapter.getCurrentPos());
                        member.setLevel(1);
                        normalMemberAdapter.add(member);
                        adminMemberAdapter.getMemberList().remove(adminMemberAdapter.getCurrentPos());
                        originAdminList = adminMemberAdapter.getMemberList();
                    } else {

                    }
                } else if (mode == MODE_SET_MASTER) {
                    if (resultCode == Constant.SUCCESS) {
/*                        user.setLevel(2);
                        team.setMaster(((Member)adminMemberAdapter.getItem(adminMemberAdapter.getCurrentPos())).getNickname());

                        Intent intent = new Intent(MemberManageActivity.this, TeamPageActivity.class);
                        intent.putExtra("login_user", user);
                        intent.putExtra("team", team);
                        startActivity(intent);*/

                        finish();
                    } else {

                    }
                } else if (mode == MODE_BANISH) {
                    if (resultCode == Constant.SUCCESS) {
                        normalMemberAdapter.getMemberList().remove(normalMemberAdapter.getCurrentPos());
                        originMemberList = normalMemberAdapter.getMemberList();
                    } else {

                    }
                }

                Log.d(TAG, jsonObject.toString());

                setListViewSize();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}