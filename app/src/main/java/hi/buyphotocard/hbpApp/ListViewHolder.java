package hi.buyphotocard.hbpApp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListViewHolder extends RecyclerView.ViewHolder{

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    ImageView sellListIcon;
    TextView sellListTitle;
    TextView sellListGroupTag;
    TextView sellListAlbumTag;
    TextView sellListMemberTag;
    TextView sellListPrice;


    ListViewHolder(View itemView){
        super(itemView);
        this.sellListIcon = itemView.findViewById(R.id.sellListIcon);
        this.sellListTitle = itemView.findViewById(R.id.sellListTitle);
        this.sellListGroupTag = itemView.findViewById(R.id.sellListGroupTag);
        this.sellListAlbumTag = itemView.findViewById(R.id.sellListAlbumTag);
        this.sellListMemberTag = itemView.findViewById(R.id.sellListMemberTag);
        this.sellListPrice = itemView.findViewById(R.id.sellListPrice);

    }


}