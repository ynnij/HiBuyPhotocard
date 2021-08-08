package com.example.hibuyphotocard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.BuyViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;
    private Intent intent;
//    private Button button;


    public BuyAdapter(ArrayList<SellItemList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public BuyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_buy_main_itemview, parent, false);
        BuyViewHolder holder = new BuyViewHolder(view);
        return holder;
    }

    //각 item에 매칭을 시켜주는 역할
    @Override
    public void onBindViewHolder(@NonNull BuyAdapter.BuyViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImageURI())
                .into(holder.sellIcon);
        holder.sellTitle.setText(arrayList.get(position).getTitle());
        holder.sellGroupTag.setText(arrayList.get(position).getGroupTag());
        holder.sellAlbumTag.setText(arrayList.get(position).getAlbumTag());
        holder.sellMemberTag.setText(arrayList.get(position).getMemberTag());
        holder.sellPrice.setText(String.valueOf(arrayList.get(position).getPrice()));


        //SellItemActivity를 위한 클릭 리스너 생성
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent = new Intent(v.getContext(), SellItemActivity.class);
                intent = new Intent(v.getContext(), ratingActivity.class);
                intent.putExtra("number",position);
                intent.putExtra("groupTag",arrayList.get(position).getGroupTag());
                intent.putExtra("albumTag",arrayList.get(position).getAlbumTag());
                intent.putExtra("memberTag",arrayList.get(position).getMemberTag());
                intent.putExtra("detail",arrayList.get(position).getDetail());
                intent.putExtra("delivery",arrayList.get(position).getDelivery());
                intent.putExtra("userName",arrayList.get(position).getUserName());
                intent.putExtra("imageURI",arrayList.get(position).getImageURI());
                intent.putExtra("price",arrayList.get(position).getPrice());
                v.getContext().startActivity(intent);

            }
        });

//        button.findViewById(R.id.sellIngButton); //sellIngButton으로 바꾸면 강제 종료 됨 왤까
//        button.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            //버튼 클릭 마다 예약 -> 판매 -> 예약 이런식으로 버튼 바뀌게
//            //버튼 클릭시 DB의 state
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_UP:
//                        button.setBackgroundResource(R.drawable.sell_button_second);
//                        button.setText("예약중");
//                        //break;
//                }
//                return true;
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        //리스트가 null이 아니면 리스트 크기 가져오고 null이면 0을 가져옴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class BuyViewHolder extends RecyclerView.ViewHolder {
        ImageView sellIcon;
        TextView sellTitle;
        TextView sellGroupTag;
        TextView sellAlbumTag;
        TextView sellMemberTag;
        TextView sellPrice;

        public BuyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sellIcon = itemView.findViewById(R.id.sellIcon);
            this.sellTitle = itemView.findViewById(R.id.sellTitle);
            this.sellGroupTag = itemView.findViewById(R.id.sellGroupTag);
            this.sellAlbumTag = itemView.findViewById(R.id.sellAlbumTag);
            this.sellMemberTag = itemView.findViewById(R.id.sellMemberTag);
            this.sellPrice = itemView.findViewById(R.id.sellPrice);
        }
    }



}