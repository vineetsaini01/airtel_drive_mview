package com.newmview.wifi.helper;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.mview.airtel.R;
import com.newmview.wifi.mView_HealthStatus;

public class CallOutput {
    private static DialogClass.TraceRouteTask traceroute=null;
    private final String feature;
    private TextView rtt_avg_val_Tv,rtt_dev_val_Tv,rtt_min_val_Tv,host_Tv_val,time_val_Tv,rtt_max_val_Tv,packetLoss_Tv_val;
    private TextView host_Tv_Val;
    private LinearLayout pingLL;

    public CallOutput(String feature)
    {
        this.feature=feature;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void showCallAlert(Context context) {
        callTest(context);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void callTest(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.call_layout, null);
        TableLayout pingTL=view.findViewById(R.id.pingTL);
        rtt_min_val_Tv=view.findViewById(R.id.rtt_min_val_Tv);
        rtt_max_val_Tv=view.findViewById(R.id.rtt_max_val_Tv);
        rtt_avg_val_Tv=view.findViewById(R.id.rtt_avg_val_Tv);
        rtt_dev_val_Tv=view.findViewById(R.id.rtt_dev_val_Tv);
        time_val_Tv=view.findViewById(R.id.time_val_Tv);
        host_Tv_Val=view.findViewById(R.id.host_val_Tv);
        packetLoss_Tv_val=view.findViewById(R.id.packetLoss_val_Tv);
        pingLL=view.findViewById(R.id.pingLL);
        rtt_avg_val_Tv.setText(mView_HealthStatus.CALLType);
        rtt_dev_val_Tv.setText(mView_HealthStatus.ROAMING);
        rtt_max_val_Tv.setText(mView_HealthStatus.CALLERDURATION);
        rtt_min_val_Tv.setText(mView_HealthStatus.CALLERPHONENUMBER);
        time_val_Tv.setText(mView_HealthStatus.timeofcallinms);
        host_Tv_Val.setText(mView_HealthStatus.SPEED);
        packetLoss_Tv_val.setText(mView_HealthStatus.inComingOutgoing);
        AutoCompleteTextView testEt=view.findViewById(R.id.testEt);
//        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
//        db_handler.open();
//        //System.out.println(helper.Config.getDateTime()+" : via readtable  Mutex  status is "+uploadinit);
//        ArrayList<HashMap<String, String>> ipList= (ArrayList<HashMap<String, String>>) db_handler.selectIPPingdetails();
//        ArrayList<String> list_count = new ArrayList<>();
//        if(ipList!=null)
//        {
//            System.out.println(" iplist is"+ipList);
//            for(int i=0;i<ipList.size();i++)
//            {
//                list_count.add(ipList.get(i).get("ip"));
//            }
//            System.out.println(" list_count for is is"+ list_count);
//            testEt.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,list_count));
//        }
//        Button startBtn=view.findViewById(R.id.startBtn);
//        ProgressBar progressBar=view.findViewById(R.id.progressBar);
//        // progressBar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
//
//        if(Utils.checkifavailable(feature))
//        {
//            if(feature.equalsIgnoreCase("ping"))
//            {
//                pingLL.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                pingLL.setVisibility(View.GONE);
//            }
//        }
//        String strength = "";
//

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }







}
