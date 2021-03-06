package kr.co.appcode.teamcloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import java.util.Locale;

import static java.lang.String.format;

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

    private TextView textTeamName;
    private TextView textUsedCapacity;
    private TextView textMaxCapacity;
    private TextView textNoLatestContents;
    private TextView textNoLatestFile;

    private int viewMode = MODE_GRID;

    private ContentListAdapter contentListAdapter;
    private FileGridAdapter fileGridAdapter;
    private FileListAdapter fileListAdapter;

    private User user;
    private Profile profile;
    private Team team;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        user = getArguments().getParcelable("login_user");
        team = getArguments().getParcelable("team");

        if (user != null) {
            if (user.getAccountType().equals("facebook")) {
                profile = Profile.getCurrentProfile();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.putExtra("team", team);
                intent.putExtra("login_user", user);
                startActivity(intent);
            }
        });

        listLatestContent = (ListView) view.findViewById(R.id.list_latest_content);
        listLatestFile = (ListView) view.findViewById(R.id.list_latest_file);
        gridLatestFile = (GridView) view.findViewById(R.id.grid_latest_file);

        imageTeam = (ImageView) view.findViewById(R.id.image_team);
        imageGrid = (ImageView) view.findViewById(R.id.image_grid);
        imageList = (ImageView) view.findViewById(R.id.image_list);

        textTeamName = (TextView) view.findViewById(R.id.text_team_name);
        textUsedCapacity = (TextView) view.findViewById(R.id.text_used_capacity);
        textMaxCapacity = (TextView) view.findViewById(R.id.text_max_capacity);
        textNoLatestContents = (TextView) view.findViewById(R.id.text_no_latest_content);
        textNoLatestFile = (TextView) view.findViewById(R.id.text_no_latest_file);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_capacity);
        contentListAdapter = new ContentListAdapter(this, new ArrayList<Content>());

        ArrayList<Content> fileList = new ArrayList<>();

        fileListAdapter = new FileListAdapter(this, fileList);
        fileGridAdapter = new FileGridAdapter(this, fileList);

        listLatestContent.setAdapter(contentListAdapter);
        listLatestFile.setAdapter(fileListAdapter);
        gridLatestFile.setAdapter(fileGridAdapter);

        imageGrid.setOnClickListener(this);
        imageList.setOnClickListener(this);

        textTeamName.setText(team.getName());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String body = "nickname=" + user.getNickname() + "&teamIdx=" + team.getIdx();

        HttpConnection httpConnection = new HttpConnection(getActivity(), body, "getTeamHome.php", httpCallBack);
        httpConnection.execute();
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

    public void setCapacity(long min, int max) {

        double dSize = Double.parseDouble(String.valueOf(min));
        String unit;
        int count = 0;

        while (dSize >= 1024 && count < 5) {
            dSize /= 1024;
            count++;
        }

        if (count == 0) {
            unit = "B";
        } else if (count == 1) {
            unit = "KB";
        } else if (count == 2) {
            unit = "MB";
        } else if (count == 3) {
            unit = "GB";
        } else {
            unit = "TB";
        }

        progressBar.setMax(max*1073700000);

        progressBar.setProgress((int)min);

        String strSize = format(Locale.KOREA, "%.2f %s", dSize, unit);

        if (strSize.contains(".00")) {
            strSize = String.format(Locale.KOREA, "%d %s", (int) dSize, unit);
        } else if (strSize.charAt(strSize.length() - 1) == '0') {
            strSize = String.format(Locale.KOREA, "%.1f %s", dSize, unit);
        }

        textUsedCapacity.setText(strSize);
    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");

                if (resultCode == Constant.SUCCESS) {
                    team.setMaxCapacity(jsonObject.getInt("maxCapacity"));
                    team.setUsedCapacity(jsonObject.getLong("usedCapacity"));
                    team.setName(jsonObject.getString("teamName"));

                    setCapacity(team.getUsedCapacity(), team.getMaxCapacity());
                    textMaxCapacity.setText(String.valueOf(team.getMaxCapacity() + "GB"));
                    textTeamName.setText(team.getName());

                    int contentCount = jsonObject.getInt("contentCount");
                    int fileCount = jsonObject.getInt("fileCount");

                    if (contentCount > 0) {
                        if (textNoLatestContents.getVisibility() == View.VISIBLE) {
                            textNoLatestContents.setVisibility(View.GONE);
                        }

                        contentListAdapter.getContentList().clear();
                        fileListAdapter.getFileList().clear();
                        for (int i = 0; i < contentCount; i++) {
                            Content content = new Content(jsonObject.getString(i + "_idx"), jsonObject.getString(i + "_boardName"), jsonObject.getString(i + "_writer"), jsonObject.getString(i + "_title"), jsonObject.getString(i + "_desc"), jsonObject.getString(i + "_date"), jsonObject.getInt(i + "_readAuth"), jsonObject.getInt(i + "_ver"));
                            contentListAdapter.add(content);

                            if (jsonObject.has(i + "_fileName")) {
                                content.setFileName(jsonObject.getString(i + "_fileName"));
                                content.setFileUrl(jsonObject.getString(i + "_fileUrl"));
                                content.setFileType(jsonObject.getString(i + "_fileType"));
                                content.setFileSize(jsonObject.getLong(i + "_fileSize"));
                                fileGridAdapter.add(content);

                            }
                        }

                        contentListAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(listLatestContent, contentListAdapter.getCount());

                        if (fileCount > 0) {
                            if (textNoLatestFile.getVisibility() == View.VISIBLE) {
                                textNoLatestFile.setVisibility(View.GONE);
                            }

                            fileGridAdapter.notifyDataSetChanged();
                            fileListAdapter.notifyDataSetChanged();

                            setGridViewHeightBasedOnItems(gridLatestFile);
                            setListViewHeightBasedOnItems(listLatestFile, listLatestFile.getCount());
                        } else {
                            if (textNoLatestFile.getVisibility() == View.GONE) {
                                textNoLatestFile.setVisibility(View.VISIBLE);
                                if (fileListAdapter.getCount() > 0) {
                                    fileListAdapter.getFileList().clear();
                                }
                            }
                        }
                    } else {
                        if (textNoLatestContents.getVisibility() == View.GONE) {
                            textNoLatestContents.setVisibility(View.VISIBLE);
                            if (contentListAdapter.getCount() > 0) {
                                contentListAdapter.getContentList().clear();
                            }
                        }

                        if (textNoLatestFile.getVisibility() == View.GONE) {
                            textNoLatestFile.setVisibility(View.VISIBLE);
                            if (fileListAdapter.getCount() > 0) {
                                fileListAdapter.getFileList().clear();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, jsonObject.toString());
        }
    };
}
