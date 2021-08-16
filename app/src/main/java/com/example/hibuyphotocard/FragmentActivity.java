package com.example.hibuyphotocard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;



import java.util.HashMap;
import java.util.Map;


public class FragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_frame, new PeopleFragment()).commit();
//        getSupportFragmentManager().beginTransaction().replace(R.id.FrameLayout_frame, new ChatFragment()).commit();


    }

}

