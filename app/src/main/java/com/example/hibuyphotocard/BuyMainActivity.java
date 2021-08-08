package com.example.hibuyphotocard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BuyMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SellItemList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_main);

        //-----------버튼 이벤트 처리
//        Button button = findViewById(R.id.sellButton); //sellIngButton으로 바꾸면 강제 종료 됨 왤까
//        button.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            //버튼 클릭 마다 예약 -> 판매 -> 예약 이런식으로 버튼 바뀌게
//            //버튼 클릭시 DB의 state
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_UP:
//                        button.setBackgroundResource(R.drawable.sell_button_second);
//                        button.setText("예약중");
//                        //break;
//                }
//                return true; //false로 지정하면 버튼 클릭시 앱 강제종료됨
//
//            }
//        });

        Button sellMoveButton = findViewById(R.id.sellButton);
        sellMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BuyMainActivity.this, SellMainActivity.class); //시작 페이지, 이동할 페이지
                startActivity(intent);
            }
        });

        //-----------뒤로 가기 이벤트 처리
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()){
//                    case R.id.sellButton:
//
//                }
//                button.setBackgroundResource(R.drawable.sell_button_second);
//            }
//        });




        //-----------판매글 리스트 처리
        recyclerView = findViewById(R.id.recyclerview); //리사이클러뷰 아이디 연결
        recyclerView.setHasFixedSize(true); //기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //sell 객체를 담을 arraylist(어댑터 쪽으로 날리기 위함)

        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동

        databaseReference = database.getReference("Sell"); //DB 테이블 연결
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
                arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){ //반복문으로 데이터 List 추출해냄
                    SellItemList sellItemList = snapshot.getValue(SellItemList.class); //만들어뒀던  User 겍체에 데이터를 담는다
                    arrayList.add(sellItemList); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                //DB를 가져오던 중 에러 발생 시
                Log.e("MainActivity",String.valueOf(databaseError.toException())); //에러문 출력
            }
        });

        adapter = new BuyAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결


    }
}