package com.example.hibuyphotocard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SellItemActivity extends AppCompatActivity {

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
   private TextView groupTagView;
   private TextView albumTagView;
   private TextView memberTagView;
   private TextView detailView;
   private TextView deliveryView;
   private TextView userNameView;
   private TextView priceView;

   protected void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_sell_post);

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

       groupTagView.setText(itemGroupTag);
       albumTagView.setText(itemAlbumTag);
       memberTagView.setText(itemMemberTag);
       detailView.setText(itemDetail);
       deliveryView.setText(itemDelivery);
       userNameView.setText(itemUserName);
       priceView.setText(String.valueOf(itemPrice));
       Glide.with(imageView).load(itemImage).into(imageView);
   }
}