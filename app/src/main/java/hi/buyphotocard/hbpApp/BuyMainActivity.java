package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BuyMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SellItemList> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    //
    private FirebaseUser user;
    ArrayList<SellItemList> buyItem;
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference buyListDB;
    private DatabaseReference SellItemList;

    private String email;
    private String nickName;
    private ArrayList buy; // firebase에서 받아온 user의 찜 목록
    //

    //하단바
    private LinearLayout contractBtn;
    private LinearLayout homeBtn;
    private LinearLayout chatBtn;
    private LinearLayout mypageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

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
                finish();
            }
        });





        //-----------판매글 리스트 처리
        recyclerView = findViewById(R.id.recyclerviewBuy); //리사이클러뷰 아이디 연결
        recyclerView.setHasFixedSize(true); //기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();
                Log.d("확인",email);

            }
        }

        buyItem = new ArrayList<>();
        buyItem.clear();

        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if(user.equals(email)){
                        Log.d("확인", "같은 이메일은 "+email);
                        nickName = datas.getKey();

                        buyListDB = mDatabase.child("id_list").child(nickName).child("buy"); //유저의 찜목록 가져오기
                        buyListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                buy = (ArrayList)dataSnapshot.getValue(); //firebase에서 wishList 받아오면 데이터 필터링 시작
                                if(buy != null){
                                    SellItemList = mDatabase.child("Sell");
                                    SellItemList.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sellData) {
                                            for(DataSnapshot snapshot: sellData.getChildren()){
                                                SellItemList itemList = snapshot.getValue(SellItemList.class);
                                                if(buy.contains(itemList.getSellID())){
                                                    buyItem.add(itemList);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else{

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //


//        arrayList = new ArrayList<>(); //sell 객체를 담을 arraylist(어댑터 쪽으로 날리기 위함)
//
//        database = FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동
//
//        databaseReference = database.getReference("Sell"); //DB 테이블 연결
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
//                //파이어베이스 데이터베이스의 데이터를 받아오는 곳
//                arrayList.clear(); //기존 배열리스트가 존재하지 않게 초기화
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){ //반복문으로 데이터 List 추출해냄
//                    SellItemList sellItemList = snapshot.getValue(SellItemList.class); //만들어뒀던  User 겍체에 데이터를 담는다
//                    arrayList.add(sellItemList); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
//                }
//                adapter.notifyDataSetChanged(); //리스트 저장 및 새로고침
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
//                //DB를 가져오던 중 에러 발생 시
//                Log.e("MainActivity",String.valueOf(databaseError.toException())); //에러문 출력
//            }
//        });

        adapter = new BuyAdapter(buyItem,this);
        //adapter = new BuyAdapter(arrayList, this);
        recyclerView.setAdapter(adapter); //리사이클러뷰에 어댑터 연결


        //하단바
        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        chatBtn = findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FragmentActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        mypageBtn = findViewById(R.id.mypageBtn);
        mypageBtn.setBackgroundColor(Color.parseColor("#B1E3FF"));

        contractBtn = findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}