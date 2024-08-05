package com.newmview.wifi.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mview.airtel.R;
import com.newmview.wifi.helper.DialogClass;
import com.newmview.wifi.mView_HealthStatus;

public class DialogClassWebTest {

    private static DialogClass.TraceRouteTask traceroute=null;
    private static String date_time;
    private final String feature;
    //  private  String date_time=null;
    private TextView rtt_avg_val_Tv,rtt_dev_val_Tv,rtt_min_val_Tv,host_Tv_val,time_val_Tv,rtt_max_val_Tv,packetLoss_Tv_val;
    private TextView host_Tv_Val,webPageUrlVal,webPageLoadTimeVal;
    private LinearLayout pingLL;

    public DialogClassWebTest(String feature)
    {
        this.feature=feature;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void webReport(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.web_layout, null);
        TableLayout pingTL=view.findViewById(R.id.pingTL);
        rtt_min_val_Tv=view.findViewById(R.id.rtt_min_val_Tv);
        rtt_max_val_Tv=view.findViewById(R.id.rtt_max_val_Tv);
        rtt_avg_val_Tv=view.findViewById(R.id.rtt_avg_val_Tv);
        rtt_dev_val_Tv=view.findViewById(R.id.rtt_dev_val_Tv);
        time_val_Tv=view.findViewById(R.id.time_val_Tv);
        host_Tv_Val=view.findViewById(R.id.host_val_Tv);
        webPageUrlVal=view.findViewById(R.id.webPageUrlVal);
        webPageLoadTimeVal=view.findViewById(R.id.webPageLoadTimeVal);
        packetLoss_Tv_val=view.findViewById(R.id.packetLoss_val_Tv);
        rtt_max_val_Tv.setText(mView_HealthStatus.packetLossNew);
        time_val_Tv.setText(mView_HealthStatus.dataUsedNew);
        rtt_dev_val_Tv.setText(mView_HealthStatus.dnsResolutionTime);
        rtt_avg_val_Tv.setText(mView_HealthStatus.latencyNew);
        webPageUrlVal.setText(mView_HealthStatus.webPageUrl);
        webPageLoadTimeVal.setText(mView_HealthStatus.webPageLoadTime);
        host_Tv_Val.setText(mView_HealthStatus.no_of_hopsNew);
        packetLoss_Tv_val.setText("5");
        pingLL=view.findViewById(R.id.pingLL);

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }






}
