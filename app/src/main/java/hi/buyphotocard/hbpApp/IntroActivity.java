package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml 연결

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); // actionBar 숨기기

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent); // 인트로 화면 후 main activity 넘기기
                finish(); // intro activity 종료
            }
        }, 1000); // 1초 후 인트로 실행
    }

    protected void onPause() { //얘는 뭘까..? 정리하기
        super.onPause();
        finish();
    }
}

// 코드 작성 후 manifest 파일 변경