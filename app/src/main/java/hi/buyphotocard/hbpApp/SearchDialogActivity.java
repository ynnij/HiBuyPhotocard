package hi.buyphotocard.hbpApp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class SearchDialogActivity extends Activity {
    private Dialog searchDialog;
    public Context dialogContext;
    public Context activityContext; //다이얼로그가 열린 activity의 context

    private Button yesBtn; //검색 버튼
    private Button closeBtn;

    private DatabaseReference mDatabase;
    private DatabaseReference idolDB;
    private DatabaseReference albumDB;
    private DatabaseReference memberDB;
    private DatabaseReference SellItemList;

    private AutoCompleteTextView search_Group;
    private AutoCompleteTextView search_Album;
    private AutoCompleteTextView search_Member;

    private LinearLayout group_label;
    private LinearLayout album_label;
    private LinearLayout member_label;

    //선택한 키워드 저장하는 변수
    private String selectGroup ="";
    private String selectAlbum ="";
    private String selectMember ="";


    private ArrayList<SearchItemList> itemList;  //전송할 리스트

    public SearchDialogActivity(Context context){
        searchDialog = new Dialog(context);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.activity_search_dialog);
        activityContext = context;
        dialogContext = searchDialog.getOwnerActivity();
    }

    public void showDialog(){
        searchDialog.show();

        group_label = searchDialog.findViewById(R.id.group_label); // 선택된 값이 표시될 linearLayout
        album_label = searchDialog.findViewById(R.id.album_label);
        member_label = searchDialog.findViewById(R.id.member_label);

        search_Group = searchDialog.findViewById(R.id.search_Group); // 검색 리스트
        search_Album = searchDialog.findViewById(R.id.search_Album);
        search_Member = searchDialog.findViewById(R.id.search_Member);

        mDatabase = FirebaseDatabase.getInstance().getReference(); //firebase 연결

        SellItemList = mDatabase.child("Sell");
        itemList = new ArrayList<>(); //검색 결과화면으로 넘길 리스트

        yesBtn = searchDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() { //다음 activity로 값 전송
            @Override
            public void onClick(View v) {
                itemList.clear();
                SellItemList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            SearchItemList sellItemList = snapshot.getValue(SearchItemList.class);
                            if(!sellItemList.getState().equals("거래완료")){ // 판매글 상태가 거래완료가 아닌 글만 조건 체크
                                if(selectGroup.equals("")){  //그룹 선택 안한 경우(하위 항목 선택 X)
                                    itemList.add(sellItemList); //모든 판매글 저장
                                }
                                else {
                                    if(selectAlbum.equals("")&&selectMember.equals("") //그룹만 선택된 경우
                                            && sellItemList.getGroupTag().equals(selectGroup))
                                        itemList.add(sellItemList);
                                    else if(selectAlbum.equals("") && sellItemList.getGroupTag().equals(selectGroup)
                                            && sellItemList.getMemberTag().equals(selectMember))  // 그룹, 멤버만 선택한 경우
                                        itemList.add(sellItemList);
                                    else if(selectMember.equals("") && sellItemList.getGroupTag().equals(selectGroup) //그룹, 앨범만 선택한 경우
                                            && sellItemList.getAlbumTag().equals(selectAlbum))
                                        itemList.add(sellItemList);
                                    else {
                                        if(sellItemList.getGroupTag().equals(selectGroup) // 전체 선택한 값이 있는 경우
                                                && sellItemList.getAlbumTag().equals(selectAlbum)
                                                && sellItemList.getMemberTag().equals(selectMember))
                                            itemList.add(sellItemList);
                                    }
                                }
                            }
                        }
                        if(itemList.isEmpty()){
                            Intent intent = new Intent(searchDialog.getContext(), NoResultActivity.class);
                            intent.putExtra("selectGroup", selectGroup);
                            intent.putExtra("selectAlbum", selectAlbum);
                            intent.putExtra("selectMember", selectMember);
                            searchDialog.getContext().startActivity(intent);
                            searchDialog.hide();
                        }
                        else {
                            Intent intent = new Intent(searchDialog.getContext(),SearchResultActivity.class);
                            intent.putExtra("selectGroup", selectGroup);
                            intent.putExtra("selectAlbum", selectAlbum);
                            intent.putExtra("selectMember", selectMember);
                            intent.putExtra("itemList",itemList);
                            searchDialog.getContext().startActivity(intent);
                            searchDialog.hide();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        closeBtn = searchDialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss(); // 다이얼로그 닫기
            }
        });

        //firebase에서 아이돌 데이터 불러오기
        idolDB = mDatabase.child("idol");
        idolDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,Object> group = (Map<String, Object>)snapshot.getValue();
                Object [] groupList = group.keySet().toArray();
                ArrayAdapter groupAdapter = new ArrayAdapter(activityContext,
                        android.R.layout.simple_list_item_1,groupList);
                search_Group.setAdapter(groupAdapter);

                search_Group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String dataGroup = parent.getAdapter().getItem(position).toString();
                        if(selectGroup.equals("")){
                            selectGroup = dataGroup;
                            createTextView(dataGroup,group_label,"group");
                            selectOther(dataGroup);
                        }
                        else{
                            Toast.makeText(activityContext,
                                    "하나만 선택해주세요.",Toast.LENGTH_SHORT).show();
                            selectOther(selectGroup);
                        }
                        search_Group.setText("");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void initSelectText(String type){ //더블클릭 시 select String 배열 초기화
        if(type.equals("group"))
            this.selectGroup = "";
        else if(type.equals("album"))
            this.selectAlbum = "";
        else if(type.equals("member"))
            this.selectMember = "";
    }
    public void selectOther(String group){
        //앨범 선택
        albumDB = mDatabase.child("idol").child(group).child("album");
        albumDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList album = (ArrayList)snapshot.getValue();
                ArrayAdapter albumAdapter = new ArrayAdapter(activityContext,
                        android.R.layout.simple_list_item_1,album);
                search_Album.setAdapter(albumAdapter);
                search_Album.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String dataAlbum = parent.getAdapter().getItem(position).toString();
                        if(selectAlbum.equals("")){
                            selectAlbum=dataAlbum;
                            createTextView(dataAlbum,album_label,"album");
                        }
                        else{
                            Toast.makeText(activityContext,
                                    "하나만 선택해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        search_Album.setText("");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberDB = mDatabase.child("idol").child(group).child("member");
        memberDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList member = (ArrayList)snapshot.getValue();
                ArrayAdapter memberAdapter = new ArrayAdapter(activityContext,
                        android.R.layout.simple_list_item_1, member);
                search_Member.setAdapter(memberAdapter);
                search_Member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String dataMemeber = parent.getAdapter().getItem(position).toString();
                        if(selectMember.equals("")){
                            selectMember=dataMemeber;
                            createTextView(dataMemeber,member_label,"member");
                        }
                        else{
                            Toast.makeText(activityContext,
                                    "하나만 선택해주세요.",Toast.LENGTH_SHORT).show();
                        }
                        search_Member.setText("");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //검색 결과화면에서 부를 경우 넘어온 값을 그대로 사용하도록 설정하는 메서드
    public void setDialog(String group, String album, String member){
        if(!album.equals("")){
            this.selectAlbum = album;
            createTextView(album,album_label,"album");
        }
        if(!member.equals("")){
            this.selectMember = member;
            createTextView(member,member_label,"member");
        }
        if(!group.equals("")){
            this.selectGroup = group;
            createTextView(group,group_label,"group");
            selectOther(selectGroup);
        }

    }
    private void createTextView(String text, LinearLayout layout, String type){
        TextView label = new TextView(searchDialog.getContext());
        label.setText(text);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setPadding(15,8,15,8);
        label.setTextSize(12);
        label.setTextColor(Color.parseColor("#ffffff"));
        label.setTypeface(null, Typeface.NORMAL);
        label.setId(0);
        label.setLinksClickable(true);
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSelectText(type);
                layout.removeView(v);
            }
        });

        GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(searchDialog.getContext(),R.drawable.custom_label);
        label_drawable.setStroke(4,Color.parseColor(selectStrokeColor(type)),8,5);
        label_drawable.setColor(Color.parseColor(selectColor(type)));
        label.setBackground(activityContext.getResources().getDrawable(R.drawable.custom_label));


        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.rightMargin =20;

        label.setLayoutParams(param);
        layout.addView(label);

    }

    private String selectColor(String type){
        if(type.equals("group"))
            return "#FFC8C8";
        else if(type.equals("album"))
            return "#83D2FE";
        else if(type.equals("member"))
            return "#FFE790";
        return "#565656";
    }
    private String selectStrokeColor(String type){
        if(type.equals("group"))
            return "#FFB8B8";
        else if(type.equals("album"))
            return "#41BBFF";
        else if(type.equals("member"))
            return "#FCD54C";
        return "#565656";
    }

}