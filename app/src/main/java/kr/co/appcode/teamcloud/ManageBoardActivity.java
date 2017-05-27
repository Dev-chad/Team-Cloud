package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManageBoardActivity extends AppCompatActivity {
    private static final String TAG = "ManageBoardListActivity";
    private static final int MODE_GET_BOARD = 1;
    private static final int MODE_ADD_BOARD = 2;
    private static final int MODE_EDIT_BOARD = 3;
    private static final int MODE_REMOVE_BOARD = 4;

    private User user;
    private Team team;

    private ListView listBoard;
    private BoardListManageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_board);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("게시판 관리");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = getIntent().getParcelableExtra("login_user");
        team = getIntent().getParcelableExtra("team");

        adapter = new BoardListManageAdapter(this, team.getIdx(), new ArrayList<Board>());
        listBoard = (ListView) findViewById(R.id.list_board);
        listBoard.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        HttpConnection httpConnection = new HttpConnection("teamIdx=" + team.getIdx(), "getTeamBoard.php", httpCallBack);
        httpConnection.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == MODE_GET_BOARD) {
                    if (resultCode == Constant.SUCCESS) {
                        int count = jsonObject.getInt("count");

                        for (int i = 0; i < count; i++) {
                            Board board = new Board(jsonObject.getString(i + "_name"), jsonObject.getInt(i + "_writeAuth"), jsonObject.getInt(i + "_readAuth"));
                            adapter.add(board);
                        }

                        adapter.notifyDataSetChanged();
                    } else {

                    }
                } else if (mode == MODE_ADD_BOARD) {
                    if (resultCode == Constant.SUCCESS) {

                    } else {

                    }
                } else if (mode == MODE_EDIT_BOARD) {
                    if (resultCode == Constant.SUCCESS) {

                    } else {

                    }
                } else if (mode == MODE_REMOVE_BOARD) {
                    if (resultCode == Constant.SUCCESS) {
                        Toast.makeText(ManageBoardActivity.this, "게시판이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        adapter.getBoardList().remove(adapter.getCurrentPos());
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
