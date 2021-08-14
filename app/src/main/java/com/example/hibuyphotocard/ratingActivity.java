package com.example.hibuyphotocard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ratingActivity extends AppCompatActivity {

    //파이어베이스 연결
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private Intent intent;

    int deliveryScore ;
    int mannerScore ;
    int itemScore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);



        ToggleButton fastButton = findViewById(R.id.fastButton);
        ToggleButton normalButton = findViewById(R.id.normalButton);
        ToggleButton slowButton = findViewById(R.id.slowButton);
        ToggleButton kindButton = findViewById(R.id.kindButton);
        ToggleButton sosoButton = findViewById(R.id.sosoButton);
        ToggleButton badButton = findViewById(R.id.badButton);
        ToggleButton greatButton = findViewById(R.id.greatButton);
        ToggleButton worstButton = findViewById(R.id.worstButton);

        Button checkButton = findViewById(R.id.checkButton);

        //버튼 미클릭 시 확인 버튼 클릭 안되게 하기
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((fastButton.isChecked()|normalButton.isChecked()|slowButton.isChecked())&(kindButton.isChecked()|sosoButton.isChecked()|badButton.isChecked())&
                        (greatButton.isChecked()|worstButton.isChecked())){
                    //확인 버튼을 클릭하면 점수를 DB에 저장
                    addScore(deliveryScore,mannerScore,itemScore);
                    rateState();
                    Intent intent = new Intent(ratingActivity.this, BuyMainActivity.class); //시작 페이지, 이동할 페이지
                    intent.putExtra("rating","finish");
                    //Toast.makeText(ratingActivity.this, "평가 완료", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    //상태 저장
                }
                else{
                    Toast.makeText(ratingActivity.this, "평가 버튼을 클릭해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });








        //토큰 버튼 점수 부여
        fastButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore += 3;
                }
            }
        });

        normalButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore += 1;
                }
            }
        });

        slowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore -= 1;
                }
            }
        });

        kindButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore += 3;
                }
            }
        });

        sosoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore += 1;
                }
            }
        });

        badButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore -= 1;
                }
            }
        });

        greatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    itemScore += 2;
                }
            }
        });

        worstButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    itemScore -= 1;
                }
            }
        });


    }

    //점수를 Realtime DB로 넘기는 함수
    public void addScore(int delivery, int manner, int item){
        ScoreList ScoreList = new ScoreList(delivery, manner, item);
        //점수 저장 경로
        intent = getIntent();
        String nickName = intent.getStringExtra("userName");
        databaseReference.child("id_list").child(nickName).child("mypage").setValue(ScoreList);
    }

    //평가 완료되었다고 표시
    public void rateState(){
        intent = getIntent();
        String sellId = intent.getStringExtra("sellID");
        databaseReference.child("Sell").child(sellId).child("rateState").setValue(true);
    }


}