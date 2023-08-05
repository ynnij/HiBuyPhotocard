package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ratingActivity extends AppCompatActivity {

    //파이어베이스 연결
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private DatabaseReference mypage;
    private Intent intent;

    int deliveryScore ;
    int mannerScore ;
    int itemScore ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }


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
                    Toast.makeText(ratingActivity.this, "평가 버튼을 모두 클릭해주세요", Toast.LENGTH_SHORT).show();
                }

            }
        });

        intent = getIntent();
        String nickName = intent.getStringExtra("userName");

        mypage = databaseReference.child("id_list").child(nickName).child("mypage");
        mypage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ScoreList scoreList = snapshot.getValue(ScoreList.class);
                deliveryScore = scoreList.getDeliveryScore();
                mannerScore = scoreList.getMannerScore();
                itemScore = scoreList.getItemScore();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        //토큰 버튼 점수 부여
        fastButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore += 3;
                    normalButton.setChecked(false);
                    slowButton.setChecked(false);
                    if(deliveryScore>100)
                        deliveryScore = 100;
                    else if(deliveryScore<0)
                        deliveryScore = 0;

//                    if(deliveryScore>=100){
//                        deliveryScore = 100;
//                    }
//                    else if (deliveryScore>=0 & deliveryScore <100){
//                        deliveryScore += 3;
//                    }
//                    else{
//                        deliveryScore = 0;
//                    }
                }
            }
        });

        normalButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore += 1;
                    fastButton.setChecked(false);
                    slowButton.setChecked(false);
                    if(deliveryScore>100)
                        deliveryScore = 100;
                    else if(deliveryScore<0)
                        deliveryScore = 0;
//
//                    if(deliveryScore>=100){
//                        deliveryScore = 100;
//                    }
//                    else if (deliveryScore>=0 & deliveryScore <100){
//                        deliveryScore += 1;
//                    }
//                    else{
//                        deliveryScore = 0;
//                    }
                }
            }
        });

        slowButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    deliveryScore -= 1;
                    fastButton.setChecked(false);
                    normalButton.setChecked(false);
                    if(deliveryScore>100)
                        deliveryScore = 100;
                    else if(deliveryScore<0)
                        deliveryScore = 0;
//
//                    if(deliveryScore>=100){
//                        deliveryScore = 100;
//                    }
//                    else if (deliveryScore>0 & deliveryScore <100){
//                        deliveryScore -= 1;
//                    }
//                    else{
//                        deliveryScore = 0;
//                    }
                }
            }
        });

        kindButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore += 3;
                    sosoButton.setChecked(false);
                    badButton.setChecked(false);
                    if(mannerScore>100)
                        mannerScore = 100;
                    else if(mannerScore<0)
                        mannerScore = 0;

//                    if(mannerScore>=100){
//                        mannerScore = 100;
//                    }
//                    else if (mannerScore>=0 & mannerScore <100){
//                        mannerScore += 3;
//                    }
//                    else{
//                        mannerScore = 0;
//                    }
                }
            }
        });

        sosoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore += 1;
                    kindButton.setChecked(false);
                    badButton.setChecked(false);
                    if(mannerScore>100)
                        mannerScore = 100;
                    else if(mannerScore<0)
                        mannerScore = 0;
//                    if(mannerScore>=100){
//                        mannerScore = 100;
//                    }
//                    else if (mannerScore>=0 & mannerScore <100){
//                        mannerScore += 1;
//                    }
//                    else{
//                        mannerScore = 0;
//                    }
                }
            }
        });

        badButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mannerScore -= 1;
                    kindButton.setChecked(false);
                    sosoButton.setChecked(false);
                    if(mannerScore>100)
                        mannerScore = 100;
                    else if(mannerScore<0)
                        mannerScore = 0;
//                    if(mannerScore>=100){
//                        mannerScore = 100;
//                    }
//                    else if (mannerScore>0 & mannerScore <100){
//                        mannerScore -= 1;
//                    }
//                    else{
//                        mannerScore = 0;
//                    }
                }
            }
        });

        greatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    itemScore += 2;
                    worstButton.setChecked(false);
                    if(itemScore>100)
                        itemScore = 100;
                    else if(itemScore<0)
                        itemScore = 0;
//                    if(itemScore>=100){
//                        itemScore = 100;
//                    }
//                    else if (itemScore>=0 & itemScore <100){
//                        itemScore += 2;
//                    }
//                    else{
//                        itemScore = 0;
//                    }
                }
            }
        });

        worstButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    itemScore -= 1;
                    greatButton.setChecked(false);
                    if(itemScore>100)
                        itemScore = 100;
                    else if(itemScore<0)
                        itemScore = 0;
//                    if(itemScore>=100){
//                        itemScore = 100;
//                    }
//                    else if (itemScore>0 & itemScore <100){
//                        itemScore -= 1;
//                    }
//                    else{
//                        itemScore = 0;
//                    }
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