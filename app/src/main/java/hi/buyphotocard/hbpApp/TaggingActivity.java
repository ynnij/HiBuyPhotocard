package hi.buyphotocard.hbpApp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class TaggingActivity extends AppCompatActivity {
    Button tagging_next_btn;
    ImageButton tagging_store_btn;
    Dialog dilaog01; // 커스텀 다이얼로그
    Dialog dilaog02; // 임시저장

    //텐서 변수
    private  int imageSizeX;
    private  int imageSizeY;
    private Bitmap bitmap;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    protected Interpreter tflite;

    Uri imageuri;

    TextView groupTag;
    TextView albumTag;
    TextView memberTag;

    Button createBtn;

    private String group;
    private String member;
    private String album;

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private List<String> labels;

    //이미지 다이얼로그 변수
    private FirebaseStorage storage;
    private Dialog dialog;
    private Button cameraBtn;
    private Button albumBtn;
    Uri selectedImageUri;
    Uri photoUri;
    String mCurrentPhotoPath;
    final static int TAKE_PICTURE = 1;
    final static int REQUEST_TAKE_PHOTO = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tagging_first);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        tagging_next_btn  = findViewById(R.id.tagging_next_btn);

        dilaog01 = new Dialog(TaggingActivity.this);       // Dialog 초기화
        dilaog01.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        dilaog01.setContentView(R.layout.dilaog01);             // xml 레이아웃 파일과 연결

        // 버튼: 커스텀 다이얼로그 띄우기
        findViewById(R.id.tagging_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog01(); // 아래 showDialog01() 함수 호출

            }
        });

        //ML
        imageview = findViewById(R.id.sellListImage);

        groupTag = findViewById(R.id.sellListGroupTag);
        albumTag = findViewById(R.id.sellListAlbumTag);
        memberTag = findViewById(R.id.sellListMemberTag);


        try{
            tflite=new Interpreter(loadmodelfile(TaggingActivity.this));
        }catch (Exception e) {
            e.printStackTrace();
        }
        createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                showresult();


            }
        });

        //사진 선택 다이얼로그
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

            }
        });

        storage = FirebaseStorage.getInstance();
        //------------이미지 선택
        imageview = findViewById(R.id.sellListImage);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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
                ActivityCompat.requestPermissions(TaggingActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }


    //ML
    private TensorImage loadImage(Bitmap bitmap) {
        // Loads bitmap into a TensorImage.

        //uri -> bitmap
        try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        // .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("my_model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }



    private void showresult(){

        try{
            labels = FileUtil.loadLabels(TaggingActivity.this, "label.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            //if (entry.getValue()==maxValueInMap) {
            String[] label = labeledProbability.keySet().toArray(new String[0]);
            Float[] label_probability = labeledProbability.values().toArray(new Float[0]);

            float max=0;
            max=label_probability[0];
            int maxposition=0;
            for(int i=1; i<label_probability.length; i++)
            {
                if( max < label_probability[i]){
                    max=label_probability[i];
                    maxposition=i;
                }
            }

            //라벨링
            String str = labels.get(maxposition);
            String[] array = str.split("_");
            group = array[0];
            album = array[1];
            member = array[2];

            groupTag.setText(group);
            albumTag.setText(album);
            memberTag.setText(member);



        }
    }


    // dialog01을 디자인하는 함수
    public void showDialog01(){

        dilaog01.show(); // 다이얼로그 띄우기
        // 아니오 직접수정하기버튼
        Button noBtn = dilaog01.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 태그 보내기
                TagDialogActivity dialog = new TagDialogActivity(TaggingActivity.this);
                dialog.showDialog(String.valueOf(selectedImageUri));
                //이미지 보내는 거 해결하기
                dilaog01.dismiss(); // 다이얼로그 닫기


            }
        });
        // 네 버튼
        dilaog01.findViewById(R.id.yesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //WritingActivity로 데이터 보내기
                Intent intent = new Intent(TaggingActivity.this,WritingActivity.class );
                intent.putExtra("groupTag",group);
                intent.putExtra("albumTag",album);
                intent.putExtra("memberTag",member);

                intent.putExtra("image",String.valueOf(selectedImageUri));

                startActivity(intent);
                finish();           // 앱 종료
            }
        });
        //Intent intent = new Intent(TaggingActivity.this,WritingActivity.class );
        //intent.putExtra("image",String.valueOf(selectedImageUri));
        //startActivity(intent);

    }

    //이미지 선택 다이얼로그 함수
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
