package hi.buyphotocard.hbpApp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class PopupDialogActivity extends Activity {
    private Dialog popupDialog;
    public Context dialogContext;
    public Context activityContext; //다이얼로그가 열린 activity의 context

    private Button yesBtn; //검색 버튼
    private Button closeBtn;


    public PopupDialogActivity(Context context) {
        popupDialog = new Dialog(context);
        popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popupDialog.setContentView(R.layout.explain_popup);
        activityContext = context;
        dialogContext = popupDialog.getOwnerActivity();
    }

    public void showDialog() {
        popupDialog.show();

        closeBtn = popupDialog.findViewById(R.id.popup_finish);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupDialog.dismiss(); // 다이얼로그 닫기
            }
        });
    }



}