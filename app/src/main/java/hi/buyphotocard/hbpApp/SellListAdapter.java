package hi.buyphotocard.hbpApp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SellListAdapter extends RecyclerView.Adapter<ListViewHolder>{

    private ArrayList<SellItemList> arrayList;
    private Context context;
    private Context activityContext;
    private Intent intent;
    private FirebaseStorage storage;
    Uri selectedImageUri;
    private final int GET_GALLERY_IMAGE = 200;

    String dUserNick;
    String sellId;
    Dialog sellDialog;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;
    private DatabaseReference userDB;
    private DatabaseReference userBuyDB;
    private ArrayList buy;
    private int count=0;


    public SellListAdapter(ArrayList<SellItemList> arrayList, String dUserNick, Dialog sellDialog, Context context, Context activityContext) {
        this.arrayList = arrayList;
        this.context = context;
        this.dUserNick = dUserNick;
        this.sellDialog = sellDialog;
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_sell_list_item, parent, false);
        ListViewHolder holder = new ListViewHolder(view);


        return holder;
    }

    //각 item에 매칭을 시켜주는 역할
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.sellListTitle.setText(arrayList.get(position).getTitle());
        holder.sellListGroupTag.setText(arrayList.get(position).getGroupTag());
        holder.sellListAlbumTag.setText(arrayList.get(position).getAlbumTag());
        holder.sellListMemberTag.setText(arrayList.get(position).getMemberTag());
        holder.sellListPrice.setText(String.valueOf(arrayList.get(position).getPrice()));

        sellId = arrayList.get(position).getSellID();

        //이미지 호출을 위해 Glide를 해당 코드 안에 넣기
        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference riverRef = storageReference.child(arrayList.get(position).getImageURI());
        riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView).load(uri).into(holder.sellListIcon);
            }
        });

        SellItemList sellItemList = arrayList.get(position);

        //클릭 리스너
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference = database.getReference();
                databaseReference.child("Sell").child(sellId).child("state").setValue("거래완료");

                userDB = database.getReference();
                userBuyDB = userDB.child("id_list").child(dUserNick).child("buy");
                userBuyDB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        buy = (ArrayList)snapshot.getValue();
                        if(buy == null)
                            count = 0;
                        else
                            count = buy.size();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                databaseReference.child("id_list").child(dUserNick).child("buy").child(String.valueOf(count)).setValue(sellId);

                sellDialog.dismiss();

                Toast.makeText(activityContext, "해당 판매글의 거래완료 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    public int getItemCount() {
        //리스트가 null이 아니면 리스트 크기 가져오고 null이면 0을 가져옴
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class SellViewHolder extends RecyclerView.ViewHolder {
        ImageView sellListIcon;
        TextView sellListTitle;
        TextView sellListGroupTag;
        TextView sellListAlbumTag;
        TextView sellListMemberTag;
        TextView sellListPrice;

        public SellViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sellListIcon = itemView.findViewById(R.id.sellListIcon);
            this.sellListTitle = itemView.findViewById(R.id.sellListTitle);
            this.sellListGroupTag = itemView.findViewById(R.id.sellListGroupTag);
            this.sellListAlbumTag = itemView.findViewById(R.id.sellListAlbumTag);
            this.sellListMemberTag = itemView.findViewById(R.id.sellListMemberTag);
            this.sellListPrice = itemView.findViewById(R.id.sellListPrice);
        }
    }
}