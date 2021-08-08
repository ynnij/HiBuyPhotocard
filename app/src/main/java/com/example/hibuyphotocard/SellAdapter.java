package com.example.hibuyphotocard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SellAdapter extends RecyclerView.Adapter<ViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;
    private Intent intent;


    public SellAdapter(ArrayList<SellItemList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_sell_main_itemview, parent, false);
        ViewHolder holder = new ViewHolder(view);


        return holder;
    }

    //각 item에 매칭을 시켜주는 역할
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                intent = new Intent(v.getContext(), SellItemActivity.class);
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


    }

    @Override
    public int getItemCount() {
        //리스트가 null이 아니면 리스트 크기 가져오고 null이면 0을 가져옴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class SellViewHolder extends RecyclerView.ViewHolder {
        ImageView sellIcon;
        TextView sellTitle;
        TextView sellGroupTag;
        TextView sellAlbumTag;
        TextView sellMemberTag;
        TextView sellPrice;

        public SellViewHolder(@NonNull View itemView) {
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