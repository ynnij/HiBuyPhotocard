package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.HashMap;
import java.util.Set;

public class WishListActivity extends AppCompatActivity {
    private Button backButton;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<SearchItemList> wishItem; //리싸이클러뷰애 적용할 아이템 리스트

    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference wishListDB;
    private DatabaseReference SellItemList;

    private String email;
    private String nickName;
    private ArrayList wish; // firebase에서 받아온 user의 찜 목록

    //하단바
    private LinearLayout contractBtn;
    private LinearLayout homeBtn;
    private LinearLayout chatBtn;
    private LinearLayout mypageBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list); // xml 연결

        ActionBar actionBar = getSupportActionBar();  //액션바 숨기기
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

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
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageMain.class);
                startActivity(intent);
                finish();
            }
        });

        contractBtn = findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backButton = findViewById(R.id.backButton); //뒤로가기 버튼
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); //뒤로가기 -> 이전 activity는 intent 후에 종료 X
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();  // 현재 로그인한 계정 저장
            }
        }

        wishItem = new ArrayList<>();
        wishItem.clear();

        /*
        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if(user.equals(email)){
                        nickName = datas.getKey();
                        wishListDB = mDatabase.child("id_list").child(nickName).child("wishList"); //유저의 찜목록 가져오기
                        wishListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String,String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                                if(hashMap !=null){
                                    Set<String> keySet = hashMap.keySet();
                                    wish = new ArrayList<String>(keySet);
                                }
                                if(wish !=null){  // 위시리스트가 있는 사용자는 위시리스트 불러오기
                                    SellItemList = mDatabase.child("Sell");
                                    SellItemList.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sellData) {
                                            for(DataSnapshot snapshot: sellData.getChildren()){
                                                SearchItemList itemList = snapshot.getValue(SearchItemList.class);
                                                if(wish.contains(itemList.getSellID())){
                                                    wishItem.add(itemList);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                    });
                                }
                                else {  //위시리스트가 없는 경우 아무것도 표시하지 않음

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

        */


        recyclerView = findViewById(R.id.recyclerviewWish);

        int numberOfColumns = 2;
        int spacing = 30;
        boolean includeEdge = true;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(numberOfColumns, spacing, includeEdge));

        adapter = new SearchAdapter(wishItem,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        wishItem.clear();
        getWish();
        adapter.notifyDataSetChanged();
    }

    public void getWish(){
        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if(user.equals(email)){
                        nickName = datas.getKey();
                        wishListDB = mDatabase.child("id_list").child(nickName).child("wishList"); //유저의 찜목록 가져오기
                        wishListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String,String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                                if(hashMap !=null){
                                    Set<String> keySet = hashMap.keySet();
                                    wish = new ArrayList<String>(keySet);
                                }
                                if(wish !=null){  // 위시리스트가 있는 사용자는 위시리스트 불러오기
                                    SellItemList = mDatabase.child("Sell");
                                    SellItemList.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sellData) {
                                            for(DataSnapshot snapshot: sellData.getChildren()){
                                                SearchItemList itemList = snapshot.getValue(SearchItemList.class);
                                                if(wish.contains(itemList.getSellID())){
                                                    wishItem.add(itemList);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                                else {  //위시리스트가 없는 경우 아무것도 표시하지 않음

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
    }
}
