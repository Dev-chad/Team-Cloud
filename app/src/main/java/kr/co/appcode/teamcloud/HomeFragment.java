package kr.co.appcode.teamcloud;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Profile;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends android.app.Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private static final int MODE_LIST = 1;
    private static final int MODE_GRID = 2;

    private ListView listLatestContent;
    private ListView listLatestFile;

    private GridView gridLatestFile;

    private ImageView imageTeam;
    private ImageView imageList;
    private ImageView imageGrid;

    private ProgressBar progressBar;

    private TextView teamName;
    private TextView textUsedCapacity;
    private TextView textMaxCapacity;

    private int viewMode = MODE_GRID;

    private User user;
    private Profile profile;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = getArguments().getParcelable("login_user");
        if (user != null) {
            if (user.getAccountType().equals("facebook")) {
                profile = Profile.getCurrentProfile();
            }
        }

        listLatestContent = (ListView) view.findViewById(R.id.list_latest_content);
        listLatestFile = (ListView) view.findViewById(R.id.list_latest_file);
        gridLatestFile = (GridView) view.findViewById(R.id.grid_latest_file);

        imageTeam = (ImageView) view.findViewById(R.id.image_team);
        imageGrid = (ImageView) view.findViewById(R.id.image_grid);
        imageList = (ImageView) view.findViewById(R.id.image_list);

        teamName = (TextView) view.findViewById(R.id.text_team_name);
        textUsedCapacity = (TextView) view.findViewById(R.id.text_used_capacity);
        textMaxCapacity = (TextView) view.findViewById(R.id.text_max_capacity);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_capacity);

        LatestContentListAdapter adapterContent = new LatestContentListAdapter(this, new ArrayList<LatestContentItem>());

        ArrayList<LatestFileItem> fileList = new ArrayList<>();

        LatestFileListAdapter fileListAdapter = new LatestFileListAdapter(this, fileList);
        LatestFileGridAdapter fileGridAdapter = new LatestFileGridAdapter(this, fileList);

        listLatestContent.setAdapter(adapterContent);
        listLatestFile.setAdapter(fileListAdapter);
        gridLatestFile.setAdapter(fileGridAdapter);

        imageGrid.setOnClickListener(this);
        imageList.setOnClickListener(this);

        teamName.setText(getArguments().getString("teamName"));

        HashMap<String, String> values = new HashMap<>();
        values.put("nickname", user.getNickname());
        values.put("teamName", teamName.getText().toString());

        HttpConnection httpConnection = new HttpConnection(getActivity(), values, httpCallBack);
        httpConnection.setMode(HttpConnection.MODE_TEAM_HOME);
        httpConnection.execute();

        return view;
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


    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.image_grid || id == R.id.image_list) {
            if (viewMode == MODE_LIST) {
                imageList.setAlpha(0.2f);
                imageGrid.setAlpha(1.0f);
                viewMode = MODE_GRID;
                listLatestFile.setVisibility(View.GONE);
                gridLatestFile.setVisibility(View.VISIBLE);
            } else {
                imageList.setAlpha(1.0f);
                imageGrid.setAlpha(0.2f);
                viewMode = MODE_LIST;
                listLatestFile.setVisibility(View.VISIBLE);
                gridLatestFile.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int mode = jsonObject.getInt("mode");
                int resultCode = jsonObject.getInt("resultCode");

                if (mode == HttpConnection.MODE_TEAM_HOME) {
                    if (resultCode == Constant.SUCCESS) {
                        setCapacity(jsonObject.getDouble("usedCapacity"), jsonObject.getInt("maxCapacity"));

                    }
                }

            } catch (Exception e) {

            }
            Log.d(TAG, jsonObject.toString());

        }
    };

    public void setCapacity(double min, int max) {
        String unit = "";
        double result = 0;

        max *= 1024;

        if (min < 0.000977) {
            result = min * 1048576;
            unit = "B";

        } else if (min < 1) {
            result = min * 1024;
            unit = "KB";

        } else if (min < 1024) {
            result = min;
            unit = "MB";

        } else if (min < 1048576) {
            result = min / 1024;
            unit = "GB";

        } else if (min < 1073700000) {
            result = min / 1048576;
            unit = "TB";
        }

        while(min > 0 && min < 1024){
            min *= 10;
            max *= 10;
        }

        progressBar.setMax(max);
        progressBar.setProgress((int)min);

        if(String.format("%.1f", result).contains(".0")){
            textUsedCapacity.setText(String.format("%d%s", Math.round(result), unit));
        } else {
            textUsedCapacity.setText(String.format("%.1f%s", result, unit));
        }
    }
}
