package kr.co.appcode.teamcloud;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "HomeActivity";

    private User user;
    private Profile profile;
    private ListView teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profile = Profile.getCurrentProfile();
        user = getIntent().getParcelableExtra("loginUser");
        Toast.makeText(this, user.getName()+"님 환영합니다.", Toast.LENGTH_SHORT).show();
        teamList = (ListView)findViewById(R.id.list_team);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for(int i=0; i<10; i++){
            adapter.add("팀 "+(i+1));
        }

        teamList.setAdapter(adapter);
        teamList.setOnItemClickListener(this);

        setListViewHeightBasedOnItems(teamList, 6);

        Button btnGetHeight = (Button)findViewById(R.id.btn_getHeight);
        btnGetHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, ""+teamList.getHeight(), Toast.LENGTH_SHORT).show();
            }
        });

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

    private class HttpPostAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;
        private URL url;
        private ProgressDialog progressDialog;
        private String cookie;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                url = new URL(Constant.SERVER_URL + "main.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("잠시 기다려주세요");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Cookie", user.getSessionInfo());
                conn.setDoInput(true);
                conn.setDoOutput(true);

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                cookie = conn.getHeaderField("Set-Cookie");

                return br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, result);
            if (cookie != null) {
                Log.d(TAG, cookie);
            }
            progressDialog.dismiss();
        }
    }
}
