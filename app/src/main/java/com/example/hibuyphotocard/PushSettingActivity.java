package com.example.hibuyphotocard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class PushSettingActivity extends AppCompatActivity {

    Switch Switch_키워드알림;
    Switch Switch_채팅알림;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushsetting);

        Switch_키워드알림  = findViewById(R.id.Switch_키워드알림);
        Switch_채팅알림    = findViewById(R.id.Switch_채팅알림);


        Switch_키워드알림.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Switch_키워드알림.setChecked(isChecked);
            }
        });

        Switch_채팅알림.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Switch_채팅알림.setChecked(isChecked);

            }

        });






    }

}