package com.newmview.wifi.activity;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.mview.airtel.R;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;


public class TransparentActivity extends Activity implements View.OnClickListener {
    Button accept_BT;
    Button decline_BT;
    TextView msg_TV;
    private String id;
    private String msg;
    private boolean pressed = false;
    private String key;
    private String user;
    private String img_path;
    private String source;
    private String videoURI;
    private String title;
    private Button ok_Bt;
    private TextView title_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        setContentView(R.layout.sim_alert_dialog);


        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

                        + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        init();

    }

    private void init() {
        msg_TV = findViewById(R.id.message_tv);
        title_tv=findViewById(R.id.title);
        ok_Bt=findViewById(R.id.ok);
ok_Bt.setOnClickListener(this);
        key = getIntent().getStringExtra("key");
        msg = getIntent().getStringExtra("msg");
      if(Utils.checkifavailable(msg)) {
          msg_TV.setText(msg);
      }
      title_tv.setText(Constants.app_name);
        TextView textview=findViewById(R.id.message_tv);
        textview.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                pressed = true;
                this.finish();
                break;

        }

    }
}
