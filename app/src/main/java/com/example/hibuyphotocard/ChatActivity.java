package com.example.hibuyphotocard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.TimeZone;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

import static com.google.firebase.database.ServerValue.TIMESTAMP;

public class ChatActivity extends AppCompatActivity {

    private String destinationUid;
    private ImageButton button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M/d HH:mm aa");
    private ImageButton ImageButton_plus;
    TextView TextView_사용자신고;
    TextView TextView_카메라앨범;
    TextView TextView_거래완료등록;
    ImageView ImageView_background;
    ImageButton ImageButton_chattinglist;
    TextView TextView_Bigtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid"); // 채팅을 당하는 아이디
        button = (ImageButton) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);
        TextView_Bigtitle =findViewById(R.id.TextView_Bigtitle);


        recyclerView = (RecyclerView)findViewById(R.id.messageActivity_reclclerview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid,true);
                chatModel.users.put(destinationUid,true);




                if(chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });

                }else {

                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

                        public void onComplete(@NonNull Task<Void> task) {
                            editText.setText("");
                        }
                    });

                }


            }
        });
        checkChatRoom();




        /* Dialog 위한 코드 */
        BottomSheetDialog searchDialog = new BottomSheetDialog(ChatActivity.this); //dialog 초기화
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        searchDialog.setContentView(R.layout.activity_chat_plusbottonclick); //xml 레이아웃 파일연결

        ImageButton_plus = findViewById(R.id.ImageButton_plus);
        ImageButton_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                searchDialog.show();

                TextView_사용자신고 = searchDialog.findViewById(R.id.TextView_사용자신고);
                TextView_거래완료등록 = searchDialog.findViewById(R.id.TextView_거래완료등록);
                TextView_카메라앨범 = searchDialog.findViewById(R.id.TextView_카메라앨범);
                ImageView_background = findViewById(R.id.ImageView_background);

                TextView_사용자신고.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.setType("plain/text");
                        String[] address = {"ghdwn643@naver.com"};
                        email.putExtra(Intent.EXTRA_EMAIL, address);
                        email.putExtra(Intent.EXTRA_SUBJECT, "Hibuyphotocard/개발자문의및신고");
                        email.putExtra(Intent.EXTRA_TEXT, "문의 및 신고하고 싶은 내용을 적어주세요.");
                        startActivity(email);
                    }
                });

                TextView_거래완료등록.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchDialog.dismiss();
                        editText.setFocusable(false);
                        editText.setClickable(false);
                        ImageButton_plus.setClickable(false);
                        ImageButton_plus.setFocusable(false);
//                        ImageView_background.setHighlightColor(Color.parseColor("#20000000"));
                        ImageView_background.setColorFilter(Color.parseColor("#20000000"));
                        editText.setText("거래가 완료된 채팅방입니다.");


                    }
                });







            }
        });
        /* Dialog 위한 코드 */



        ImageButton_chattinglist = findViewById(R.id.ImageButton_chattinglist);
        ImageButton_chattinglist.setClickable(true);
        ImageButton_chattinglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton_chattinglist.setColorFilter(Color.parseColor("#ffffff"));
                Intent intent = new Intent(ChatActivity.this,ViewPager_MainActivity.class );//FragmentActivity2.class // viewPager_
                startActivity(intent);
            }
        });
















    }

    void  checkChatRoom(){

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    ChatModel  chatModel = item.getValue(ChatModel.class);
                    if(chatModel.users.containsKey(destinationUid)){
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        List<ChatModel.Comment> comments;
        UserModel userModel;
        private ViewGroup parent;
        private int viewType;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {

                public void onDataChange(DataSnapshot dataSnapshot) {
                    userModel = dataSnapshot.getValue(UserModel.class);
                    getMessageList();

                }


                public void onCancelled(DatabaseError databaseError) {

                }
            });






        }

        void getMessageList(){

            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();

                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메세지가 갱신
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);


            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder)holder);


            //내가보낸 메세지
            if(comments.get(position).uid.equals(uid)){
                messageViewHolder.textView_message.setText(comments.get(position).message);
//                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
//                messageViewHolder.textView_message.setTextSize(25);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textView_timestamp.setGravity(Gravity.RIGHT);
                messageViewHolder.textView_timestamp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                //상대방이 보낸 메세지
            }else {

//                Glide.with(holder.itemView.getContext())
//                        .load(userModel.profileImageUrl)
//                        .apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder.imageView_profile);
//                messageViewHolder.textview_name.setText(userModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
//                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                messageViewHolder.textView_timestamp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);


        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;


            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView)view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView)view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView)view.findViewById(R.id.messageItem_textview_timestamp);
            }
        }
    }





}