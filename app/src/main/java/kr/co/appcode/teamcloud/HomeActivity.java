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
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private User user;
    private TextView textServer;
    private TextView textServer2;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        callbackManager = CallbackManager.Factory.create();
        user = getIntent().getParcelableExtra("login_user");

        TextView textId = (TextView) findViewById(R.id.text_id);
        TextView textName = (TextView) findViewById(R.id.text_name);
        TextView textKey = (TextView) findViewById(R.id.text_key);
        TextView textType = (TextView) findViewById(R.id.text_type);
        textServer2 = (TextView)findViewById(R.id.text_server2);
        textServer = (TextView)findViewById(R.id.text_server);

        textId.setText(user.getId());
        textName.setText(user.getName());
        textKey.setText(user.getSessionInfo());
        textType.setText(user.getAccountType());

        Button btn = (Button) findViewById(R.id.btn_refresh);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpPostAsyncTask httpPostAsyncTask = new HttpPostAsyncTask();
                httpPostAsyncTask.execute();
            }
        });

        LoginButton loginButton = (LoginButton) findViewById(R.id.btn_facebook_login);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("result", object.toString());

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, error.toString());
            }
        });

        if(user.getAccountType().equals("facebook")){
            loginButton.setVisibility(View.VISIBLE);
            textServer2.setVisibility(View.GONE);
            textServer.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_logout){
            SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
            SharedPreferences.Editor spEditor = sp.edit();

            spEditor.remove("id")
                    .remove("password")
                    .remove("type");

            spEditor.apply();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                url = new URL(Constant.SERVER_URL+"main.php");
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
            textServer.setText("Session id : " + result);
            if(cookie != null){
                Log.d(TAG, cookie);
                textServer2.setText("Session key : " + cookie);
            }
            progressDialog.dismiss();
        }
    }
}
