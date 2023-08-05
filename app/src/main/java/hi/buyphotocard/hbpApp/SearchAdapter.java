package hi.buyphotocard.hbpApp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private ArrayList<SearchItemList> itemList;
    private Context context;

    private FirebaseUser user;
    private String email;
    private String nickName;
    private ArrayList wish;
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference wishListDB;
    private Boolean like = false;
    private FirebaseStorage storage;

    public  SearchAdapter(ArrayList<SearchItemList> itemList, Context context){
        this.itemList = itemList;
        this.context = context;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();  // 현재 로그인한 계정 저장
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
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
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if(user.equals(email)){
                        nickName = datas.getKey();
                        wishListDB = mDatabase.child("id_list").child(nickName).child("wishList"); //유저의 찜목록 가져오기
                        wishListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                HashMap<String,String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                                if(hashMap !=null){
                                    Set<String> keySet = hashMap.keySet();
                                    wish = new ArrayList<String>(keySet);
                                }

                                //wish = (ArrayList)dataSnapshot.getValue(); //firebase에서 wishList 받아오면 데이터 필터링 시작
                                if(wish!=null && wish.contains(itemList.get(position).getSellID())){
                                    holder.likeButton.setChecked(true);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference riverRef = storageReference.child(itemList.get(position).getImageURI());
        riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView).load(uri).into(holder.search_img);
            }
        });
        holder.price.setText(itemList.get(position).getPrice()+"원");
        holder.seller.setText(itemList.get(position).getUserName());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SellPageActivity.class);
                intent.putExtra("email",itemList.get(position).getEmail());
                intent.putExtra("groupTag",itemList.get(position).getGroupTag());
                intent.putExtra("albumTag",itemList.get(position).getAlbumTag());
                intent.putExtra("memberTag",itemList.get(position).getMemberTag());
                intent.putExtra("detail",itemList.get(position).getDetail());
                intent.putExtra("delivery",itemList.get(position).getDelivery());
                intent.putExtra("userName",itemList.get(position).getUserName());
                intent.putExtra("imageURI",itemList.get(position).getImageURI());
                intent.putExtra("price",itemList.get(position).getPrice());
                intent.putExtra("state",itemList.get(position).getState());
                intent.putExtra("sellID",itemList.get(position).getSellID());
                intent.putExtra("defect",itemList.get(position).getDefect());
                intent.putExtra("title",itemList.get(position).getTitle());
                if(wish!=null && wish.contains(itemList.get(position).getSellID())){
                    like= true;
                }
                else
                    like =false;
                intent.putExtra("likeState",like);
                v.getContext().startActivity(intent);

            }
        });





    }

    @Override
    public int getItemCount() {
        return (itemList != null? itemList.size() : 0);
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView search_img;
        TextView price;
        TextView seller;
        ToggleButton likeButton;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            this.search_img = itemView.findViewById(R.id.search_img);
            this.price = itemView.findViewById(R.id.price);
            this.seller = itemView.findViewById(R.id.seller);
            this.likeButton = itemView.findViewById(R.id.likeButton);
        }
    }
}