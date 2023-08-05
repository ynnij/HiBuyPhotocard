package hi.buyphotocard.hbpApp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private ImageButton button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" M/d HH:mm");
    private ImageButton ImageButton_plus;

    TextView TextView_Bigtitle;
    ImageButton ImageButton_카메라앨범;
    ImageButton ImageButton_사용자신고;
    ImageButton ImageButton_거래완료;
    private ImageButton ImageButton_chattinglist;
    private final int GET_GALLERY_IMAGE = 200;
    private FirebaseStorage storage;
    Uri selectedImageUri;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    int peoplecount = 0;


    ArrayList<SellItemList> sellItem;
    private DatabaseReference mDatabase;
    private DatabaseReference allUsers;
    private DatabaseReference sellListDB;
    private DatabaseReference SellItemList;
    private String email;
    private FirebaseUser user;
    private String nickName;
    private ArrayList sell; // firebase에서 받아온 목록

    private DatabaseReference userDB;

    String destinationNickName; // 채팅 상대방 닉네임

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //statusBbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#C5DCFF"));
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        Intent intent2 = getIntent();
        Bundle bundle = intent2.getExtras();

        destinationUid = bundle.getString("destinationUid"); // 채팅을 당하는 아이디
        userDB = FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap<String,String> hashMap = (HashMap<String, String>) snapshot.getValue();
                destinationNickName = hashMap.get("userName");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        button = findViewById(R.id.messageActivity_button);
        editText = findViewById(R.id.messageActivity_editText);
        TextView_Bigtitle = findViewById(R.id.TextView_Bigtitle);
//        EditText edit = new EditText(this);
//        edit.setWidth(200);
//        edit.setText("Katou", BufferType.NORMAL);
        editText.setPadding(40, 0, 48, 0);


        recyclerView = findViewById(R.id.messageActivity_reclclerview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);


                if (chatRoomUid == null) {
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                        }
                    });

                } else {

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
        BottomSheetDialog searchDialog = new BottomSheetDialog(MessageActivity.this); //dialog 초기화
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        searchDialog.setContentView(R.layout.activity_chat_plusbottonclick); //xml 레이아웃 파일연결


        //sellListDialog 코드
        SellDialogActivity sellDialog = new SellDialogActivity(MessageActivity.this);

        ImageButton_plus = findViewById(R.id.ImageButton_plus);
        ImageButton_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                searchDialog.show();

                ImageButton_사용자신고 = searchDialog.findViewById(R.id.ImageButton_사용자신고);
                ImageButton_사용자신고.setClickable(true);
//                ImageView_background = findViewById(R.id.ImageView_background);

                ImageButton_사용자신고.setOnClickListener(new View.OnClickListener() {
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


                ImageButton_카메라앨범 = searchDialog.findViewById(R.id.ImageButton_카메라앨범);
                ImageButton_카메라앨범.setClickable(true);
                ImageButton_카메라앨범.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, GET_GALLERY_IMAGE);
                    }
                });


                //***
                //
                //
                // 거래완료 버튼 기능
                //
                //
                // ***
                ImageButton_거래완료 = searchDialog.findViewById(R.id.ImageButton_거래완료);
                ImageButton_거래완료.setClickable(true);
                ImageButton_거래완료.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Activity로 설정하는 방법
//                        Intent intent = new Intent(MessageActivity.this, SellListMainActivity.class);
//                        startActivity(intent);

                        //다이얼로그로 설정하는 방법
                        searchDialog.dismiss();
                        sellDialog.showDialog(destinationNickName);

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
                Intent intent = new Intent(MessageActivity.this, FragmentActivity2.class);//FragmentActivity2.class // viewPager_
                startActivity(intent);
            }
        });


    }

    void checkChatRoom() {

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot item : dataSnapshot1.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    if (chatModel.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                        button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;
        UserModel userModel;
        private ViewGroup parent;
        private int viewType;

        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {

                public void onDataChange(DataSnapshot dataSnapshot2) {
                    userModel = dataSnapshot2.getValue(UserModel.class);
                    TextView_Bigtitle.setText(userModel.userName);
                    getMessageList();

                }


                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot3) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot3.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modifiy = item.getValue(ChatModel.Comment.class);
                        comment_modifiy.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_modifiy);
                        comments.add(comment_origin);
                    }
                    Log.d("확인", String.valueOf(comments.size()));

                    if (comments.size() != 0) {
                        if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                    .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    notifyDataSetChanged();
                                    recyclerView.scrollToPosition(comments.size() - 1);


                                }
                            });
                        } else {
                            notifyDataSetChanged();
                            recyclerView.scrollToPosition(comments.size() - 1);
                        }
                    }


                    //바꾸기전
                    /*
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull  Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);


                            }
                        });
                    }
                    */

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg, parent, false);


            return new MessageViewHolder(view);
        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            //내가보낸 메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);

//                messageViewHolder.messageItem_textview_timestamp_left.setGravity(Gravity.RIGHT);
//                messageViewHolder.messageItem_textview_timestamp_left.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
//                messageViewHolder.messageItem_textview_timestamp_right.setVisibility(View.GONE);
                messageViewHolder.textView_timestamp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                setReadCounter(position, messageViewHolder.messageItem_textview_readCounter_left);


                messageViewHolder.textView_message.setSingleLine(true);
                messageViewHolder.textView_message.setEllipsize(TextUtils.TruncateAt.END);
                messageViewHolder.textView_message.setTextColor(Color.parseColor("#433F4F"));
                messageViewHolder.textView_message.setTypeface(null, Typeface.NORMAL);
                messageViewHolder.textView_message.setPadding(15, 8, 15, 8);


                GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(getApplication(), R.drawable.custom_label1);
                label_drawable.setStroke(4, Color.parseColor("#83D2FE"));
                messageViewHolder.textView_message.setBackground(getResources().getDrawable(R.drawable.custom_label1));


                //상대방이 보낸 메세지
            } else {
//                Glide.with(holder.itemView.getContext())
//                        .load(userModel.profileImageUrl)
//                        .apply(new RequestOptions().circleCrop())
//                        .into(messageViewHolder.imageView_profile);


                storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference riversRef = storageReference.child(userModel.profileImageUrl);

                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(holder.itemView.getContext())
                                .load(uri)
                                .apply(new RequestOptions().circleCrop())
                                .into(messageViewHolder.imageView_profile);
                    }
                });


                messageViewHolder.textview_name.setText(userModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);

                messageViewHolder.textView_timestamp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                messageViewHolder.messageItem_textview_timestamp_right.setGravity(Gravity.RIGHT);
//                messageViewHolder.messageItem_textview_timestamp_right.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//                messageViewHolder.messageItem_textview_timestamp_left.setVisibility(View.GONE);
                setReadCounter(position, messageViewHolder.messageItem_textview_readCounter_right);


                messageViewHolder.textView_message.setSingleLine(true);
                messageViewHolder.textView_message.setEllipsize(TextUtils.TruncateAt.END);
                messageViewHolder.textView_message.setTextColor(Color.parseColor("#433F4F"));
                messageViewHolder.textView_message.setTypeface(null, Typeface.NORMAL);
                messageViewHolder.textView_message.setPadding(15, 8, 15, 8);


                GradientDrawable label_drawable = (GradientDrawable) ContextCompat.getDrawable(getApplication(), R.drawable.custom_label2);
                label_drawable.setStroke(4, Color.parseColor("#FFB8B8"));
                messageViewHolder.textView_message.setBackground(getResources().getDrawable(R.drawable.custom_label2));


            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);
//            messageViewHolder.messageItem_textview_timestamp_right.setText(time);
//            messageViewHolder.messageItem_textview_timestamp_left.setText(time);


        }

        void setReadCounter(int position, TextView textView) {
            if (peoplecount == 0) {


                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot4) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot4.getValue();
                        peoplecount = users.size();
                        int count = users.size() - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                int count = peoplecount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
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
            public TextView messageItem_textview_readCounter_left;
            public TextView messageItem_textview_readCounter_right;
//            public TextView messageItem_textview_timestamp_left;
//            public TextView messageItem_textview_timestamp_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_textView_message);
                textview_name = view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = view.findViewById(R.id.messageItem_textview_timestamp);
                messageItem_textview_readCounter_left = view.findViewById(R.id.messageItem_textview_readCounter_left);
                messageItem_textview_readCounter_right = view.findViewById(R.id.messageItem_textview_readCounter_right);

//                messageItem_textview_timestamp_left = (TextView)view.findViewById(R.id.messageItem_textview_timestamp_left);
//                messageItem_textview_timestamp_right = (TextView)view.findViewById(R.id.messageItem_textview_timestamp_right);
////
//
            }
        }


    }

    @Override
    public void onBackPressed() {
        databaseReference.removeEventListener(valueEventListener);
        finish();
    }
}