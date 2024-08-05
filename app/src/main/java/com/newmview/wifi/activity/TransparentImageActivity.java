package com.newmview.wifi.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mview.airtel.R;
import com.newmview.wifi.other.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class TransparentImageActivity  extends Activity {


    private ImageView imgView;
    private String imgPath;
    private ProgressBar progressBar;
    private TextView txtView;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.image_preview);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

                        + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        init();
        setTextToView();
        setImageToView();

    }

    private void setTextToView() {
        if(Utils.checkifavailable(msg))
        {
            txtView.setText(Html.fromHtml(msg));
        }
    }

    private void setImageToView() {

        if (Utils.checkifavailable(imgPath)) {
            Picasso.with(this).load(imgPath).error(R.drawable.default_image).into(imgView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    Log.d("TAG", "onSuccess");
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);

                }
            });

        }

    }

    private void init() {
        imgView=findViewById(R.id.imgView);
        imgPath=getIntent().getStringExtra("image");
        msg=getIntent().getStringExtra("msg");
        progressBar=findViewById(R.id.thumbnailprogress);
        txtView=findViewById(R.id.textView);
    }




}
