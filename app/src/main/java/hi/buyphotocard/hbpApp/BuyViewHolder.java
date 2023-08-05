package hi.buyphotocard.hbpApp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


public class BuyViewHolder extends RecyclerView.ViewHolder{
    Button button;
    ImageView sellIcon;
    TextView sellTitle;
    TextView sellGroupTag;
    TextView sellAlbumTag;
    TextView sellMemberTag;
    TextView sellPrice;

    BuyViewHolder(View itemView){
        super(itemView);
        button = itemView.findViewById(R.id.ratingButton);
        sellIcon = itemView.findViewById(R.id.sellIcon);
        sellTitle = itemView.findViewById(R.id.sellTitle);
        sellGroupTag = itemView.findViewById(R.id.sellGroupTag);
        sellAlbumTag = itemView.findViewById(R.id.sellAlbumTag);
        sellMemberTag = itemView.findViewById(R.id.sellMemberTag);
        sellPrice = itemView.findViewById(R.id.sellPrice);

        //리사이클러뷰 버튼 이벤트
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ratingActivity.class); //시작 페이지, 이동할 페이지
//                v.getContext().startActivity(intent);
//            }
//        });



    }
}