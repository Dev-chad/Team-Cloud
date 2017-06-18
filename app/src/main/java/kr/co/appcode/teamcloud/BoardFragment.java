package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class BoardFragment extends android.app.Fragment {
    private static final String TAG = "BoardFragment";

    private ListView listContents;

    private User user;
    private Team team;
    private Board board;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        user = getArguments().getParcelable("login_user");
        team = getArguments().getParcelable("team");
        board = getArguments().getParcelable("board");


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.putExtra("team", team);
                intent.putExtra("login_user", user);
                intent.putExtra("board", board);
                startActivity(intent);
            }
        });

        ContentsListAdapter adapter = new ContentsListAdapter(this, new ArrayList<Content>());

        listContents = (ListView) view.findViewById(R.id.list_contents);
        listContents.setAdapter(adapter);
        listContents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ContentDetailActivity.class);
                intent.putExtra("board", board);
                intent.putExtra("content", (Content) listContents.getAdapter().getItem(position));
                intent.putExtra("login_user", user);
                intent.putExtra("team", team);

                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String body = "teamIdx=" + team.getIdx() + "&boardIdx=" + board.getIdx();

        HttpConnection httpConnection = new HttpConnection(getActivity(), body, "getContents.php", httpCallBack);
        httpConnection.execute();
    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");

                if (resultCode == Constant.SUCCESS) {
                    ContentsListAdapter contentsListAdapter = (ContentsListAdapter) listContents.getAdapter();
                    if (contentsListAdapter.getCount() > 0) {
                        contentsListAdapter.getContentList().clear();
                    }

                    for (int i = 0; i < jsonObject.getInt("count"); i++) {
                        Content content = new Content(jsonObject.getString(i + "_idx"), jsonObject.getString(i + "_boardName"), jsonObject.getString(i + "_writer"), jsonObject.getString(i + "_title"), jsonObject.getString(i + "_desc"), jsonObject.getString(i + "_date"), jsonObject.getInt(i + "_readAuth"), jsonObject.getInt(i + "_ver"));

                        if (jsonObject.has(i + "_fileName")) {
                            content.setFileName(jsonObject.getString(i + "_fileName"));
                            content.setFileUrl(jsonObject.getString(i + "_fileUrl"));
                            content.setFileType(jsonObject.getString(i + "_fileType"));
                            content.setFileSize(jsonObject.getLong(i + "_fileSize"));
                        }

                        contentsListAdapter.add(content);
                    }

                    contentsListAdapter.notifyDataSetChanged();
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, jsonObject.toString());
        }
    };
}
