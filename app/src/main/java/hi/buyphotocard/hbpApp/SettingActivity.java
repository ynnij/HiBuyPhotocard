package hi.buyphotocard.hbpApp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private List<String> list;          // 데이터를 넣은 리스트변수
    private ListView listView;          // 검색을 보여줄 리스트변수
    private EditText editSearch;        // 검색어를 입력할 Input 창
    private SearchAdapter adapter;      // 리스트뷰에 연결할 아답터
    private ArrayList<String> arraylist;
    private FirebaseAuth mAuth;
    int i = 0;
    private DatabaseReference SellItemList;

    DatabaseReference ref;
    private Dialog searchDialog;
    private Button search_button;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference databaseReference = database.getReference();
    private DatabaseReference mDatabase;
    private DatabaseReference groupDB;
    private DatabaseReference fandomDB;
    private FirebaseDatabase mData;
    private DatabaseReference memberDB;

    private AutoCompleteTextView search_Group;
    private AutoCompleteTextView search_Album;
    private AutoCompleteTextView search_Member;

    private List<String> group_list = new ArrayList<String>();
    private List<String> album_list = new ArrayList<String>();
    private List<String> member_list = new ArrayList<String>();
    private ArrayList<SearchItemList> item; // recyclerview에 적용할 아이템 리스트
    private ArrayList<SearchItemList> itemList2;  //전송할 리스트


    SearchItemList sellItemList;

    private HashMap sell1 = new HashMap();
    private String selectMember ="";
    private LinearLayout group_label;
    private LinearLayout album_label;
    private LinearLayout member_label;
    private String selectGroup ="";
    private List<String> searchKeywordGroup = new ArrayList<String>();
    private List<String> AllGroup = new ArrayList<String>();
    private List<String> searchKeywordAlbum = new ArrayList<String>();
    private List<String> searchKeywordMember = new ArrayList<String>();
    private DatabaseReference photocardDB;
    private String dataGroup;


    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;

    private ImageView explain_button;
    TextView txtResult;

    private FirebaseStorage storage;
    Uri photoUri;
    String mCurrentPhotoPath;
    final static int TAKE_PICTURE = 1;
    final static int REQUEST_TAKE_PHOTO = 1;
    private Dialog dialog;
    private Button cameraBtn;
    private Button albumBtn;


    private DatabaseReference usersDB = database.getReference();;

    private String uid;
    Uri selectedImageUri;
    EditText nicknameText;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profilesetting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); // actionBar 숨기기

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#000000"));
        }

        imageview = (ImageView) findViewById(R.id.profile_ficture);
        imageview.setBackground(new ShapeDrawable(new OvalShape()));

        explain_button = (ImageView) findViewById(R.id.explanation_button);
        explain_button.setBackground(new ShapeDrawable(new OvalShape()));

        PopupDialogActivity explain_dialog = new PopupDialogActivity(SettingActivity.this);


        explain_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explain_dialog.showDialog();
            }
        });


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
                ActivityCompat.requestPermissions(SettingActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }


        storage = FirebaseStorage.getInstance();
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.show();
            }
        });

        nicknameText = findViewById(R.id.signup_nickname);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        String email = intent.getStringExtra("emailtext");

        group_label = findViewById(R.id.group_label);

        member_label = findViewById(R.id.member_label);

        search_Group = findViewById(R.id.signup_favorit);

        search_Member = findViewById(R.id.signup_love);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mData=FirebaseDatabase.getInstance();
        groupDB = mDatabase.child("idol");
        fandomDB=mDatabase.child("fandom");

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();


        Button Finish_button = findViewById(R.id.button_profile_finish);

        Finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                databaseReference.child("id_list").child(nicknameText.getText().toString()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String value = snapshot.getValue(String.class);

                        if(value!=null){
                            Toast.makeText(getApplicationContext(),"중복되는 닉네임 입니다",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                        else if(searchKeywordGroup.size()==0|| searchKeywordMember.size()==0){
                            Toast.makeText(getApplicationContext(),"그룹 또는 멤버를 선택해주세요",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                        else if(nicknameText.getText().toString().equals("")||nicknameText.getText().toString()==null){
                            Toast.makeText(getApplicationContext(),"닉네임을 입력해주세요",Toast.LENGTH_SHORT).show();//토스메세지 출력
                        }
                        else{
                            Log.d("확인",selectedImageUri.toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("profileImageUrl").setValue(selectedImageUri.toString());
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("uid").setValue(uid);
                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("userName").setValue(nicknameText.getText().toString());
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("email").setValue(email);
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("name").setValue(nicknameText.getText().toString());
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("group").setValue(searchKeywordGroup);

                            String fandomname;
                            fandomDB.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String,String> fandom = (Map<String, String>)snapshot.getValue();
                                    String groupname=searchKeywordGroup.get(0);

                                    databaseReference.child("id_list").child(nicknameText.getText().toString()).child("mypage").child("fandom").setValue(fandom.get(groupname));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("UID").setValue(uid);
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("member").setValue(searchKeywordMember);
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("image").setValue(selectedImageUri.toString());
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("mypage").child("deliveryScore").setValue(30);
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("mypage").child("itemScore").setValue(30);
                            databaseReference.child("id_list").child(nicknameText.getText().toString()).child("mypage").child("mannerScore").setValue(30);


                            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                            intent.putExtra("itemList",item);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 디비를 가져오던중 에러 발생 시
                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });


            }
        });
        //firebase에서 데이터 불러오기




        groupDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,Object> group = (Map<String, Object>)snapshot.getValue();
                Object [] groupList = group.keySet().toArray();
                ArrayAdapter groupAdapter = new ArrayAdapter(SettingActivity.this,
                        android.R.layout.simple_list_item_1,groupList);
                search_Group.setAdapter(groupAdapter);

                search_Group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference(); //firebase 연결
                        SellItemList = mData.child("Sell");

                        item = new ArrayList<>(); //검색 결과화면으로 넘길 리스트
                        item.clear();

                        String dataGroup = parent.getAdapter().getItem(position).toString();
                        if (selectGroup.equals("")){
                            selectGroup=dataGroup;
                            createTextView(dataGroup,group_label,"group",searchKeywordGroup);
                            selectOther(searchKeywordGroup);
                            search_Group.setText("");
                        }
                        else{
                            Toast.makeText(SettingActivity.this,
                                    "하나만 선택해주세요.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


    public void selectOther(List group) {

        for (int i=0; i <group.size();i++) {
            memberDB = mDatabase.child("idol").child(group.get(i).toString()).child("member");
            memberDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList member = (ArrayList) snapshot.getValue();
                    ArrayAdapter memberAdapter = new ArrayAdapter(SettingActivity.this,
                            android.R.layout.simple_list_item_1, member);
                    search_Member.setAdapter(memberAdapter);
                    search_Member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {





                            String dataMemeber = parent.getAdapter().getItem(position).toString();
                            if (selectMember.equals("")) {
                                selectMember = dataMemeber;
                                createTextView(dataMemeber, member_label, "member",searchKeywordMember);
                            } else {
                                Toast.makeText(SettingActivity.this,
                                        "하나만 선택해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            search_Member.setText("");
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

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


    private void createTextView(String text, LinearLayout layout, String type, List list){
        TextView label = new TextView(getApplicationContext());
        label.setText(text);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setPadding(15,8,15,8);
        label.setTextSize(12);
        label.setTextColor(Color.parseColor("#ffffff"));
        label.setTypeface(null, Typeface.NORMAL);
        label.setId(0);
        label.setLinksClickable(true);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(v);
                int index = list.indexOf(text);
                list.remove(index);
                Log.d("데이터",list.toString());
            }
        });

        GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(this,R.drawable.custom_label);
        label_drawable.setStroke(4,Color.parseColor(selectStrokeColor(type)),8,5);
        label_drawable.setColor(Color.parseColor(selectColor(type)));
        label.setBackground(getResources().getDrawable(R.drawable.custom_label));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.rightMargin =20;

        label.setLayoutParams(param);
        layout.addView(label);
        list.add(text);
        Log.d("데이터",list.toString());

    }


    private String selectColor(String type){
        if(type.equals("group"))
            return "#FFC8C8";
        else if(type.equals("member"))
            return "#FFE790";
        return "#565656";
    }
    private String selectStrokeColor(String type){
        if(type.equals("group"))
            return "#FFB8B8";
        else if(type.equals("member"))
            return "#FCD54C";
        return "#565656";
    }


}