package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SellItemActivity extends AppCompatActivity {

   private FirebaseDatabase database = FirebaseDatabase.getInstance();
   private DatabaseReference databaseReference = database.getReference();
   private Intent intent;

   private FirebaseStorage storage;
   Uri selectedImageUri;
    private final int GET_GALLERY_IMAGE = 200;

   //private int number;
   private String itemGroupTag;
   private String itemAlbumTag;
   private String itemMemberTag;
   private String itemDetail;
   private String itemDelivery;
   private String itemUserName;
   private String itemImage;
   private String itemSellId;
   private String itemTitle;
   private String itemPrice;
   private String itemState;
   private String itemDefect;
   private ImageView imageView;
   private ImageView imagePopup;
   private TextView groupTagView;
   private TextView albumTagView;
   private TextView memberTagView;
   private EditText sellTitle;
   private EditText sellPrice;
   private EditText sellDelivery;
   private EditText sellDetail;
   private TextView detailView;
   private TextView deliveryView;
   private TextView userNameView;
   private TextView priceView;
   private ToggleButton sellStateButton1;
   private ToggleButton sellStateButton2;
   private ToggleButton sellStateButton3;
   private Button editButton;
   private ToggleButton sellYesButton;
   private ToggleButton sellNoButton;
   private Button taggingButton;

   protected void onCreate(Bundle savedInstanceState) {

       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_sell_mypage);

       ActionBar actionBar = getSupportActionBar();
       actionBar.hide(); // actionBar 숨기기

       //statusBbar
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           Window window = getWindow();
           window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           window.setStatusBarColor(Color.parseColor("#C5DCFF"));
       }

//       Intent intent = new Intent(SellItemActivity.this,TaggingActivity.class);
//       startActivity(intent);


       intent = getIntent();
       //number = intent.getIntExtra("number",-1);
       itemGroupTag = intent.getStringExtra("groupTag");
       itemAlbumTag = intent.getStringExtra("albumTag");
       itemMemberTag = intent.getStringExtra("memberTag");
       itemDetail = intent.getStringExtra("detail");
       itemDelivery = intent.getStringExtra("delivery");
       itemUserName = intent.getStringExtra("userName");
       itemImage = intent.getStringExtra("imageURI");
       itemPrice = intent.getStringExtra("price");
       itemTitle = intent.getStringExtra("title");
       itemSellId = intent.getStringExtra("sellID");
       itemDefect = intent.getStringExtra("defect");
       itemState = intent.getStringExtra("state");



       groupTagView = findViewById(R.id.sellListGroupTag);
       albumTagView = findViewById(R.id.sellListAlbumrTag);
       memberTagView = findViewById(R.id.sellListMemberTag);
       //detailView = findViewById(R.id.sellListDetail);
       //deliveryView = findViewById(R.id.sellListDelivery);
       //userNameView = findViewById(R.id.sellListUserName);
       imageView = findViewById(R.id.sellListImage);
       //priceView = findViewById(R.id.sellListPrice);
       sellStateButton1 = findViewById(R.id.sellStateButton1);
       sellStateButton2 = findViewById(R.id.sellStateButton2);
       sellStateButton3 = findViewById(R.id.sellStateButton3);
       editButton = findViewById(R.id.editButton);
       sellYesButton = findViewById(R.id.sellYesButton);
       sellNoButton = findViewById(R.id.sellNoButton);

       sellDelivery = findViewById(R.id.sellListDelivery);
       sellDetail = findViewById(R.id.sellListDetail);
       sellPrice = findViewById(R.id.sellPrice);
       sellTitle = findViewById(R.id.sellTitle);

       groupTagView.setText(itemGroupTag);
       albumTagView.setText(itemAlbumTag);
       memberTagView.setText(itemMemberTag);
       //detailView.setText(itemDetail);
       //deliveryView.setText(itemDelivery);
       //userNameView.setText(itemUserName);
       //priceView.setText(String.valueOf(itemPrice));
       //Glide.with(imageView).load(itemImage).into(imageView);

       sellDetail.setText(itemDetail);
       sellPrice.setText(itemPrice);
       sellDelivery.setText(itemDelivery);
       sellTitle.setText(itemTitle);


       storage = FirebaseStorage.getInstance();
       StorageReference storageReference = storage.getReference();
       StorageReference riversRef = storageReference.child(itemImage);
       riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               Glide.with(imageView).load(uri).into(imageView);
           }
       });

       storage = FirebaseStorage.getInstance();
       imageView.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {

               Intent intent = new Intent(Intent.ACTION_PICK);
               intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
               startActivityForResult(intent, GET_GALLERY_IMAGE);
           }
       });

       if(itemDefect.equals("하자 있음")){
           sellYesButton.setChecked(true);
       }
       else if(itemDefect.equals("하자 없음")){
           sellNoButton.setChecked(true);
       }

       if(itemState.equals("판매중")){
           sellStateButton1.setChecked(true);
       }
       else if(itemState.equals("예약중")){
           sellStateButton2.setChecked(true);
       }
       else if(itemState.equals("거래완료")){
           sellStateButton3.setChecked(true);
       }




       //이미지 클릭 시 팝업
//       imageView.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               Intent intent = new Intent(getBaseContext(), PopupActivity.class);
//               intent.putExtra("type", PopupType.IMAGE);
//               intent.putExtra("title", itemImage); //Image
//               intent.putExtra("buttonLeft", "종료");
//               intent.putExtra("buttonRight", "상세보기");
//               startActivityForResult(intent, 4);
//           }
//       });

//       sellStateButton1.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//
//               intent = getIntent();
//               String sellID = intent.getStringExtra("sellID");
//               databaseReference.child("Sell").child(sellID).child("state").setValue("판매중");
//
//               Toast.makeText(SellItemActivity.this,"\'판매중\'을 선택하였습니다.",Toast.LENGTH_SHORT).show();
//           }
//       });

//       sellStateButton2.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               intent = getIntent();
//               String sellID = intent.getStringExtra("sellID");
//               databaseReference.child("Sell").child(sellID).child("state").setValue("예약중");
//               Toast.makeText(SellItemActivity.this,"\'예약중\'을 선택하였습니다.",Toast.LENGTH_SHORT).show();
//           }
//       });

//       sellStateButton3.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               intent = getIntent();
//               String sellID = intent.getStringExtra("sellID");
//               databaseReference.child("Sell").child(sellID).child("state").setValue("거래완료");
//               Toast.makeText(SellItemActivity.this,"\'거래완료\'를 선택하였습니다.",Toast.LENGTH_SHORT).show();
//           }
//       });

       sellStateButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   sellStateButton2.setChecked(false);
                   sellStateButton3.setChecked(false);
                   itemState = "판매중";
//                   intent = getIntent();
//                   String sellID = intent.getStringExtra("sellID");
//
//                   databaseReference.child("Sell").child(sellID).child("state").setValue("판매중");
               }
           }
       });

       sellStateButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   sellStateButton1.setChecked(false);
                   sellStateButton3.setChecked(false);
                   itemState = "예약중";
//                   intent = getIntent();
//                   String sellID = intent.getStringExtra("sellID");
//                   databaseReference.child("Sell").child(sellID).child("state").setValue("예약중");
               }
           }
       });

       sellStateButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   sellStateButton1.setChecked(false);
                   sellStateButton2.setChecked(false);
                   itemState = "거래완료";
//                   intent = getIntent();
//                   String sellID = intent.getStringExtra("sellID");
//                   databaseReference.child("Sell").child(sellID).child("state").setValue("거래완료");
               }
           }
       });

       sellYesButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   itemDefect = "하자 있음";
                   sellNoButton.setChecked(false);
               }
           }
       });

       sellNoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   itemDefect = "하자 없음";
                   sellYesButton.setChecked(false);
               }
           }
       });

       if(itemState == null)
           itemDefect = intent.getStringExtra("defect");


       editButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               databaseReference.child("Sell").child(itemSellId).child("detail").setValue(sellDetail.getText().toString());
               databaseReference.child("Sell").child(itemSellId).child("title").setValue(sellTitle.getText().toString());
               databaseReference.child("Sell").child(itemSellId).child("price").setValue(sellPrice.getText().toString());
               databaseReference.child("Sell").child(itemSellId).child("delivery").setValue(sellDelivery.getText().toString());
               databaseReference.child("Sell").child(itemSellId).child("defect").setValue(itemDefect);
               databaseReference.child("Sell").child(itemSellId).child("state").setValue(itemState);
               if (selectedImageUri == null){
                   databaseReference.child("Sell").child(itemSellId).child("imageURI").setValue(itemImage);
               }
               else{
                   databaseReference.child("Sell").child(itemSellId).child("imageURI").setValue(selectedImageUri.toString());
               }

               Toast.makeText(SellItemActivity.this, "판매글 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();

               Intent intent = new Intent(SellItemActivity.this, SellMainActivity.class);
               startActivity(intent);
           }
       });


   }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            //데이터 받기
//            if(requestCode == 1){
//                PopupResult result = (PopupResult) data.getSerializableExtra("result");
//                if(result == PopupResult.CENTER){
//                    // 작성 코드
//                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();
//                }
//            }
//            if(requestCode == 2){
//                PopupResult result = (PopupResult) data.getSerializableExtra("result");
//                if(result == PopupResult.LEFT){
//                    // 작성 코드
//                    Toast.makeText(this, "LEFT", Toast.LENGTH_SHORT).show();
//
//                } else if(result == PopupResult.RIGHT){
//                    // 작성 코드
//                    Toast.makeText(this, "RIGHT", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//            if(requestCode == 3){
//                PopupResult result = (PopupResult) data.getSerializableExtra("result");
//                if(result == PopupResult.CENTER){
//                    // 작성 코드
//                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//            if(requestCode == 4){
//                PopupResult result = (PopupResult) data.getSerializableExtra("result");
//                if(result == PopupResult.IMAGE){
//                    // 작성 코드
//                    Toast.makeText(this, "IMAGE", Toast.LENGTH_SHORT).show();
//
//                } else if(result == PopupResult.CENTER){
//                    // 작성 코드
//                    Toast.makeText(this, "CENTER", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef=storageRef.child(selectedImageUri.toString());
            UploadTask uploadTask = riversRef.putFile(selectedImageUri);



        }

    }


}