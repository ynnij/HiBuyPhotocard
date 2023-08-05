package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;


public class SignUpActivity extends AppCompatActivity {
    SignInButton Google_Login;
    private Button SignButton;
    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    TextView textView;

    class MainHandler extends Handler{
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value/60;
            sec = value % 60;
            //초가 10보다 작으면 앞에 0이 더 붙어서 나오도록한다.
            if(sec<10){
                //텍스트뷰에 시간초가 카운팅
                emailCodeText.setHint("0"+min+" : 0"+sec);
            }else {
                emailCodeText.setHint("0"+min+" : "+sec);
            }
        }
    }


    class MailTread extends Thread{

        public void run(){
            GMailSender gMailSender = new GMailSender("hibuyphotocard@gmail.com", "hibp1886");
            //GMailSender.sendMail(제목, 본문내용, 받는사람);


            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                gMailSender.sendMail("하이바이포토카드 회원가입 이메일 인증", GmailCode , emailText.getText().toString());
            } catch (SendFailedException e) {

            } catch (MessagingException e) {
                System.out.println("인터넷 문제"+e);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class BackgrounThread extends Thread{
        //180초는 3분
        //메인 쓰레드에 value를 전달하여 시간초가 카운트다운 되게 한다.

        public void run(){
            //180초 보다 밸류값이 작거나 같으면 계속 실행시켜라
            while(true){
                value-=1;
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }

                Message message = mainHandler.obtainMessage();
                //메세지는 번들의 객체 담아서 메인 핸들러에 전달한다.
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle);

                //핸들러에 메세지 객체 보내기기

                mainHandler.sendMessage(message);

                if(value<=0){
                    GmailCode="";
                    break;
                }
            }



        }
    }


    Button Button;

    MainHandler mainHandler;


    EditText emailText;

    //인증코드
    String GmailCode;


    //인증번호 입력하는 곳
    EditText emailCodeText;

    Button emailCodeButton;

    static int value;

    int mailSend=0;

    /* Dialog 위한 코드 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button = findViewById(R.id.button_double);

        //이메일 입력하는 뷰
        emailText= findViewById(R.id.signup_email);


        //인증번호 받는 부분은 GONE으로 안보이게 숨긴다
        emailCodeText= findViewById(R.id.email_authentication);
        emailCodeButton= findViewById(R.id.button_email);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide(); // actionBar 숨기기

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        //이메일 인증 하는 부분
        //인증코드 시간초가 흐르는데 이때 인증을 마치지 못하면 인증 코드를 지우게 만든다.
        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                이메일 인증부분을 보여준다.
                Toast.makeText(getApplicationContext(), "메일을 전송했습니다!", Toast.LENGTH_SHORT).show();
                //메일을 보내주는 쓰레드
                MailTread mailTread = new MailTread();
                mailTread.start();

                if(mailSend==0){
                    value=180;
                    //쓰레드 객체 생성
                    BackgrounThread backgroundThread = new BackgrounThread();
                    //쓰레드 스타트
                    backgroundThread.start();
                    mailSend+=1;
                }else{
                    value = 180;
                }

                //이메일이 보내지면 이 부분을 실행시킨다.
//핸들러 객체 생성
                mainHandler=new MainHandler();

            }
        });



        emailCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //이메일로 전송한 인증코드와 내가 입력한 인증코드가 같을 때
                if(emailCodeText.getText().toString().equals(GmailCode)){
                    Toast.makeText(getApplicationContext(), "인증완료", Toast.LENGTH_SHORT).show();
                    SignButton=findViewById(R.id.button_signbutton);
                    SignButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            signUp();
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(), "인증번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuth = FirebaseAuth.getInstance();




    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);


            }
            else{
                //구글 로그인 실패
            }
        }



    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(SignUpActivity.this, "구글 로그인 인증 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, SettingActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
    private void signUp() {


        EditText emailEditText = findViewById(R.id.signup_email);
        String email = emailEditText.getText().toString();
        // 비밀번호
        EditText passwordEditText = findViewById(R.id.signup_password);
        //숫자, 문자, 특수문자 중 2가지 포함(8~15자)
        String pwPattern = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#$%^&*])(?=.*[0-9!@#$%^&*]).{8,15}$";
        String password = passwordEditText.getText().toString();
        Boolean check = Pattern.matches(pwPattern,password);

        EditText passwordEditText2 = findViewById(R.id.signup_password_c);
        String password2=passwordEditText2.getText().toString();

        if(check == true){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && password.equals(password2)) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(SignUpActivity.this, SettingActivity.class);
                                intent.putExtra("emailtext",email);
                                startActivity(intent);
                                Toast.makeText(SignUpActivity.this, "등록 완료", Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(SignUpActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "숫자, 문자, 특수문자 중 2가지 포함해서 8~15자로 입력해주세요",Toast.LENGTH_SHORT).show();
        }


    }


}