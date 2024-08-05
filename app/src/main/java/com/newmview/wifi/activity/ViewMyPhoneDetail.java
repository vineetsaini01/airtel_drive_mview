package com.newmview.wifi.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sharad Gupta on 10/3/2016.
 */
public class ViewMyPhoneDetail extends AppCompatActivity {

    TextView txt_operator, txt_mccval,txt_mncval,txt_lacval,txt_typeval ;
    TextView  txt_rncval,txt_cellidval,txt_pscval;
    TextView  txt_rscpval,txt_echoval,txt_snrval ;

    TextView txt_lac;

    //labels
    TextView txt_rnc,txt_rscp,txt_echo,txt_psc;

    TextView  txt_lonval,txt_latval ;
    TextView  txt_speedval,txt_gpsaccuracyval ;
    TextView  txt_heightval,txt_altitudeval, txt_groundval ;

    TextView txt_ulval, txt_dlval;
    TextView txt_dataval, txt_idleval;

    TextView txt_serving;

    TableLayout tbl_layout;

    boolean dontRefreshScreen;
    Handler mHandler;
    int refreshFrequency = 1000; //1 second

    public long lastTxBytes, lastRxBytes;

    int cellLocationArraySizeShown = 0;

    RelativeLayout r4;
    TextView txt_enbval, txt_arfcnval, txt_taval, txt_pcival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phonemonitor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //group1
        txt_operator = (TextView) findViewById(R.id.txt_operator);

        txt_mccval = (TextView) findViewById(R.id.txt_mccval);
        txt_mncval = (TextView) findViewById(R.id.txt_mncval);
        txt_lac = (TextView) findViewById(R.id.txt_lac);
        txt_lacval = (TextView) findViewById(R.id.txt_lacval);
        txt_typeval = (TextView) findViewById(R.id.txt_typeval);

        txt_rnc = (TextView) findViewById(R.id.txt_rnc);
        txt_rncval = (TextView) findViewById(R.id.txt_rncval);
        txt_cellidval = (TextView) findViewById(R.id.txt_cellidval);


        //labels
        txt_echo = (TextView) findViewById(R.id.txt_echo);
       // txt_rnc = (TextView) findViewById(R.id.txt_rnc);
        txt_rscp = (TextView) findViewById(R.id.txt_rscp);
        txt_psc = (TextView) findViewById(R.id.txt_psc);

        txt_pscval =(TextView) findViewById(R.id.txt_pscval);

        txt_rscpval = (TextView) findViewById(R.id.txt_rscpval);
        txt_echoval = (TextView) findViewById(R.id.txt_echoval);
        txt_snrval = (TextView) findViewById(R.id.txt_snrval);

        txt_lonval = (TextView) findViewById(R.id.txt_lonval);
        txt_latval = (TextView) findViewById(R.id.txt_latval);

        txt_speedval = (TextView) findViewById(R.id.txt_speedval);
        txt_gpsaccuracyval = (TextView) findViewById(R.id.txt_gpsaccuracyval);

        txt_heightval = (TextView) findViewById(R.id.txt_heightval);
        txt_altitudeval = (TextView) findViewById(R.id.txt_altitudeval);
        txt_groundval = (TextView) findViewById(R.id.txt_groundval);

        txt_ulval = (TextView) findViewById(R.id.txt_ulval);
        txt_dlval = (TextView) findViewById(R.id.txt_dlval);

        txt_dataval = (TextView) findViewById(R.id.txt_dataval);
        txt_idleval = (TextView) findViewById(R.id.txt_idleval);

        txt_serving = (TextView) findViewById(R.id.txt_serving);

        txt_operator.setText("Operator : " + mView_HealthStatus.OperatorName);

        tbl_layout = (TableLayout) findViewById(R.id.tbl_layout);

        r4 = (RelativeLayout)findViewById(R.id.r4);

        txt_enbval = (TextView) findViewById(R.id.txt_enbval);
        txt_arfcnval = (TextView) findViewById(R.id.txt_arfcnval);
        txt_taval = (TextView) findViewById(R.id.txt_taval);
        txt_pcival = (TextView) findViewById(R.id.txt_pcival);

        initTable();
        useHandlerForScreenRefresh();
    }

    public void initTable()
    {
        if(mView_HealthStatus.timeSeriesServingCellDataArray == null)
            return;

        cellLocationArraySizeShown = mView_HealthStatus.timeSeriesServingCellDataArray.size();
        for (int i = 0; i < cellLocationArraySizeShown; i++) {
            LayoutInflater inflater = getLayoutInflater();
            TableRow tr = (TableRow)inflater.inflate(R.layout.table_row_phonemonitor, tbl_layout, false);


            tbl_layout.addView(tr);
            TextView label1 = (TextView) tr.getChildAt(1);
            TextView label2 = (TextView) tr.getChildAt(3);
            TextView label3 = (TextView) tr.getChildAt(5);
            TextView label4 = (TextView) tr.getChildAt(7);
            TextView label5 = (TextView) tr.getChildAt(9);
            TextView label6 = (TextView) tr.getChildAt(11);
            TextView label7 = (TextView) tr.getChildAt(13);
            TextView label8 = (TextView) tr.getChildAt(15);
            TextView label9 = (TextView) tr.getChildAt(17);
            TextView label10 = (TextView) tr.getChildAt(19);

            //ShapeDrawable border = new ShapeDrawable(new RectShape());
           // border.getPaint().setStyle(Paint.Style.STROKE);
           // border.getPaint().setColor(Color.BLACK);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date resultdate = new Date(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).captureTime);
            String displaydate = sdf.format(resultdate);
            label1.setText(displaydate);
            //label1.setBackground(border);

            label2.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).LAC);
           // label2.setBackground(border);

            //label3.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).node);
           // label3.setBackground(border);

            byte[] l_byte_array = new byte[4];
            l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).cellId));
            int l_RNC_ID   = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.RNCID_C);
            int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.CID_C);

            label3.setText(l_RNC_ID + ""); //mView_HealthStatus.timeSeriesServingCellDataArray.get(i).node);

            label4.setText(l_real_CID + ""); //mView_HealthStatus.timeSeriesServingCellDataArray.get(i).cellId);
           // label4.setBackground(border);

            label5.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).ci);
           // label5.setBackground(border);

            label6.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).arfcn);
           // label6.setBackground(border);

            label7.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).level);
            //label7.setBackground(border);

            label8.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).qual);
           // label8.setBackground(border);

            label9.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).networkType + "");
           // label9.setBackground(border);

            label10.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).serveTime + "");
           // label10.setBackground(border);
        }
    }//end initTable

    public void useHandlerForScreenRefresh() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, refreshFrequency);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(dontRefreshScreen == false)
            {
                updateUI();
                mHandler.postDelayed(mRunnable, refreshFrequency);
            }
        }
    };




    public void updateUI()
    {
        if(mView_HealthStatus.timeSeriesServingCellDataArray != null && cellLocationArraySizeShown < mView_HealthStatus.timeSeriesServingCellDataArray.size())
        {
            //add table rows
            int startCnt = cellLocationArraySizeShown;
            cellLocationArraySizeShown = mView_HealthStatus.timeSeriesServingCellDataArray.size();
            for( int i=startCnt; i <mView_HealthStatus.timeSeriesServingCellDataArray.size(); i++ )
            {
                //add extra rows
            }
        }
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();

        if (TextUtils.isEmpty(networkOperator) == false) {
            mView_HealthStatus.mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mView_HealthStatus.mnc = Integer.parseInt(networkOperator.substring(3));
            txt_mccval.setText(mView_HealthStatus.mcc + "");
            txt_mncval.setText(mView_HealthStatus.mnc + "");
        }

        txt_lacval.setText(mView_HealthStatus.Lac + "");
        txt_cellidval.setText(mView_HealthStatus.Cid + "");
        txt_pscval.setText(mView_HealthStatus.Psc + "");

        byte[] l_byte_array = new byte[4];
        l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
        int l_RNC_ID   = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.RNCID_C);
        int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.CID_C);

        txt_cellidval.setText(l_real_CID + "");
        txt_rncval.setText(l_RNC_ID + "");

        if(mView_HealthStatus.strCurrentNetworkProtocol != null)
            txt_typeval.setText(mView_HealthStatus.strCurrentNetworkProtocol);

        int io = Integer.MAX_VALUE;

        int rxl = MyPhoneStateListener.getRxLev();
        txt_snrval.setText(MyPhoneStateListener.getSNR());
        if(mView_HealthStatus.iCurrentNetworkState == 4 )
        {
            txt_echo.setText("RSSI");
            txt_echoval.setText(MyPhoneStateListener.getLTERSSI());
            //txt_echoval.setText(mView_HealthStatus.lteRSSI);
            //LTE

            txt_snrval.setText(mView_HealthStatus.lteSNR);


            txt_rnc.setText("RSRP");
            if( Integer.parseInt(mView_HealthStatus.lteRSRP) == Integer.MAX_VALUE)
            {
                txt_rncval.setText(" - ");
            }else
                txt_rncval.setText(mView_HealthStatus.lteRSRP);

            txt_rscp.setText("RSRQ");
            if( Integer.parseInt(mView_HealthStatus.lteRSRQ) == Integer.MAX_VALUE)
            {
                txt_rscpval.setText(" - ");
            }else
                txt_rscpval.setText(mView_HealthStatus.lteRSRQ);

            txt_psc.setText("CQI");
            txt_pscval.setText(MyPhoneStateListener.getCQI());


            //if( Integer.MAX_VALUE != Integer.parseInt(mView_HealthStatus.lteCQI))
            //    txt_pscval.setText(mView_HealthStatus.lteCQI);

            txt_lac.setText("TAC");
            if( Integer.parseInt(mView_HealthStatus.lteTAC) == Integer.MAX_VALUE)
            {
                txt_lacval.setText(" - ");
            }else
                txt_lacval.setText(mView_HealthStatus.lteTAC);

            txt_pcival.setText(mView_HealthStatus.ltePCI);
            txt_arfcnval.setText(mView_HealthStatus.lteArfcn);

            if( Integer.parseInt(mView_HealthStatus.lteta) == Integer.MAX_VALUE)
            {
                txt_taval.setText(" - ");
            }else
                txt_taval.setText(mView_HealthStatus.lteta);

            txt_enbval.setText(mView_HealthStatus.lteENB);

            r4.setVisibility(View.VISIBLE);

        }else {
            txt_rnc.setText("RNC");
            txt_rscp.setText("RSCP");
            txt_rscpval.setText(rxl + "");

            txt_echo.setText("ECHO");

            txt_psc.setText("PSC");
            txt_lac.setText("LAC");

            r4.setVisibility(View.GONE);
        }

        if(listenService.gps != null && listenService.gps.canGetLocation())
        {
            txt_lonval.setText(String.format("%.6f",listenService.gps.getLongitude()) + "");
            txt_latval.setText(String.format("%.6f",listenService.gps.getLatitude()) + "");
            txt_altitudeval.setText(String.format("%.2f", listenService.gps.getAltitude()) + "m");
            double speed = 3.6*listenService.gps.getSpeed();

            txt_speedval.setText( String.format("%.1f", speed) + "kmph");
            if( listenService.gps.getLocation() != null)
                txt_gpsaccuracyval.setText(listenService.gps.getLocation().getAccuracy()+"m");
        }

        txt_dataval.setText(chkDataConnectivity());

        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        if(lastTxBytes == 0)
            lastTxBytes = totalTxBytes;
        if(lastRxBytes == 0 )
            lastRxBytes = totalRxBytes;
        
        long ulspeed = (totalTxBytes - lastTxBytes)/8;
        long dlspeed = (totalRxBytes - lastRxBytes)/8;
        lastTxBytes = totalTxBytes;
        lastRxBytes = totalRxBytes;

        txt_ulval.setText(ulspeed +" kbps");
        txt_dlval.setText(dlspeed + " kbps");

        if(mView_HealthStatus.timeSeriesServingCellDataArray != null && mView_HealthStatus.timeSeriesServingCellDataArray.size() > 0 ) {
            long servetime = System.currentTimeMillis() - mView_HealthStatus.timeSeriesServingCellDataArray.get(mView_HealthStatus.timeSeriesServingCellDataArray.size() - 1).captureTime;
            servetime = servetime / 1000;
            txt_serving.setText("Serving since :" + servetime + " sec");
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            dontRefreshScreen = true;
//            try {
//                mRunnable.wait();
//            }catch(InterruptedException e)
//            {
//
//            }
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        dontRefreshScreen = true;
        Log.e("mView MainAct MyPhone", "onPause ");
        // Release the Camera because we don't need it when paused
        // and other activities might need to use it.
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        dontRefreshScreen = false;
        useHandlerForScreenRefresh();
        Log.e("mView MainAct MyPhone", "onResume");
        // Get the Camera instance as the activity achieves full user focus

    }
    String chkDataConnectivity() {
        String s = "";
        final ConnectivityManager connMgr = (ConnectivityManager)
               getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            s = "WiFi";
            WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            int linkSpeed = wifiInfo.getLinkSpeed();
            String ss = wifiInfo.getSSID();
            int rs = wifiInfo.getRssi();
            s = "WiFi("+ ss + ", " + rs +"dBm)\n";
            mView_HealthStatus.connectionType = "WiFi";
            mView_HealthStatus.connectionTypeIdentifier = 1;
        } else if (mobile.isConnectedOrConnecting ()) {

            s = "Mobile data"; //getNetworkTypeString (listenService.telMgr.getNetworkType());
            s += "\n";
            mView_HealthStatus.connectionType = "Mobile data";
            mView_HealthStatus.connectionTypeIdentifier = 2;
        } else {
            s = "Offline";
            mView_HealthStatus.connectionTypeIdentifier = 0;
            //Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return s;
    }

    public void sendMessageToActivity(int msg)
    {
        Message m = new Message();
        m.what = msg;
        updateHandler.sendMessage(m);
    }

    public Handler updateHandler = new Handler(){

        // @Override
        public void handleMessage(Message msg) {

            int event = msg.what;
            switch(event){
                case 1:
                    //ACM.myWebView.loadUrl("file:///android_asset/" + urlToLoad);
                    dontRefreshScreen = true;
                    initTable();
                    dontRefreshScreen = false;
                    break;
            }//end of switch
        }
    }; //end of updateHandler
}
