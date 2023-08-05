package hi.buyphotocard.hbpApp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.BuyViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;
    private Intent intent;
    private FirebaseStorage storage;


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
    public void onBindViewHolder(@NonNull BuyViewHolder holder, int position) {
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getImageURI())
//                .into(holder.sellIcon);
        holder.sellTitle.setText(arrayList.get(position).getTitle());
        holder.sellGroupTag.setText(arrayList.get(position).getGroupTag());
        holder.sellAlbumTag.setText(arrayList.get(position).getAlbumTag());
        holder.sellMemberTag.setText(arrayList.get(position).getMemberTag());
        holder.sellPrice.setText(String.valueOf(arrayList.get(position).getPrice()));

        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference riverRef = storageReference.child(arrayList.get(position).getImageURI());
        riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView).load(uri).into(holder.sellIcon);
            }
        });

        //viewHolder 버튼 이벤트 변경을 위한
        SellItemList sellItemList = arrayList.get(position);
        holder.setState(sellItemList);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(v.getContext(), ratingActivity.class);
                intent.putExtra("number",position);
                intent.putExtra("userName",arrayList.get(position).getUserName());
                intent.putExtra("sellID",arrayList.get(position).getSellID());
                intent.putExtra("rateState",arrayList.get(position).getRateState());
                v.getContext().startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        //리스트가 null이 아니면 리스트 크기 가져오고 null이면 0을 가져옴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class BuyViewHolder extends RecyclerView.ViewHolder {
        Button button;
        ImageView sellIcon;
        TextView sellTitle;
        TextView sellGroupTag;
        TextView sellAlbumTag;
        TextView sellMemberTag;
        TextView sellPrice;

        public BuyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.button = itemView.findViewById(R.id.ratingButton);
            this.sellIcon = itemView.findViewById(R.id.sellIcon);
            this.sellTitle = itemView.findViewById(R.id.sellTitle);
            this.sellGroupTag = itemView.findViewById(R.id.sellGroupTag);
            this.sellAlbumTag = itemView.findViewById(R.id.sellAlbumTag);
            this.sellMemberTag = itemView.findViewById(R.id.sellMemberTag);
            this.sellPrice = itemView.findViewById(R.id.sellPrice);


        }

        public void setState(SellItemList sellItemList){
            boolean rateState = sellItemList.getRateState();
            if(rateState==true){
                button.setBackgroundResource(R.drawable.buy_button_second);
                button.setText("평가\n완료");
                button.setTextColor(Color.parseColor("#FFFFFF"));
                button.setEnabled(false);

            }
        }
    }



}