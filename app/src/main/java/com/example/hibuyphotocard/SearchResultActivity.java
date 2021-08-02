package com.example.hibuyphotocard;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> groupList; //이전 activity에서 넘어온 list
    private ArrayList<String> albumList;
    private ArrayList<String> memberList;

    private ArrayList<SearchItemList> itemList; //recyclerview로 넘길 리스트
    private FirebaseDatabase mDatabase;
    private DatabaseReference allSellList;

    private LinearLayout label_area; //라벨 붙일 곳

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result); // xml 연결

        ActionBar actionBar = getSupportActionBar(); //actionbar 숨기기
        actionBar.hide();

        //이전 activity에서 넘긴 데이터 받기
        groupList = getIntent().getStringArrayListExtra("groupArray");
        albumList = getIntent().getStringArrayListExtra("albumArray");
        memberList = getIntent().getStringArrayListExtra("memberArray");

//        Log.d("확인","그룹 "+ groupList.toString());
//        Log.d("확인","앨범 "+ albumList.toString());
//        Log.d("확인","멤버 "+ memberList.toString());

        //받은 태그 동적 생성
        label_area = findViewById(R.id.label_area);
        for(int i=0;i<groupList.size();i++){
            createTextView(groupList.get(i),label_area,"group");
        }
        for(int i=0;i<albumList.size();i++){
            createTextView(albumList.get(i),label_area,"album");
        }
        for(int i=0;i<memberList.size();i++){
            createTextView(memberList.get(i),label_area,"member");
        }

        itemList = new ArrayList<>();

        //데이터베이스 받아오기
        mDatabase = FirebaseDatabase.getInstance();
        allSellList = mDatabase.getReference("Sell");

        allSellList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){ //반복문으로 데이터 List 추출해냄
                    SearchItemList sellItemList = snapshot.getValue(SearchItemList.class); //만들어뒀던  User 겍체에 데이터를 담는다
                    itemList.add(sellItemList); //담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }
                adapter.notifyDataSetChanged();

            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d("확인",itemList.toString());

        recyclerView = findViewById(R.id.recyclerview);

        int numberOfColumns = 2; //컬럼 2개로
        int spacing = 30;
        boolean includeEdge = true;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(numberOfColumns, spacing, includeEdge));

        adapter = new SearchAdapter(itemList,this);
        recyclerView.setAdapter(adapter);
    }

    private void createTextView(String text, LinearLayout layout, String type){
        TextView label = new TextView(getApplicationContext());
        label.setText(text);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setMinWidth(7);
        label.setPadding(15,8,15,8);
        label.setTextSize(15);
        label.setTextColor(Color.parseColor("#433F4F"));
        label.setTypeface(null, Typeface.NORMAL);
        label.setId(0);

        GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(this,R.drawable.custom_label2);
        label_drawable.setStroke(4,Color.parseColor(selectStrokeColor(type)));
        label.setBackground(getResources().getDrawable(R.drawable.custom_label2));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins(15,40,0,0);

        label.setLayoutParams(param);
        layout.addView(label);

    }
    private String selectStrokeColor(String type){
        if(type.equals("group"))
            return "#FFB8B8";
        else if(type.equals("album"))
            return "#41BBFF";
        else if(type.equals("member"))
            return "#FCD54C";
        return "#565656";
    }

}

