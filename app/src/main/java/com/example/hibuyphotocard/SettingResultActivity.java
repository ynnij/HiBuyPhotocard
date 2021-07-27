package com.example.hibuyphotocard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SettingResultActivity extends AppCompatActivity {
    ImageButton ImageButton_푸시알림;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);


        ImageButton_푸시알림 = findViewById(R.id.ImageButton_푸시알림);
        ImageButton_푸시알림.setClickable(true);
        ImageButton_푸시알림.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent( SettingResultActivity.this, PushSettingActivity.class);
                startActivity(intent);
            }
        });

    }
}