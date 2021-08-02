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


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private ArrayList<SearchItemList> itemList;
    private Context context;

    public  SearchAdapter(ArrayList<SearchItemList> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_itemview,parent,false);
        SearchViewHolder holder = new SearchViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.SearchViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(itemList.get(position).getImageURI())
                .into(holder.search_img);
       // holder.price.setText(itemList.get(position).getPrice());
        holder.price.setText(itemList.get(position).getPrice()+"원");
        holder.seller.setText(itemList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return (itemList != null? itemList.size() : 0);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView search_img;
        TextView price;
        TextView seller;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.search_img = itemView.findViewById(R.id.search_img);
            this.price = itemView.findViewById(R.id.price);
            this.seller = itemView.findViewById(R.id.seller);
        }
    }
}
