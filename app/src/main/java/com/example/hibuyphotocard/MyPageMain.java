package com.example.hibuyphotocard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URL;
import java.util.ArrayList;

public class MyPageMain<CircularImageView> extends AppCompatActivity {

    private TextView TextView_설정;
    private TextView TextView_개발자문의;
    private TextView TextView_선호아이돌;
    private TextView textview_점수1;
    private TextView textview_점수2;
    private TextView textview_점수3;

    private ImageView ImageView_photo;
    private TextView userName;
    private String email;
    private String nickName;
    private ImageView TextView_상태변화;



    private FirebaseAuth mAuth;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference();
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference mypageDB;
    private DatabaseReference imageDB;
    private DatabaseReference memberDB;
    private FirebaseStorage storage;
    private FirebaseUser user;

//    Uri userProfilePhoto;
//
//    public Integer a;
//    public Integer b;
//    public Integer c;
//    public String cs;

    //하단바
    private LinearLayout contractBtn;
    private LinearLayout homeBtn;
    private LinearLayout chatBtn;
    private LinearLayout mypageBtn;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage);

        TextView_선호아이돌= findViewById(R.id.TextView_선호아이돌);

        TextView_설정= findViewById(R.id.TextView_설정);
        TextView_설정.setClickable(true);
        TextView_설정.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MyPageMain.this, SettingResultActivity.class);
                startActivity(intent);
            }
        });

        TextView_개발자문의= findViewById(R.id.TextView_개발자문의);
        TextView_개발자문의.setClickable(true);
        TextView_개발자문의.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"ghdwn643@naver.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "Hibuyphotocard/개발자문의및신고");
                email.putExtra(Intent.EXTRA_TEXT, "문의 및 신고하고 싶은 내용을 적어주세요.");
                startActivity(email);
            }
        });





        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();
                Log.d("확인",email);

            }
        }


        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if (user.equals(email)) {
                        Log.d("확인", "같은 이메일은 " + email);
                        nickName = datas.getKey();
                        userName = findViewById(R.id.userName);
                        userName.setText(nickName);



                        memberDB = mDatabase.child("id_list").child(nickName).child("member");
                        memberDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                TextView_선호아이돌.setText("♥"+snapshot.child("0").getValue(String.class)+"♥");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                        imageDB = mDatabase.child("id_list").child(nickName).child("image");
                        imageDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String imageUrl = snapshot.getValue(String.class);
                                ImageView_photo = findViewById(R.id.ImageView_photo);
                                ImageView_photo.setImageURI(Uri.parse(imageUrl));

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });





                        mypageDB = mDatabase.child("id_list").child(nickName).child("mypage"); //유저의 마이페이지 가져오기
                        mypageDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Integer deliveryscore = dataSnapshot.child("deliveryScore").getValue(Integer.class);
                                Integer itemScore   =  dataSnapshot.child("itemScore").getValue(Integer.class);
                                Integer mannerScore     =  dataSnapshot.child("mannerScore").getValue(Integer.class);

                                String 배송속도 =  String.valueOf(deliveryscore);
                                String 판매자태도 = String.valueOf(itemScore);
                                String 상품상태 = String.valueOf(mannerScore);

                                ProgressBar progress1 = findViewById(R.id.progress1);
                                textview_점수1 = findViewById(R.id.textview_점수1);
                                ProgressBar progress2 = findViewById(R.id.progress2);
                                textview_점수2 = findViewById(R.id.textview_점수2);
                                ProgressBar progress3 = findViewById(R.id.progress3);
                                textview_점수3 = findViewById(R.id.textview_점수3);



                                progress1.setProgress(deliveryscore);
                                textview_점수1.setText(배송속도);
                                progress2.setProgress(itemScore);
                                textview_점수2.setText(판매자태도);
                                progress3.setProgress(mannerScore);
                                textview_점수3.setText(상품상태);

//                                progress1.setClickable(true);

//                                progress1.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Toast.makeText(MyPageMain.this, "덕력은 "+deliveryscore+" 입니다.", Toast.LENGTH_SHORT).show();
//                                    }
//                                });


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



        //하단바
        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        chatBtn = findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        mypageBtn = findViewById(R.id.mypageBtn);
        mypageBtn.setBackgroundColor(Color.parseColor("#B1E3FF"));

        contractBtn = findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }

}








