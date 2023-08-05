package hi.buyphotocard.hbpApp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SellDialogActivity extends Activity {
    private Dialog sellDialog;
    public Context dialogContext;
    public Context activityContext; //다이얼로그가 열린 activity의 context

    private Button closeBtn;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<SellItemList> arrayList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private FirebaseUser user;
    ArrayList<SellItemList> sellItem;
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference sellListDB;
    private DatabaseReference SellItemList;

    private String email;
    private String nickName;
    private ArrayList sell; // firebase에서 받아온 user의 sell목록

    String dUserName;

    public SellDialogActivity(Context context){
        sellDialog = new Dialog(context);
        sellDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sellDialog.setContentView(R.layout.activity_sell_list_dialog);
        activityContext = context;
        dialogContext = sellDialog.getOwnerActivity();

        closeBtn = sellDialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellDialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }
    public void showDialog(String destinationUserNickName){
        dUserName = destinationUserNickName;
        recyclerView = sellDialog.findViewById(R.id.recyclerviewSellList); //리사이클러뷰 아이디 연결
        recyclerView.setHasFixedSize(true); //기존 성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            for(UserInfo profile : user.getProviderData()){
                email = profile.getEmail();
                Log.d("확인",email);

            }
        }

        sellItem = new ArrayList<>();
        sellItem.clear();


        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결
        allUsers = mDatabase.child("id_list");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datas : snapshot.getChildren()) {
                    String user = datas.child("email").getValue(String.class);
                    if(user.equals(email)){
                        Log.d("확인", "같은 이메일은 "+email);
                        nickName = datas.getKey();

                        sellListDB = mDatabase.child("id_list").child(nickName).child("sell"); //유저의 찜목록 가져오기
                        sellListDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                sell = (ArrayList)dataSnapshot.getValue(); //firebase에서 wishList 받아오면 데이터 필터링 시작
                                if(sell != null){
                                    SellItemList = mDatabase.child("Sell");
                                    SellItemList.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot sellData) {
                                            for(DataSnapshot snapshot: sellData.getChildren()){
                                                SellItemList itemList = snapshot.getValue(SellItemList.class);
                                                if(sell.contains(itemList.getSellID())&& !itemList.getState().equals("거래완료")){
                                                    //거래완료가 아니면서 판매자의 sell DB에 있는 판매글 목록만 추가
                                                    sellItem.add(itemList);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                else{

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

        adapter = new SellListAdapter(sellItem, destinationUserNickName, sellDialog,this, activityContext);
        recyclerView.setAdapter(adapter);

        sellDialog.show();

    }
}
