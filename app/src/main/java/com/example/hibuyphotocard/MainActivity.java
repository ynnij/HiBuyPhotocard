package com.example.hibuyphotocard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /* Dialog 위한 코드 */
    private Dialog searchDialog;
    private Button search_button;

    private DatabaseReference mDatabase;
    private DatabaseReference photocardDB;
    private DatabaseReference groupDB;
    private DatabaseReference albumDB;
    private DatabaseReference memberDB;

    private AutoCompleteTextView search_Group;
    private AutoCompleteTextView search_Album;
    private AutoCompleteTextView search_Member;

    private List<String> group_list = new ArrayList<String>();
    private List<String> album_list = new ArrayList<String>();
    private List<String> member_list = new ArrayList<String>();


    private LinearLayout group_label;
    private LinearLayout album_label;
    private LinearLayout member_label;

    private List<String> searchKeywordGroup = new ArrayList<String>();
    private List<String> searchKeywordAlbum = new ArrayList<String>();
    private List<String> searchKeywordMember = new ArrayList<String>();
    /* Dialog 위한 코드 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Dialog 위한 코드 */
        searchDialog = new Dialog(MainActivity.this); //dialog 초기화
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        searchDialog.setContentView(R.layout.activity_search_dialog); //xml 레이아웃 파일연결

        search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        /* Dialog 위한 코드 */


    }

    /* Dialog 위한 코드 */
    public void showDialog(){
        searchDialog.show();

        group_label = searchDialog.findViewById(R.id.group_label);
        album_label = searchDialog.findViewById(R.id.album_label);
        member_label = searchDialog.findViewById(R.id.member_label);

        search_Group = searchDialog.findViewById(R.id.search_Group);
        search_Album = searchDialog.findViewById(R.id.search_Album);
        search_Member = searchDialog.findViewById(R.id.search_Member);

        //firebase에서 데이터 불러오기
        mDatabase = FirebaseDatabase.getInstance().getReference();
        photocardDB = mDatabase.child("photocard");
        groupDB = photocardDB.child("group");
        albumDB = photocardDB.child("album");
        memberDB = photocardDB.child("member");
        //각 데이터에 이벤트 붙여주기
        groupDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group_list = (ArrayList)snapshot.getValue();
                ArrayAdapter groupAdapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, group_list);
                search_Group.setAdapter(groupAdapter);

                search_Group.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String data = parent.getAdapter().getItem(position).toString();
                        createTextView(data, group_label, "group",searchKeywordGroup);
                        search_Group.setText("");
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        albumDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                album_list = (ArrayList)snapshot.getValue();
                ArrayAdapter albumAdapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, album_list);
                search_Album.setAdapter(albumAdapter);

                search_Album.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String data = parent.getAdapter().getItem(position).toString();
                        createTextView(data, album_label,"album", searchKeywordAlbum);
                        search_Album.setText("");
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        memberDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                member_list = (ArrayList)snapshot.getValue();
                ArrayAdapter memberAdapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, member_list);
                search_Member.setAdapter(memberAdapter);
                search_Member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String data = parent.getAdapter().getItem(position).toString();
                        createTextView(data, member_label, "member", searchKeywordMember);
                        search_Member.setText("");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
         findViewById() 사용하는 경우 다이얼로그 이름을 반드시 붙이기
         */

        Button closeBtn = searchDialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss(); // 다이얼로그 닫기
            }
        });

    }


    private void createTextView(String text, LinearLayout layout, String type, List list){
        TextView label = new TextView(getApplicationContext());
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
                layout.removeView(v);
                int index = list.indexOf(text);
                list.remove(index);
                Log.d("데이터",list.toString());
            }
        });

        GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(this,R.drawable.custom_label);
        label_drawable.setStroke(4,Color.parseColor(selectStrokeColor(type)),8,5);
        label_drawable.setColor(Color.parseColor(selectColor(type)));
        label.setBackground(getResources().getDrawable(R.drawable.custom_label));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.rightMargin =20;

        label.setLayoutParams(param);
        layout.addView(label);
        list.add(text);
        Log.d("데이터",list.toString());

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

    /* Dialog 위한 코드 */
}