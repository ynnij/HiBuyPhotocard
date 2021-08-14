package com.example.hibuyphotocard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lakue.lakuepopupactivity.PopupActivity;
import com.lakue.lakuepopupactivity.PopupResult;
import com.lakue.lakuepopupactivity.PopupType;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SellItemActivity extends AppCompatActivity {

   private FirebaseDatabase database = FirebaseDatabase.getInstance();
   private DatabaseReference databaseReference = database.getReference();
   private Intent intent;
   //private int number;
   private String itemGroupTag;
   private String itemAlbumTag;
   private String itemMemberTag;
   private String itemDetail;
   private String itemDelivery;
   private String itemUserName;
   private String itemImage;
   private Integer itemPrice;
   private ImageView imageView;
   private ImageView imagePopup;
   private TextView groupTagView;
   private TextView albumTagView;
   private TextView memberTagView;
   private TextView detailView;
   private TextView deliveryView;
   private TextView userNameView;
   private TextView priceView;
   private Button sellStateButton1;
   private Button sellStateButton2;
   private Button sellStateButton3;
   private Button editButton;

   protected void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_sell_mypage);


       intent = getIntent();
       //number = intent.getIntExtra("number",-1);
       itemGroupTag = intent.getStringExtra("groupTag");
       itemAlbumTag = intent.getStringExtra("albumTag");
       itemMemberTag = intent.getStringExtra("memberTag");
       itemDetail = intent.getStringExtra("detail");
       itemDelivery = intent.getStringExtra("delivery");
       itemUserName = intent.getStringExtra("userName");
       itemImage = intent.getStringExtra("imageURI");
       itemPrice = intent.getIntExtra("price",0);

       groupTagView = findViewById(R.id.sellListGroupTag);
       albumTagView = findViewById(R.id.sellListAlbumrTag);
       memberTagView = findViewById(R.id.sellListMemberTag);
       detailView = findViewById(R.id.sellListDetail);
       deliveryView = findViewById(R.id.sellListDelivery);
       userNameView = findViewById(R.id.sellListUserName);
       imageView = findViewById(R.id.sellListImage);
       priceView = findViewById(R.id.sellListPrice);
       sellStateButton1 = findViewById(R.id.sellStateButton1);
       sellStateButton2 = findViewById(R.id.sellStateButton2);
       sellStateButton3 = findViewById(R.id.sellStateButton3);
       editButton = findViewById(R.id.editButton);

       groupTagView.setText(itemGroupTag);
       albumTagView.setText(itemAlbumTag);
       memberTagView.setText(itemMemberTag);
       detailView.setText(itemDetail);
       deliveryView.setText(itemDelivery);
       userNameView.setText(itemUserName);
       priceView.setText(String.valueOf(itemPrice));
       Glide.with(imageView).load(itemImage).into(imageView);

       //이미지 클릭 시 팝업
       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getBaseContext(), PopupActivity.class);
               intent.putExtra("type", PopupType.IMAGE);
               intent.putExtra("title", itemImage); //Image
               intent.putExtra("buttonLeft", "종료");
               intent.putExtra("buttonRight", "상세보기");
               startActivityForResult(intent, 4);
           }
       });

       sellStateButton1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               intent = getIntent();
               String sellID = intent.getStringExtra("sellID");
               databaseReference.child("Sell").child(sellID).child("state").setValue("판매중");
               Toast.makeText(SellItemActivity.this,"\'판매중\'을 선택하였습니다.",Toast.LENGTH_SHORT).show();
           }
       });

       sellStateButton2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               intent = getIntent();
               String sellID = intent.getStringExtra("sellID");
               databaseReference.child("Sell").child(sellID).child("state").setValue("예약중");
               Toast.makeText(SellItemActivity.this,"\'예약중\'을 선택하였습니다.",Toast.LENGTH_SHORT).show();
           }
       });

       sellStateButton3.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               intent = getIntent();
               String sellID = intent.getStringExtra("sellID");
               databaseReference.child("Sell").child(sellID).child("state").setValue("거래완료");
               Toast.makeText(SellItemActivity.this,"\'거래완료\'를 선택하였습니다.",Toast.LENGTH_SHORT).show();
           }
       });

       editButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(SellItemActivity.this,SellMainActivity.class);
               startActivity(intent);
           }
       });
   }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //데이터 받기
            if(requestCode == 1){
                PopupResult result = (PopupResult) data.getSerializableExtra("result");
                if(result == PopupResult.CENTER){
                    // 작성 코드
                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();
                }
            }
            if(requestCode == 2){
                PopupResult result = (PopupResult) data.getSerializableExtra("result");
                if(result == PopupResult.LEFT){
                    // 작성 코드
                    Toast.makeText(this, "LEFT", Toast.LENGTH_SHORT).show();

                } else if(result == PopupResult.RIGHT){
                    // 작성 코드
                    Toast.makeText(this, "RIGHT", Toast.LENGTH_SHORT).show();

                }
            }
            if(requestCode == 3){
                PopupResult result = (PopupResult) data.getSerializableExtra("result");
                if(result == PopupResult.CENTER){
                    // 작성 코드
                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();

                }
            }
            if(requestCode == 4){
                PopupResult result = (PopupResult) data.getSerializableExtra("result");
                if(result == PopupResult.IMAGE){
                    // 작성 코드
                    Toast.makeText(this, "IMAGE", Toast.LENGTH_SHORT).show();

                } else if(result == PopupResult.CENTER){
                    // 작성 코드
                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }



}