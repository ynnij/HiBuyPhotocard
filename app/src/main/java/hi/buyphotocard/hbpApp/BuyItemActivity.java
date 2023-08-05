package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BuyItemActivity extends AppCompatActivity {

    private Intent intent;
    //private int number;
    private String itemGroupTag;
    private String itemAlbumTag;
    private String itemMemberTag;
    private String itemDetail;
    private String itemDelivery;
    private String itemUserName;
    private String itemImage;
    private Integer itemPrice;
    private ImageView imageView;
    private TextView groupTagView;
    private TextView albumTagView;
    private TextView memberTagView;
    private TextView detailView;
    private TextView deliveryView;
    private TextView userNameView;
    private TextView priceView;
    private Button ratingButton;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_buy_post);

        intent = getIntent();
        //number = intent.getIntExtra("number",-1);
        itemGroupTag = intent.getStringExtra("groupTag");
        itemAlbumTag = intent.getStringExtra("albumTag");
        itemMemberTag = intent.getStringExtra("memberTag");
        itemDetail = intent.getStringExtra("detail");
        itemDelivery = intent.getStringExtra("delivery");
        itemUserName = intent.getStringExtra("userName");
        itemImage = intent.getStringExtra("imageURI");
        itemPrice = intent.getIntExtra("price",0);

        groupTagView = findViewById(R.id.sellListGroupTag);
        albumTagView = findViewById(R.id.sellListAlbumrTag);
        memberTagView = findViewById(R.id.sellListMemberTag);
        detailView = findViewById(R.id.sellListDetail);
        deliveryView = findViewById(R.id.sellListDelivery);
        userNameView = findViewById(R.id.sellListUserName);
        imageView = findViewById(R.id.sellListImage);
        priceView = findViewById(R.id.sellListPrice);
        ratingButton = findViewById(R.id.ratingButton);

        groupTagView.setText(itemGroupTag);
        albumTagView.setText(itemAlbumTag);
        memberTagView.setText(itemMemberTag);
        detailView.setText(itemDetail);
        deliveryView.setText(itemDelivery);
        userNameView.setText(itemUserName);
        priceView.setText(String.valueOf(itemPrice));
        Glide.with(imageView).load(itemImage).into(imageView);

        //평가버튼
//        ratingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), ratingActivity.class); //시작 페이지, 이동할 페이지
//                v.getContext().startActivity(intent);
//            }
//        });

    }



}