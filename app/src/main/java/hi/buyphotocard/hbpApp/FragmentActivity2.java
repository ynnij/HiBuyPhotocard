package hi.buyphotocard.hbpApp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class FragmentActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main2);
//        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_frame, new PeopleFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_frame2, new ChatFragment()).commit();

    }
}