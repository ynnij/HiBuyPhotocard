package com.example.hibuyphotocard;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder{
    Button button;
    ImageView sellIcon;
    TextView sellTitle;
    TextView sellGroupTag;
    TextView sellAlbumTag;
    TextView sellMemberTag;
    TextView sellPrice;

    ViewHolder(View itemView){
        super(itemView);
        button = itemView.findViewById(R.id.sellIngButton);
        sellIcon = itemView.findViewById(R.id.sellIcon);
        sellTitle = itemView.findViewById(R.id.sellTitle);
        sellGroupTag = itemView.findViewById(R.id.sellGroupTag);
        sellAlbumTag = itemView.findViewById(R.id.sellAlbumTag);
        sellMemberTag = itemView.findViewById(R.id.sellMemberTag);
        sellPrice = itemView.findViewById(R.id.sellPrice);


        //리사이클러뷰 버튼 이벤트
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        button.setBackgroundResource(R.drawable.sell_button_second);
                        button.setText("예약중");

                }
                return false;
            }
        });


    }
}