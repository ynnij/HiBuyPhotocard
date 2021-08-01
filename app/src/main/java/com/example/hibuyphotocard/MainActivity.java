package com.example.hibuyphotocard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    TextView TextView_설정;
    TextView TextView_개발자문의;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        TextView_설정 = findViewById(R.id.TextView_설정);
        TextView_설정.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, SettingResultActivity.class);
                startActivity(intent);
            }
        });

        TextView_개발자문의 = findViewById(R.id.TextView_개발자문의);
        TextView_개발자문의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });


    }
}