package hi.buyphotocard.hbpApp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class SettingResultActivity extends AppCompatActivity {
    ImageButton ImageButton_푸시알림;
    ImageButton ImageButton_로그아웃;
    ImageButton ImageButton_계정탈퇴;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        ImageButton_푸시알림 = findViewById(R.id.ImageButton_푸시알림);
        ImageButton_푸시알림.setClickable(true);
        ImageButton_푸시알림.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( SettingResultActivity.this, PushSettingActivity.class);
                startActivity(intent);
            }
        });

        ImageButton_로그아웃 = findViewById(R.id.ImageButton_로그아웃);
        ImageButton_로그아웃.setClickable(true);
        ImageButton_로그아웃.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent( SettingResultActivity.this, LogoutSettingActivity.class);
//                startActivity(intent);

                ImageButton_로그아웃.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder ad = new AlertDialog.Builder(SettingResultActivity.this);
                        ad.setMessage("로그아웃하시겠습니까?");

                        ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });


                        ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        ad.show();
//
                    }
                });


            }
        });

        ImageButton_계정탈퇴 = findViewById(R.id.ImageButton_계정탈퇴);
        ImageButton_계정탈퇴.setClickable(true);
        ImageButton_계정탈퇴.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent( SettingResultActivity.this, PushSettingActivity.class);
//                startActivity(intent);


                AlertDialog.Builder ad= new AlertDialog.Builder(SettingResultActivity.this);
                ad.setTitle("탈퇴 하시겠습니까?");
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                ad.show();

            }
        });





    }
}