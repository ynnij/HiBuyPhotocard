package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> groupList; //이전 activity에서 넘어온 list
    private ArrayList<String> albumList;
    private ArrayList<String> memberList;

    private String selectGroup; //dialog에서 넘어온 키워드
    private String selectAlbum;
    private String selectMember;

    private ArrayList<SearchItemList> item; // recyclerview에 적용할 아이템 리스트

    private LinearLayout label_area; //라벨 붙일 곳
    private Button search_button; //dialog 버튼

    public boolean makeLabel = false;

    //하단바
    private LinearLayout contractBtn;
    private LinearLayout homeBtn;
    private LinearLayout chatBtn;
    private LinearLayout mypageBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result); // xml 연결

        ActionBar actionBar = getSupportActionBar(); //actionbar 숨기기
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        //이전 activity에서 넘긴 데이터 받기
        selectGroup = getIntent().getStringExtra("selectGroup");
        selectAlbum = getIntent().getStringExtra("selectAlbum");
        selectMember = getIntent().getStringExtra("selectMember");

        item = (ArrayList<SearchItemList>) getIntent().getSerializableExtra("itemList"); // 넘어온 검색 아이템 받기

        //받은 태그 동적 생성
        label_area = findViewById(R.id.label_area);
        if(!selectGroup.equals("")) createTextView(selectGroup,label_area,"group");
        if(!selectAlbum.equals("")) createTextView(selectAlbum,label_area,"album");
        if(!selectMember.equals("")) createTextView(selectMember,label_area,"member");

        //다이얼로그 연결
        SearchDialogActivity dialog = new SearchDialogActivity(SearchResultActivity.this);
        search_button = findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showDialog();
                if(!makeLabel){  //다이얼로그에 라벨 한 번만 생성하도록 함
                    dialog.setDialog(selectGroup,selectAlbum,selectMember);
                    makeLabel = true;
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerview);

        int numberOfColumns = 2; //컬럼 2개로
        int spacing = 30;
        boolean includeEdge = true;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(numberOfColumns, spacing, includeEdge));

        adapter = new SearchAdapter(item,this);
        recyclerView.setAdapter(adapter);

        //하단바
        homeBtn = findViewById(R.id.homeBtn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        chatBtn = findViewById(R.id.chatBtn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FragmentActivity2.class);
                startActivity(intent);
                finish();
            }
        });

        mypageBtn = findViewById(R.id.mypageBtn);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyPageMain.class);
                startActivity(intent);
                finish();
            }
        });

        contractBtn = findViewById(R.id.contractBtn);
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WritingActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void createTextView(String text, LinearLayout layout, String type){
        TextView label = new TextView(getApplicationContext());
        label.setText(text);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        label.setMinWidth(7);
        label.setPadding(15,8,15,8);
        label.setTextSize(15);
        label.setTextColor(Color.parseColor("#433F4F"));
        label.setTypeface(null, Typeface.NORMAL);
        label.setId(0);

        GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(this,R.drawable.custom_label2);
        label_drawable.setStroke(4,Color.parseColor(selectStrokeColor(type)));
        label.setBackground(getResources().getDrawable(R.drawable.custom_label2));

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.setMargins(15,40,0,0);

        label.setLayoutParams(param);
        layout.addView(label);

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

    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }

}

