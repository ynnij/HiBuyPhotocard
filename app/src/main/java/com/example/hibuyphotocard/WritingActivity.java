package com.example.hibuyphotocard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WritingActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference sellListDB;

    private String email;
    private String nickName;
    private ArrayList sell;
    private int sellCnt = 0;
    private int cnt=0;
    private int price=0;

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
    private FirebaseStorage storage;
    Uri selectedImageUri;

    private EditText writeTitle;
    private EditText writePrice;
    private EditText writeDetail;
    private EditText writeDelivery;
    private ToggleButton yesButton;
    private ToggleButton noButton;
    private Button saveButton;
    private String state;


    private HashMap sell1 = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //-----------뒤로 가기 이벤트 처리
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //------------이미지 선택
        imageview = findViewById(R.id.writeImage);

        storage = FirebaseStorage.getInstance();
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });





        writeTitle = findViewById(R.id.writeTitle);
        writePrice = findViewById(R.id.writePrice);
        writeDetail = findViewById(R.id.writeDetail);
        writeDelivery = findViewById(R.id.writeDelivery);
        yesButton = findViewById(R.id.wrtieYesButton);
        noButton = findViewById(R.id.writeNoButton);
        saveButton = findViewById(R.id.saveButton);


        //------로그인 사용자 닉네임 가져오기
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();
                //Log.d("확인",email);

            }
        }

        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String user = dataSnapshot.child("email").getValue(String.class);
                    if(user.equals(email)){
                        nickName = dataSnapshot.getKey();
                        sellListDB = allUsers.child(nickName).child("sell");
                        sellListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                sell = (ArrayList)snapshot.getValue();
                                if(sell == null)
                                    cnt = 0;
                                else
                                    cnt = sell.size();
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        yesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    state = "하자 있음";
                    noButton.setChecked(false);
                }
            }
        });

        noButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    state = "하자 없음";
                    yesButton.setChecked(false);
                }
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Sell").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                sell1 = (HashMap) snapshot.getValue();
                if (sell1 == null){
                    sellCnt = 0;
                }
                else{
                    sellCnt = sell1.size();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(writeTitle.getText()!=null&writePrice.getText()!=null&writeDetail.getText()!=null&writeDelivery.getText()!=null&selectedImageUri!=null&(yesButton.isChecked()|noButton.isChecked()))
                {
                    //SellItemList sellItemList = new SellItemList();
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("albumTag").setValue("Lived(White ver.)"); //추후 변경
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("defect").setValue(state);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("delivery").setValue(writeDelivery.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("detail").setValue(writeDetail.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("email").setValue(email);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("groupTag").setValue("원어스"); //추후 변경
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("imageURI").setValue(selectedImageUri.toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("memberTag").setValue("시온"); //추후 변경
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("price").setValue(writePrice.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("rateState").setValue(false); //default
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("sellID").setValue("sell"+String.valueOf(sellCnt+1));
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("state").setValue("판매중"); //default
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("title").setValue(writeTitle.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("userName").setValue(nickName);

                    //sell 하위로 들어가도록 해야함
                    databaseReference.child("id_list").child(nickName).child("sell").child(String.valueOf(cnt)).setValue("sell"+String.valueOf(sellCnt+1));

                    Toast.makeText(WritingActivity.this, "판매글 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(WritingActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(WritingActivity.this,"모두 작성해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef=storageRef.child(selectedImageUri.toString());
            UploadTask uploadTask = riversRef.putFile(selectedImageUri);


        }

    }
}