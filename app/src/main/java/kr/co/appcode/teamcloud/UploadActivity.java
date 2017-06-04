package kr.co.appcode.teamcloud;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadActivity extends AppCompatActivity {

    private RelativeLayout layoutCategory;
    private RelativeLayout layoutFile;

    private MaterialEditText editTitle;
    private EditText editDesc;

    private TextView textCategory;
    private TextView textFileName;
    private TextView textFileSize;

    private ImageView imageFile;
    private ImageView imageClear;

    private Board board;
    private User user;
    private Team team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        team = getIntent().getParcelableExtra("team");
        user = getIntent().getParcelableExtra("login_user");
        board = getIntent().getParcelableExtra("board");

        layoutCategory = (RelativeLayout) findViewById(R.id.layout_category);
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, BoardListDialog.class);
                intent.putExtra("team", team);
                startActivityForResult(intent, 1);
            }
        });

        layoutFile = (RelativeLayout) findViewById(R.id.layout_file);

        editTitle = (MaterialEditText) findViewById(R.id.edit_title);
        editDesc = (EditText) findViewById(R.id.edit_desc);

        textCategory = (TextView) findViewById(R.id.text_category);
        textFileName = (TextView) findViewById(R.id.text_file_name);
        textFileSize = (TextView) findViewById(R.id.text_file_size);


        imageFile = (ImageView) findViewById(R.id.image_file);

        imageClear = (ImageView) findViewById(R.id.image_clear);
        imageClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder checkDialog = new AlertDialog.Builder(UploadActivity.this);

                checkDialog
                        .setTitle("파일 첨부 삭제")
                        .setMessage("첨부 파일을 지우시겠습니까?");

                checkDialog.setPositiveButton("지우기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layoutFile.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });

                checkDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                checkDialog.show();
            }
        });

        Button btnAddFile = (Button) findViewById(R.id.btn_file_Add);
        btnAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //사용자가 임의로 권한 취소시킨 경우
                        AlertDialog.Builder ad = new AlertDialog.Builder(UploadActivity.this);

                        ad.setTitle("저장공간 권한 허용 요청");
                        ad.setMessage("파일 첨부 기능은 저장공간 권한을 요구합니다.");

                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                        });

                        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                            }
                        });

                        ad.show();
                    } else {
                        SharedPreferences sp = getSharedPreferences("app_info", MODE_PRIVATE);
                        if (sp.contains("CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION")) {
                            AlertDialog.Builder ad = new AlertDialog.Builder(UploadActivity.this);

                            ad.setTitle("저장공간 권한 허용 요청");
                            ad.setMessage("파일 첨부 기능은 저장공간 권한을 요구합니다.\n[설정]>[권한]에서 해당 권한을 허용해 주세요.\n\n해당 설정으로 이동하시겠습니까?");

                            ad.setPositiveButton("이동", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });

                            ad.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(UploadActivity.this, "저장공간 권한 허용 후 이용 가능합니다.", Toast.LENGTH_LONG).show();
                                }
                            });

                            ad.show();
                        } else {
                            SharedPreferences.Editor spEditor = sp.edit();
                            spEditor.putBoolean("CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION", true);
                            spEditor.apply();

                            AlertDialog.Builder ad = new AlertDialog.Builder(UploadActivity.this);

                            ad.setTitle("저장공간 권한 허용 요청");
                            ad.setMessage("파일 첨부 기능은 저장공간 권한을 요구합니다.");

                            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                }
                            });

                            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    ActivityCompat.requestPermissions(UploadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

                                }
                            });

                            ad.show();

                        }
                    }
                } else {
                    Intent intent = new Intent(UploadActivity.this, FileBrowserActivity.class);
                    startActivityForResult(intent, 2);
                }
            }
        });


        if (board != null) {
            textCategory.setText(board.getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (editTitle.length() > 0 || editDesc.length() > 0) {
                    AlertDialog.Builder checkDialog = new AlertDialog.Builder(this);

                    checkDialog
                            .setTitle("업로드에서 나가기")
                            .setMessage("게시물 작성을 취소하고 나가시겠습니까?");

                    checkDialog.setPositiveButton("나가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.dismiss();
                        }
                    });

                    checkDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    checkDialog.show();
                } else {
                    finish();
                }
                return true;
            case R.menu.menu_upload:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가

                    Intent intent = new Intent(UploadActivity.this, FileBrowserActivity.class);
                    startActivityForResult(intent, 2);
                } else {
                    // 권한 거부
                    Toast.makeText(this, "저장소에 대한 권한을 허가해야 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                board = data.getParcelableExtra("board");
                textCategory.setText(board.getName());
            } else {
                UploadFile file = data.getParcelableExtra("selectedFile");
                layoutFile.setVisibility(View.VISIBLE);
                textFileName.setText(file.getFileName());
                textFileSize.setText(file.getFileSize());
            }
        }
    }

    HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void CallBackResult(JSONObject jsonObject) {
            try {
                int resultCode = jsonObject.getInt("resultCode");

                if (resultCode == Constant.SUCCESS) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
