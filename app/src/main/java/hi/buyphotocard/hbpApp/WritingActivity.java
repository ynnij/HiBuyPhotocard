package hi.buyphotocard.hbpApp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    Uri photoUri;
    String mCurrentPhotoPath;
    final static int TAKE_PICTURE = 1;
    final static int REQUEST_TAKE_PHOTO = 1;

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

    private String itemGroupTag;
    private String itemAlbumTag;
    private String itemMemberTag;
    private String itemImage;


    private HashMap sell1 = new HashMap();

    private Dialog dialog;
    private Button cameraBtn;
    private Button albumBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //-----------뒤로 가기 이벤트 처리
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TaggActivity에서 가져옴
        Intent intent = getIntent();
        itemGroupTag = intent.getStringExtra("groupTag");
        itemAlbumTag = intent.getStringExtra("albumTag");
        itemMemberTag = intent.getStringExtra("memberTag");
        itemImage = intent.getStringExtra("image");

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        //다이얼로그
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_camera_dialog);

        cameraBtn = dialog.findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(this::onClick);
        albumBtn = dialog.findViewById(R.id.albumBtn);
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
                dialog.hide();
            }
        });
        // 권한 확인 및 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("check", "권한 설정 완료");
            }
            else {
                Log.d("check", "권한 설정 요청");
                ActivityCompat.requestPermissions(WritingActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        storage = FirebaseStorage.getInstance();
        //------------이미지 선택
        imageview = findViewById(R.id.writeImage);
        Glide.with(getApplicationContext()).load(itemImage).into(imageview);

        //--------------다이얼로그 부분 삭제 필요
//        imageview.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.show();
//            }
//        });

        // 카메라 선택
        //imageview.setOnClickListener(this::onClick);

        //앨범에서 불러오기
        /*
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
        */

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

                if(writeTitle.getText()!=null&writePrice.getText()!=null&writeDetail.getText()!=null&writeDelivery.getText()!=null&(yesButton.isChecked()|noButton.isChecked()))
                {
                    //SellItemList sellItemList = new SellItemList();
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("albumTag").setValue(itemAlbumTag);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("defect").setValue(state);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("delivery").setValue(writeDelivery.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("detail").setValue(writeDetail.getText().toString());
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("email").setValue(email);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("groupTag").setValue(itemGroupTag);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("imageURI").setValue(itemImage);
                    databaseReference.child("Sell").child("sell"+String.valueOf(sellCnt+1)).child("memberTag").setValue(itemMemberTag);
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


    // 권한 요청하기
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("check", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED )
        {
            Log.d("check", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO: {
                if(resultCode == RESULT_OK){
                    imageview.setImageURI(selectedImageUri);
                    StorageReference storageRef = storage.getReference();
                    StorageReference riversRef=storageRef.child(selectedImageUri.toString());
                    UploadTask uploadTask = riversRef.putFile(selectedImageUri);
                    dialog.hide();
                }
            }
        }

        if(requestCode == 0 &&resultCode == RESULT_OK){
            imageview.setImageURI(photoUri);
            dialog.hide();

        }

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            imageview.setImageURI(selectedImageUri);
            StorageReference storageRef = storage.getReference();
            StorageReference riversRef=storageRef.child(selectedImageUri.toString());
            UploadTask uploadTask = riversRef.putFile(selectedImageUri);
            dialog.hide();
        }
    }

    // 촬영한 사진을 이미지 파일로 저장
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,".jpg",storageDir
        );
        return image;
    }

    //카메라 버튼 클릭 시 실행할 메서드
    public void onClick(View view){
       Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

       //인텐트를 처리할 카메라 액티비티가 있는지 확인
        if(takePictureIntent.resolveActivity(getPackageManager())!=null) {
            //촬영한 사진을 저장할 파일 생성
            File photoFile = null;

            try {
                //임시로 사용할 파일 -> 캐시 폴더로 경로 설정
                File tempDir = getCacheDir();

                //임시 촬영 파일 세팅
                String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
                String imageFileName = "Capture_"+timeStamp+"_";

                File tempImage = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        tempDir
                );

                //ACTION_VIEW 인텐르를 사용할 경로 (임시파일의 경로)
                mCurrentPhotoPath = tempImage.getAbsolutePath();
                photoFile = tempImage;
            } catch (IOException e){
                Log.d("확인","파일 생성 에러 ",e);
            }

            //파일이 정상적으로 생성되었을 때 계속 실행
            if(photoFile !=null){
                Uri photoURI = FileProvider.getUriForFile(this, getPackageName()+".fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                selectedImageUri = photoURI;
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                dialog.hide();

            }
        }
    }
}