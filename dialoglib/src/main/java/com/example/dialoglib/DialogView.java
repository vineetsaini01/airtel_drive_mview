package com.example.dialoglib;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogView {
    Context context;
    Dialog dialog;
    Button okbutton;
    Button cancelButton;
    TextView messageTv,titleTv;
    ImageView img;


public DialogView(Context context)
{
    this.context=context;
    dialog = new Dialog(context);

}


    public Dialog getDialog() {
        return dialog;
    }

    public void setLayoutForDialog(String message,String positiveBtn,String negativeBtn,String title,int id) {

       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       LayoutInflater inflater = (LayoutInflater) context
               .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

       dialog.setContentView(R.layout.dialog_view1);
       dialog.setCanceledOnTouchOutside(true);
       Window window = dialog.getWindow();
       window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
       dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
       dialog.show();
       okbutton = dialog.findViewById(R.id.ok);
   cancelButton = dialog.findViewById(R.id.cancel);
   messageTv=dialog.findViewById(R.id.message_tv);
   titleTv=dialog.findViewById(R.id.title);
   img=dialog.findViewById(R.id.img);
   messageTv.setText(message);
   okbutton.setText(positiveBtn);
   cancelButton.setText(negativeBtn);
   titleTv.setText(title);
   img.setImageDrawable(context.getResources().getDrawable(id));
      /* okbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            //   finish();
           }
       });
       cancelButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           }
       });*/
   }

}
