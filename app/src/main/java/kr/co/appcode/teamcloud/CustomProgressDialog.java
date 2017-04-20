package kr.co.appcode.teamcloud;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

public class CustomProgressDialog extends Dialog {

   public CustomProgressDialog(Context context){
       super(context);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}
