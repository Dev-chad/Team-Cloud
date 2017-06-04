package kr.co.appcode.teamcloud;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BoardListDialog extends AppCompatActivity {

    private ListView listBoard;
    private ArrayAdapter<String> adapter;
    private ArrayList<Board> boardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_board_list);

        Team team = getIntent().getParcelableExtra("team");

        listBoard = (ListView) findViewById(R.id.list_board);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listBoard.setAdapter(adapter);
        listBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setResult(RESULT_OK, getIntent().putExtra("board", boardList.get(position)));
                finish();
            }
        });

        boardList = new ArrayList<>();

        HttpConnection httpConnection = new HttpConnection("teamIdx=" + team.getIdx(), "getTeamBoard.php", httpCallBack);
        httpConnection.execute();
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                if (jsonObject.getInt("resultCode") == Constant.SUCCESS) {
                    int count = jsonObject.getInt("count");

                    for (int i = 0; i < count; i++) {
                        Board board = new Board(jsonObject.getString(i+"_idx"), jsonObject.getString(i + "_name"), jsonObject.getInt(i + "_writeAuth"), jsonObject.getInt(i + "_readAuth"));
                        boardList.add(board);
                        adapter.add(board.getName());
                    }

                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


}
