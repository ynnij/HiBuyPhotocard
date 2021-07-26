package com.example.hibuyphotocard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SellAdapter extends RecyclerView.Adapter<SellAdapter.SellViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;


    public SellAdapter(ArrayList<SellItemList> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public SellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sell_main_itemview, parent, false);
        SellViewHolder holder = new SellViewHolder(view);
        return holder;
    }

    //각 item에 매칭을 시켜주는 역할
    @Override
    public void onBindViewHolder(@NonNull SellAdapter.SellViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getImageURI())
                .into(holder.sellIcon);
        holder.sellTitle.setText(arrayList.get(position).getTitle());
        holder.sellGroupTag.setText(arrayList.get(position).getGroupTag());
        holder.sellAlbumTag.setText(arrayList.get(position).getAlbumTag());
        holder.sellMemberTag.setText(arrayList.get(position).getMemberTag());
        holder.sellPrice.setText(String.valueOf(arrayList.get(position).getPrice()));


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