package hi.buyphotocard.hbpApp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewHolder extends RecyclerView.ViewHolder{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

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

        //버튼 비활성화하고 안에서 거래 예약 누를 수 있도록 함
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                databaseReference.child("Sell").child("sell01").child("state").setValue("예약중");
//            }
//        });

        //리사이클러뷰 버튼 이벤트
//        button.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                        button.setBackgroundResource(R.drawable.sell_button_second);
//                        button.setText("예약중");
//
//                }
//                return false;
//            }
//        });


    }

    public void setSellState(SellItemList sellItemList){
        String sellState = sellItemList.getState();
        if(sellState.equals("판매중")){
            button.setBackgroundResource(R.drawable.sell_button_basic);
            button.setText("판매중");
        }
        if(sellState.equals("예약중")){
            button.setBackgroundResource(R.drawable.sell_button_second);
            button.setText("예약중");
        }
        if(sellState.equals("거래완료")){
            button.setBackgroundResource(R.drawable.sell_button_third);
            button.setText("거래\n완료");
        }
    }

}