package hi.buyphotocard.hbpApp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SellAdapter extends RecyclerView.Adapter<ViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;
    private Intent intent;
    private FirebaseStorage storage;
    Uri selectedImageUri;
    private final int GET_GALLERY_IMAGE = 200;


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
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getImageURI())
//                .into(holder.sellIcon);
        holder.sellTitle.setText(arrayList.get(position).getTitle());
        holder.sellGroupTag.setText(arrayList.get(position).getGroupTag());
        holder.sellAlbumTag.setText(arrayList.get(position).getAlbumTag());
        holder.sellMemberTag.setText(arrayList.get(position).getMemberTag());
        holder.sellPrice.setText(String.valueOf(arrayList.get(position).getPrice()));

        //이미지 호출을 위해 Glide를 해당 코드 안에 넣기
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference riverRef = storageReference.child(arrayList.get(position).getImageURI());
        riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView).load(uri).into(holder.sellIcon);
            }
        });

//        holder.sellIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                intent = new Intent(intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"i")
//            }
//        });

        SellItemList sellItemList = arrayList.get(position);
        holder.setSellState(sellItemList);

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
                intent.putExtra("sellID",arrayList.get(position).getSellID());
                intent.putExtra("title",arrayList.get(position).getTitle());
                intent.putExtra("defect",arrayList.get(position).getDefect());
                intent.putExtra("state",arrayList.get(position).getState());
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