package com.example.hibuyphotocard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class QuestionActivity<spinner> extends AppCompatActivity {
    private static final String TAG = "Questionctivity";
    int i = 1; //pk
    Spinner spinner_신고유형;
    Button Button_취소;
    Button Button_확인;
    private DatabaseReference DB;
    EditText EditText_문의내용;
    String variable;
    String text;
    ImageButton addphoto_image;
    Uri fileuri;
    String filePath;
    StorageReference imgRef;
    ImageView ImageView_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);


        spinner_신고유형 = findViewById(R.id.spinner_신고유형);
        Button_취소 = findViewById(R.id.Button_취소);
        Button_확인 = findViewById(R.id.Button_확인);
        EditText_문의내용 = findViewById(R.id.EditText_문의내용);
//        addphoto_image = findViewById(R.id.addphoto_image);
//        ImageView_upload = findViewById(R.id.ImageView_upload);

        DB = FirebaseDatabase.getInstance().getReference();
        imgRef = FirebaseStorage.getInstance().getReference();

        spinner_신고유형.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                text = spinner_신고유형.getSelectedItem().toString();
                if(spinner_신고유형.getSelectedItem().toString().equals("4. 파일 첨부하여 메일 보내기(메일함으로 이동됨)")){
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    String[] address = {"ghdwn643@naver.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
                    email.putExtra(Intent.EXTRA_SUBJECT, "Hibuyphotocard/개발자문의및신고");
                    email.putExtra(Intent.EXTRA_TEXT, "문의 및 신고하고 싶은 내용을 적어주세요.");
                    startActivity(email);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        addphoto_image.setClickable(true);
//        addphoto_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent= new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent,10);
//
//            }
//
//        });





        Button_취소.setClickable(true);
        Button_취소.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        Button_확인.setClickable(true);
        Button_확인.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



//                SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
//                String filename= sdf.format(new Date())+ ".png";
//                imgRef= firebaseStorage.getReference("picture/"+filename);
//                imgRef.child("picture/"+filename).putFile(fileuri);
//                UploadTask uploadTask =imgRef.putFile(fileuri);

                DB.child("QnAContent").child(Integer.toString(i)).child("ReportType").setValue(text);
                variable = EditText_문의내용.getText().toString();
                DB.child("QnAContent").child(Integer.toString(i++)).child("Detailed").setValue(variable);
                Toast.makeText(getApplicationContext(),"전송되었습니다.",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuestionActivity.this, MainActivity.class);
                startActivity(intent);
                //이전 페이지로 이동

            }
        });


    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        if (resultCode == RESULT_OK) {
//            //선택한 이미지의 경로 얻어오기
//            fileuri = data.getData();
//            Glide.with(this).load(filePath).into(ImageView_upload);
//        }
//
//
//    }







}