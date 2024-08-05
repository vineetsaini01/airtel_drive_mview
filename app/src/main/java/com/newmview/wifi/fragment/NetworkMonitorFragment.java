package com.newmview.wifi.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.newmview.wifi.CommonFunctions;
import com.newmview.wifi.MyPhoneStateListener;
import com.mview.airtel.R;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.listenService;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.newmview.wifi.network.NetworkUtil.getGSMNetworkParams;
import static com.newmview.wifi.network.NetworkUtil.getLTENetworkParams;
import static com.newmview.wifi.network.NetworkUtil.getLTEParams;
import static com.newmview.wifi.network.NetworkUtil.getThreeGNetworkParams;


public class NetworkMonitorFragment extends Fragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private static final String TAG ="NetworkMonitorFragment" ;


    TextView txt_operator, txt_mccval,txt_mncval,txt_lacval,txt_typeval ;
    TextView  txt_rncval,txt_cellidval,txt_pscval,txt_cellid;
    TextView  txt_rssival,txt_echoval,txt_snrval ;

    TextView txt_snr;
    TextView txt_lac;

    //labels
    TextView txt_rnc,txt_rssi,txt_psc;
    //RSSI is rxd signal strength indication

    TextView  txt_lonval,txt_latval ;
    TextView  txt_speedval,txt_gpsaccuracyval ;
    TextView  txt_heightval,txt_altitudeval, txt_groundval ;

    TextView txt_ulval, txt_dlval;
    TextView txt_dataval, txt_idleval,txt_datamode;

    TextView txt_serving;

    //TableLayout tbl_layout;
    boolean dontRefreshScreen;
    Handler mHandler;
    int refreshFrequency = 5000; //1 second
    public long lastTxBytes, lastRxBytes;
    public long lastTimestamp;
    int cellLocationArraySizeShown = 0;
    RelativeLayout r4;
    TextView txt_rsrpval, txt_arfcnval, txt_taval, txt_cqival,txt_cqi;
    TextView txt_rsrp,level,qual,ta;
    //LinearLayout l4_21, l4_22, l4_31, l4_41, l2;
    TableRow l4_21, l4_22, l4_31,l2;
    TableLayout l4_41;
    TextView timeval,lacval,nodeval,cellidval,cival,arfcnval,levelval,qualval,typeval,servcval;
    TextView uarfcn,rscp,ec,psc,type,node_val,uarfcn_val,rscp_val,ec_val,psc_val,type_val;
    TableLayout layoutFor4g;
    //TextView txt_pci;
    TextView txt_arfcn;
    private Menu menu;
    private View v;
    private TextView arfcn,node,cellid,lac,ci;
    private TableLayout threegparams;
    private TextView txt_ta;
    //TableLayout tableLayout;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    public static final int REQUEST_CHECK_SETTINGS = 214;
     public static final int REQUEST_ENABLE_GPS = 516;
    private long totalulspeed;
    private long ulspeed=0;
    private long dlspeed=0;
    private String mode="NA";

    public NetworkMonitorFragment() {
        // Required empty public constructor
    }

    Activity parentActivity;
    LayoutInflater inflater;
   /* ArrayList<String> monitor_params=new ArrayList<>();
    String[] monitoring_params={"MCC","MNC","LAC","TYPE","BSC","CELLID","BSIC","ARFCN","TA","CQI","RXLEV","RXQUAL","Latitude","Longitude","Speed","GPS Accuracy",
            "Height","Altitude","Ground",}*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        //openLocationAlertDialog();

    }







    private void openLocationAlertDialog() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));
        //builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
if(getActivity()!=null) {
    mSettingsClient = LocationServices.getSettingsClient(getActivity());

    mSettingsClient
            .checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    //Success Perform Task Here
                    //Utils.showToast(getActivity(), "successss");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    //if the location service is off
                    //Utils.showToast(getActivity(), "failed,.." +statusCode);
                    switch (statusCode) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {

                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.e("GPS", "Unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.e("GPS", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                    }
                }
            })
            .addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Log.e("GPS", "checkLocationSettings -> onCanceled");
                   // Utils.showToast(getActivity(), "cancelled");
                }
            });
}


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.ntwrk_monitor_new, container, false);
        View view1 = inflater.inflate(R.layout.padding, null);
        parentActivity = getActivity();

        //group1
        init();
        initTable();
        Log.i(TAG,"Updating ui called from 1");
     //   useHandlerForScreenRefresh();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendRequest();
            }
        },3000);

        return v;
    }

    private void sendRequest() {
        try {


            JSONArray jsonArray = new JSONArray();

            if (MyPhoneStateListener.getNetworkType() == 4) {
                jsonArray.put(getLTENetworkParams());
                // jsonArray.put(getThreeGParams());
                //jsonArray.put(getGSMParams());
            } else if (MyPhoneStateListener.getNetworkType() == 3) {

                jsonArray.put(getThreeGNetworkParams());

            } else if (MyPhoneStateListener.getNetworkType() == 2) {
                jsonArray.put(getGSMNetworkParams());
            } else {
                jsonArray.put(getLTEParams());

            }
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("MCC",mView_HealthStatus.prim_mcc+"");
            jsonObject.put("MNC",mView_HealthStatus.prim_mnc+"");
            jsonObject.put("type",mView_HealthStatus.strCurrentNetworkProtocol+"");
            if (listenService.gps.getLocation() != null)
            {
                jsonObject.put("gps_accuracy",listenService.gps.getLocation().getAccuracy()+"m");
                jsonObject.put("altitude",String.format("%.2f", listenService.gps.getAltitude()) + "m");
            }
            else
            {
                jsonObject.put("gps_accuracy","NA");
                jsonObject.put("altitude","NA");
            }
            jsonObject.put("uplink_speed_in_kbps",ulspeed);
            jsonObject.put("downlink_speed_in_kbps",dlspeed);
            jsonObject.put("data",txt_dataval.getText()+"");
            jsonObject.put("mode",mode);


            jsonObject.put("Network_params",jsonArray);
            JSONArray jsonArray1=new JSONArray();
            jsonArray1.put(jsonObject);
            RequestResponse.sendEvent(jsonArray1, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.NETWORK_MONITOR_INFO_EVT,"network_monitor");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }




    }

    private void init() {
        layoutFor4g=(TableLayout)v.findViewById(R.id.layoutFor4gParams);
        txt_operator = (TextView) v.findViewById(R.id.txt_operator);
        txt_mccval = (TextView) v.findViewById(R.id.txt_mccval);
        txt_mncval = (TextView) v.findViewById(R.id.txt_mncval);
        txt_lac = (TextView) v.findViewById(R.id.txt_lac);
        txt_lacval = (TextView) v.findViewById(R.id.txt_lacval);
        txt_typeval = (TextView) v.findViewById(R.id.txt_typeval);
        txt_rnc = (TextView) v.findViewById(R.id.txt_rnc);
        txt_rncval = (TextView) v.findViewById(R.id.txt_rncval);
        txt_cellid=(TextView)v.findViewById(R.id.txt_cellid);
        txt_cellidval = (TextView) v.findViewById(R.id.txt_cellidval);
        //labels
        //txt_echo = (TextView) v.findViewById(R.id.txt_echo);
        // txt_rnc = (TextView) findViewById(R.id.txt_rnc);
        txt_rssi = (TextView) v.findViewById(R.id.txt_rssi);
        txt_psc = (TextView) v.findViewById(R.id.txt_psc);
        txt_pscval =(TextView) v.findViewById(R.id.txt_pscval);
        txt_rssival = (TextView) v.findViewById(R.id.txt_rssival);
      //  txt_echoval = (TextView) v.findViewById(R.id.txt_echoval);
        txt_snrval = (TextView) v.findViewById(R.id.txt_snrval);
        txt_snr =  (TextView) v.findViewById(R.id.txt_snr);
        txt_lonval = (TextView) v.findViewById(R.id.txt_lonval);
        txt_latval = (TextView) v.findViewById(R.id.txt_latval);
        txt_speedval = (TextView) v.findViewById(R.id.txt_speedval);
        txt_gpsaccuracyval = (TextView) v.findViewById(R.id.txt_gpsaccuracyval);
        txt_heightval = (TextView) v.findViewById(R.id.txt_heightval);
        txt_altitudeval = (TextView) v.findViewById(R.id.txt_altitudeval);
        txt_groundval = (TextView) v.findViewById(R.id.txt_groundval);
        txt_ulval = (TextView) v.findViewById(R.id.txt_ulval);
        txt_dlval = (TextView) v.findViewById(R.id.txt_dlval);
        txt_datamode=(TextView)v.findViewById(R.id.txt_data);
        txt_dataval = (TextView) v.findViewById(R.id.txt_dataval);
        txt_idleval = (TextView) v.findViewById(R.id.txt_idleval);
        txt_serving = (TextView) v.findViewById(R.id.txt_serving);
        txt_operator.setText("DATA Operator : " + mView_HealthStatus.OperatorName);
        //tbl_layout = (TableLayout) v.findViewById(R.id.tbl_layout);
        r4 = (RelativeLayout)v.findViewById(R.id.r4);
        l4_21 = v.findViewById(R.id.l4_21);
        l4_22 = v.findViewById(R.id.l4_22);
        //l4_31 = (TableRow)v.findViewById(R.id.l4_31);
        l4_41 = (TableLayout) v.findViewById(R.id.l4_41);
        l2 = v.findViewById(R.id.l2);
        txt_rsrp = (TextView) v.findViewById(R.id.txt_rsrp);
        txt_rsrpval = (TextView) v.findViewById(R.id.txt_rsrpval);
        txt_arfcnval = (TextView) v.findViewById(R.id.txt_arfcnval);
        txt_ta=(TextView)v.findViewById(R.id.txt_ta);
        txt_taval = (TextView) v.findViewById(R.id.txt_taval);
        txt_cqi=(TextView)v.findViewById(R.id.txt_cqi);
        txt_cqival = (TextView) v.findViewById(R.id.txt_cqival);

        //txt_pci = (TextView) v.findViewById(R.id.txt_pci);
        txt_arfcn = (TextView) v.findViewById(R.id.txt_arfcn);
        //tableLayout=(TableLayout)v.findViewById(R.id.tableLayout);
        timeval=(TextView) v.findViewById(R.id.textView1val);
        lac=(TextView)v.findViewById(R.id.textView2);
        lacval=(TextView)v.findViewById(R.id.textView2val);
        node=(TextView)v.findViewById(R.id.textView3);
        nodeval=(TextView)v.findViewById(R.id.textView3val);
        cellid=(TextView)v.findViewById(R.id.textView4);
        cellidval=(TextView)v.findViewById(R.id.textView4val);
        ci=(TextView)v.findViewById(R.id.textView5);
        cival=(TextView)v.findViewById(R.id.textView5val);
        arfcn=(TextView)v.findViewById(R.id.textView6);
        arfcnval=(TextView)v.findViewById(R.id.textView6val);
        level=(TextView)v.findViewById(R.id.textView7);
        levelval=(TextView)v.findViewById(R.id.textView7val);
        qual=(TextView)v.findViewById(R.id.textView8);
        qualval=(TextView)v.findViewById(R.id.textView8val);
        typeval=(TextView)v.findViewById(R.id.textView9val);
        servcval=(TextView)v.findViewById(R.id.textView10val);
        psc=(TextView)v.findViewById(R.id.textView15);
        psc_val=(TextView)v.findViewById(R.id.textView15val);
        threegparams=(TableLayout)v.findViewById(R.id.tableLayout6);
        uarfcn=(TextView)v.findViewById(R.id.textView12);
        uarfcn_val=(TextView)v.findViewById(R.id.textView12val);
        psc=(TextView)v.findViewById(R.id.textView15);
        psc_val=(TextView)v.findViewById(R.id.textView15val);
        rscp=(TextView)v.findViewById(R.id.textView13);
        rscp_val=(TextView)v.findViewById(R.id.textView13val);
        node_val=(TextView)v.findViewById(R.id.textView11val);
        ec_val=(TextView)v.findViewById(R.id.textView14val);


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.terrain_view);
        item.setVisible(false);
    }

    public void initTable() {
        if(mView_HealthStatus.timeSeriesServingCellDataArray == null)
            return;

        cellLocationArraySizeShown = mView_HealthStatus.timeSeriesServingCellDataArray.size();
       for (int i = 0; i < cellLocationArraySizeShown; i++) {


           lacval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).LAC);
           byte[] l_byte_array = new byte[4];
            l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).cellId));
            int l_RNC_ID   = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.RNCID_C);
            int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array,CommonFunctions.CID_C);

            nodeval.setText(l_RNC_ID + "");

            if( mView_HealthStatus.timeSeriesServingCellDataArray.get(i).networkType.equals("4G")) {
                l_real_CID = Integer.parseInt(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).cellId) & 0xff;
                nodeval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).lteENB + "");
                qualval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).lteRSRQ + "");

            }else {
                qualval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).lteRSRQ + "");
            }


            cellidval.setText(l_real_CID + "");
            System.out.println("arfcn values  "+mView_HealthStatus.timeSeriesServingCellDataArray.get(i).arfcn);
            arfcnval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).arfcn);
            levelval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).level);
            typeval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).networkType + "");
            servcval.setText(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).serveTime + "");

        }

    }//end initTable


    public void useHandlerForScreenRefresh() {
        mHandler = new Handler();
        mHandler.post(mRunnable);
    }

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            if(!dontRefreshScreen)
            {

                updateUI();
                mHandler.postDelayed(mRunnable, refreshFrequency);
            }
        }
    };

    public void updateUI() {
        Log.i(TAG,"Updating ui getting called at "+Utils.getDateTime());
        if (mView_HealthStatus.timeSeriesServingCellDataArray != null && cellLocationArraySizeShown < mView_HealthStatus.timeSeriesServingCellDataArray.size()) {
            initTable();

        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date resultdate = new Date(System.currentTimeMillis());
        //  Date resultdate = new Date(mView_HealthStatus.timeSeriesServingCellDataArray.get(i).captureTime);

        String displaydate = sdf.format(resultdate);
        timeval.setText(displaydate);
        TelephonyManager tel = (TelephonyManager) parentActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperator = tel.getNetworkOperator();
        System.out.println("fragment network operator "+networkOperator  +TextUtils.isEmpty(networkOperator));

        if (TextUtils.isEmpty(networkOperator) == false) {
            mView_HealthStatus.mcc = Integer.parseInt(networkOperator.substring(0, 3));
            mView_HealthStatus.mnc = Integer.parseInt(networkOperator.substring(3));
            txt_mccval.setText(mView_HealthStatus.prim_mcc+ "");
            txt_mncval.setText(mView_HealthStatus.prim_mnc+ "");
        }
        else
        {
         //  Utils.showToast(getActivity(),"network op"+networkOperator );
        }

if(mView_HealthStatus.Lac==null||Integer.parseInt(mView_HealthStatus.Lac) == Integer.MAX_VALUE) {
    txt_lacval.setText("0");
}
else
{
    txt_lacval.setText(mView_HealthStatus.Lac);
}

if(mView_HealthStatus.Cid==null||Integer.parseInt(mView_HealthStatus.Cid)==Integer.MAX_VALUE) {
    txt_cellidval.setText("0");
}
else
{
    txt_cellidval.setText(mView_HealthStatus.Cid + "");
}
       if(mView_HealthStatus.Psc==null||Integer.parseInt(mView_HealthStatus.Psc)==Integer.MAX_VALUE)
       {
           txt_pscval.setText("0");
       }
       else {
           txt_pscval.setText(mView_HealthStatus.Psc + "");
       }


            byte[] l_byte_array = new byte[4];
            if(mView_HealthStatus.Cid!=null)
            l_byte_array = CommonFunctions.convertByteArray__p(Integer.parseInt(mView_HealthStatus.Cid));
            int l_RNC_ID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.RNCID_C);
            int l_real_CID = CommonFunctions.getRNCID_or_CID__p(l_byte_array, CommonFunctions.CID_C);
            txt_cellidval.setText(l_real_CID + "");
            txt_rncval.setText(l_RNC_ID + "");



        if (mView_HealthStatus.strCurrentNetworkProtocol != null)
            txt_typeval.setText(mView_HealthStatus.strCurrentNetworkProtocol);


        int io = Integer.MAX_VALUE;

        int rxl = MyPhoneStateListener.getRxLev();
      //  txt_snrval.setText(MyPhoneStateListener.getSNR());
       // BY SWAPNIL BANSAL 07/03/2023
        txt_cqival.setText(mView_HealthStatus.lteCQI);
      //  Toast.makeText(getActivity(), " current instance "+mView_HealthStatus.currentInstance, Toast.LENGTH_SHORT).show();

        if (mView_HealthStatus.currentInstance.equalsIgnoreCase("lte")) {

            txt_ta.setVisibility(View.VISIBLE);
            txt_taval.setVisibility(View.VISIBLE);
            txt_cqival.setVisibility(View.VISIBLE);
            txt_cqi.setVisibility(View.VISIBLE);
            txt_rssi.setVisibility(View.VISIBLE);
            txt_rssival.setVisibility(View.VISIBLE);
            txt_psc.setVisibility(View.VISIBLE);
            txt_pscval.setVisibility(View.VISIBLE);

//            txt_cellidval.setText(mView_HealthStatus.Cid);

            txt_arfcn.setText("EARFCN");
            if(mView_HealthStatus.Cid!=null) {
                int cid1 = Integer.parseInt(mView_HealthStatus.Cid) & 0xff;
                txt_cellidval.setText(cid1 + "");
            }
            else
            {
                txt_cellid.setText("0");
            }



//            txt_cellidval.setText(mView_HealthStatus.lteArfcn);

            txt_rssi.setText("RSSI");
            txt_rssival.setText(MyPhoneStateListener.getLTERSSI());
            //txt_echoval.setText(mView_HealthStatus.lteRSSI);
            //LTE
if(mView_HealthStatus.lteSNR!=null) {
    txt_snrval.setText(mView_HealthStatus.lteSNR);
}
else
{
    txt_snrval.setText("NA");
}
            txt_snr.setText("SINR");

            ///////////MATCHING/////////////
            txt_rnc.setText("eNB");
            if (mView_HealthStatus.lteENB == null || Integer.parseInt(mView_HealthStatus.lteENB) == Integer.MAX_VALUE) {
                txt_rncval.setText(" 0 ");
            } else
                txt_rncval.setText(mView_HealthStatus.lteENB);
            ///////////MATCHING/////////////

            txt_rsrp.setText("RSRP");
            if(Utils.checkIfNumeric(mView_HealthStatus.lteRSRP)) {
                if (mView_HealthStatus.lteRSRP == null || Integer.parseInt(mView_HealthStatus.lteRSRP) == Integer.MAX_VALUE) {
                    txt_rsrpval.setText(" 0 ");
                } else
                    txt_rsrpval.setText(mView_HealthStatus.lteRSRP);
            }
            else
            {
                txt_rsrpval.setText(" 0 ");
            }

      //      txt_echo.setText("RSRQ");
           /* if (mView_HealthStatus.lteRSRQ == null || Integer.parseInt(mView_HealthStatus.lteRSRQ) == Integer.MAX_VALUE) {
                txt_echoval.setText(" - ");
            } else
                txt_echoval.setText(mView_HealthStatus.lteRSRQ);*/

            //txt_psc.setText("CQI");




            //if( Integer.MAX_VALUE != Integer.parseInt(mView_HealthStatus.lteCQI))
            //    txt_pscval.setText(mView_HealthStatus.lteCQI);

            txt_lac.setText("TAC");
            if (mView_HealthStatus.lteTAC == null || Integer.parseInt(mView_HealthStatus.lteTAC) == Integer.MAX_VALUE) {
                txt_lacval.setText(" 0 ");
            } else
                txt_lacval.setText(mView_HealthStatus.lteTAC);

            //txt_pcival.setText(mView_HealthStatus.ltePCI);
            if((mView_HealthStatus.lteArfcn==null) ||  Integer.parseInt(mView_HealthStatus.lteArfcn) == Integer.MAX_VALUE)
            {
                txt_arfcnval.setText("0");
            }
            else
                txt_arfcnval.setText(mView_HealthStatus.lteArfcn);

            if (mView_HealthStatus.lteta == null || Integer.parseInt(mView_HealthStatus.lteta) == Integer.MAX_VALUE) {
                txt_taval.setText(" 0 ");
            } else
                txt_taval.setText(mView_HealthStatus.lteta);

            txt_psc.setText("PCI");
            if (mView_HealthStatus.ltePCI == null ||  Integer.parseInt(mView_HealthStatus.ltePCI) == Integer.MAX_VALUE) {
                txt_pscval.setText("0");
            } else
                txt_pscval.setText(mView_HealthStatus.ltePCI);





            //txt_enbval.setText(mView_HealthStatus.lteENB);

            //r4.setVisibility(View.VISIBLE);
          /*  if (!mView_HealthStatus.lteCQI.equalsIgnoreCase("0")) {
                System.out.println("mView cqi  " +mView_HealthStatus.lteCQI);
                int cqi = Integer.parseInt(mView_HealthStatus.lteCQI) & 0xff;
                System.out.println("mView cqi afterwards... " +cqi);
                txt_cqival.setText(String.valueOf(cqi));
            }*/


          //  hideAndShowParamsFor4G();



        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("wcdma")) {
           // layoutFor4g.setVisibility(View.GONE);
            txt_rnc.setText("NodeB Id");
            if((mView_HealthStatus.nodeb_id==null)||(Integer.parseInt(mView_HealthStatus.nodeb_id)==Integer.MAX_VALUE))
            {
                txt_rncval.setText("0");
            }
            else
            {
                txt_rncval.setText(mView_HealthStatus.nodeb_id);
            }
            txt_rssi.setVisibility(View.VISIBLE);
            txt_rssival.setVisibility(View.VISIBLE);
            txt_ta.setVisibility(View.GONE);
            txt_cqival.setVisibility(View.VISIBLE);
            txt_cqi.setVisibility(View.VISIBLE);
            txt_psc.setVisibility(View.VISIBLE);
            txt_pscval.setVisibility(View.VISIBLE);




            txt_rsrp.setText("RSCP");
            if(mView_HealthStatus.rscp!=null)
                txt_rsrpval.setText(mView_HealthStatus.rscp);
            else
                txt_rsrpval.setText("0");

            txt_arfcn.setText("Uarfcn");
            if (mView_HealthStatus.Uarfcn == null || Integer.parseInt(mView_HealthStatus.Uarfcn) == Integer.MAX_VALUE) {

                txt_arfcnval.setText(" 0 ");
            } else
                txt_arfcnval.setText(mView_HealthStatus.Uarfcn);


            txt_rssi.setText("RSSI");
            txt_rssival.setText(String.valueOf(rxl) );

            txt_ta.setVisibility(View.GONE);
            txt_taval.setVisibility(View.GONE);

            txt_psc.setText("PSC");
            if(mView_HealthStatus.Wcdma_Psc!=null) {
                txt_pscval.setText(mView_HealthStatus.Wcdma_Psc);
            }
            else
            {
                txt_pscval.setText("0");
            }
           /* if (mView_HealthStatus.lteta == null || Integer.parseInt(mView_HealthStatus.lteta) == Integer.MAX_VALUE) {
                txt_taval.setText(" - ");
            } else
                txt_taval.setText(mView_HealthStatus.lteta);*/
            txt_lac.setText("LAC");
            txt_snr.setText("Ec/no");
            if(mView_HealthStatus.rscp!=null) {
                int rscp = Integer.valueOf(mView_HealthStatus.rscp);

                int ecno = rscp - rxl;
                txt_snrval.setText(String.valueOf(ecno));
            }
            else
            {
                txt_snrval.setText("0");
            }



          //  txt_snrval.setText(String.valueOf(mView_HealthStatus.EVDOSNR));
           /* if (mView_HealthStatus.lteRSRQ == null || Integer.parseInt(mView_HealthStatus.lteRSRQ) == Integer.MAX_VALUE) {
                txt_snrval.setTem xt(" - ");
            } else
                txt_snrval.setText(mView_HealthStatus.lteRSRQ);*/





            //txt_rsrpval.setText(rxl + "");

           /* txt_echo.setText("Ec/No");
            System.out.println("ec/no on top "+mView_HealthStatus.lteRSRQ);
            if (mView_HealthStatus.lteRSRQ == null || Integer.parseInt(mView_HealthStatus.lteRSRQ) == Integer.MAX_VALUE) {
                txt_echoval.setText(" - ");
            } else
                txt_echoval.setText(mView_HealthStatus.lteRSRQ);*/



         //   l4_31.setVisibility(View.VISIBLE);
            l4_41.setVisibility(View.VISIBLE);



            System.out.println("uarfcn in 3g "+mView_HealthStatus.Uarfcn);

           // uarfcn_val.setText(mView_HealthStatus.Uarfcn);


            System.out.println("rscp in 3g "+mView_HealthStatus.lteRSRP);
            if (mView_HealthStatus.lteRSRP == null || Integer.parseInt(mView_HealthStatus.lteRSRP) == Integer.MAX_VALUE) {
                rscp_val.setText(" 0 ");
            } else
               rscp_val.setText(mView_HealthStatus.lteRSRP);


        } else if (mView_HealthStatus.currentInstance.equalsIgnoreCase("gsm")) {
            txt_cqival.setVisibility(View.GONE);
            txt_cqi.setVisibility(View.GONE);

            txt_rssi.setVisibility(View.GONE);
            txt_rssival.setVisibility(View.GONE);
            txt_ta.setVisibility(View.VISIBLE);
            txt_taval.setVisibility(View.VISIBLE);

            txt_ta.setText("TA");
            if (mView_HealthStatus.lteta == null || Integer.parseInt(mView_HealthStatus.lteta) == Integer.MAX_VALUE) {
             //   Toast.makeText(getActivity(), "ta value is "+mView_HealthStatus.lteta, Toast.LENGTH_SHORT).show();
                txt_taval.setText("NA");
            } else {
             //   Toast.makeText(getActivity(), "ta value is " + mView_HealthStatus.lteta, Toast.LENGTH_SHORT).show();
                txt_taval.setText(mView_HealthStatus.lteta);
            }
            txt_psc.setVisibility(View.GONE);
            txt_pscval.setVisibility(View.GONE);


            txt_arfcn.setText("ARFCN");
            if (mView_HealthStatus.ARFCN == null || Integer.parseInt(mView_HealthStatus.ARFCN) == Integer.MAX_VALUE) {

                txt_arfcnval.setText(" 0 ");
            } else
                txt_arfcnval.setText(mView_HealthStatus.ARFCN);

           /* if (mView_HealthStatus.lteArfcn == null || Integer.parseInt(mView_HealthStatus.lteArfcn) == Integer.MAX_VALUE) {

                txt_arfcnval.setText(" - ");
            } else
                txt_arfcnval.setText(mView_HealthStatus.lteArfcn);*/
            txt_rnc.setText("Site Id");

            txt_rsrp.setText("RXLEV");
            txt_rsrpval.setText(rxl + "");


            txt_snr.setText("RXQUAL");
        //    Toast.makeText(getActivity(), "value for qual "+mView_HealthStatus.rxqualfor2g, Toast.LENGTH_SHORT).show();
            System.out.println("value for qual "+mView_HealthStatus.rxqualfor2g);

            if (mView_HealthStatus.rxqualfor2g == null || Integer.parseInt(mView_HealthStatus.rxqualfor2g) == Integer.MAX_VALUE) {
                txt_snrval.setText(" 0 ");
            } else
                txt_snrval.setText(" " + mView_HealthStatus.rxqualfor2g);



            txt_lac.setText("LAC");



       //     l4_31.setVisibility(View.INVISIBLE);
           // l4_41.setVisibility(View.GONE);

            String dbm = mView_HealthStatus.ltedbm;
            String asulevel = mView_HealthStatus.lteasus;
            String level = mView_HealthStatus.ltelevel;

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txt_rsrp.getLayoutParams();
            params.weight = 1.2f;
            txt_rsrp.setLayoutParams(params);

            LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) txt_rsrpval.getLayoutParams();
            params.weight = .8f;
            txt_rsrpval.setLayoutParams(params);
            //showParams();


            //r4.setVisibility(View.GONE);
        } else {
            /*txt_rnc.setText("RNC");
            txt_rssi.setText("RSCP");
            txt_rssival.setText(rxl + "");

      //      txt_echo.setText("ECHO");

            txt_psc.setText("PSC");
            txt_lac.setText("LAC");

*/
            //r4.setVisibility(View.GONE);
        }

        if (listenService.gps != null && listenService.gps.canGetLocation()) {
            System.out.println("gsp val  "+listenService.gps +"boolean "+listenService.gps.canGetLocation());
            txt_lonval.setText(String.format("%.6f", listenService.gps.getLongitude()) + "");
            txt_latval.setText(String.format("%.6f", listenService.gps.getLatitude()) + "");
            txt_altitudeval.setText(String.format("%.2f", listenService.gps.getAltitude()) + "m");
            System.out.println("speed value"+listenService.gps.getSpeed());
            double speed = 3.6 * listenService.gps.getSpeed();


            txt_speedval.setText(String.format("%.1f", speed) + "kmph");
            if (listenService.gps.getLocation() != null)
                txt_gpsaccuracyval.setText(listenService.gps.getLocation().getAccuracy() + "m");
        }

        txt_dataval.setText(chkDataConnectivity());

        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        if (lastTxBytes == 0)
            lastTxBytes = totalTxBytes;
        if (lastRxBytes == 0)
            lastRxBytes = totalRxBytes;

        long curr = System.currentTimeMillis();
        long gap = curr - lastTimestamp;
        float gapInSecs = (float) gap / 1000.0f;
        // if(gapInSecs == 0)
        //    gapInSecs = 1;


        if (gapInSecs >= 1) {
            float kb =
                    ulspeed = (long) (((totalTxBytes - lastTxBytes) * 8) / (1024 * gapInSecs));
             totalulspeed=(long) ((totalTxBytes  * 8) / (1024 * gapInSecs));
            long lastul=(long) ((lastTxBytes * 8) / (1024 * gapInSecs));
            dlspeed = (long) (((totalRxBytes - lastRxBytes) * 8) / (1024 * gapInSecs));
            lastTxBytes = totalTxBytes;
            lastRxBytes = totalRxBytes;
            lastTimestamp = curr;

            txt_ulval.setText(ulspeed + " kbps");
            txt_dlval.setText(dlspeed + " kbps");
            if (ulspeed == 0 && dlspeed == 0) {
                mode="IDLE";
                txt_idleval.setText("IDLE");
            }
            else {
                mode="DATA";
                txt_idleval.setText("DATA");
            }

        }


        if (mView_HealthStatus.timeSeriesServingCellDataArray != null && mView_HealthStatus.timeSeriesServingCellDataArray.size() > 0) {
            long servetime = System.currentTimeMillis() - mView_HealthStatus.timeSeriesServingCellDataArray.get(mView_HealthStatus.timeSeriesServingCellDataArray.size() - 1).captureTime;
            servetime = servetime / 1000;
            txt_serving.setText("Serving since :" + servetime + " sec");
        }
    }

    private void hideAndShowParamsFor3G() {
        levelval.setVisibility(View.GONE);
        qualval.setVisibility(View.GONE);
        level.setVisibility(View.GONE);
        qual.setVisibility(View.GONE);
        ci.setVisibility(View.GONE);
        cival.setVisibility(View.GONE);
        txt_taval.setVisibility(View.GONE);
        ta.setVisibility(View.GONE);
        txt_arfcn.setVisibility(View.GONE);
        txt_arfcnval.setVisibility(View.GONE);
        l4_41.setVisibility(View.GONE);


    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(false);
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
        Log.i(TAG,"Updating ui called from 2");
        useHandlerForScreenRefresh();
        Log.e("mView MainAct MyPhone", "onResume");
        // Get the Camera instance as the activity achieves full user focus

    }

    String chkDataConnectivity() {
        String s = "";
        final ConnectivityManager connMgr = (ConnectivityManager)
                parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            s = "WiFi";
            WifiManager wm = (WifiManager)parentActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            int linkSpeed = wifiInfo.getLinkSpeed();
            String ss = wifiInfo.getSSID();
            int rs = wifiInfo.getRssi();
            s = "WiFi("+ ss + ", " + rs +"dBm)";
            mView_HealthStatus.connectionType = "WiFi";
            mView_HealthStatus.connectionTypeIdentifier = 1;
        } else if (mobile.isConnectedOrConnecting ()) {

            s = "Mobile data"; //getNetworkTypeString (listenService.telMgr.getNetworkType());
           // s += "\n";
            mView_HealthStatus.connectionType = "Mobile data";
            mView_HealthStatus.connectionTypeIdentifier = 2;
        } else {
            s = "Offline";
            mView_HealthStatus.connectionTypeIdentifier = 0;
            //Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
        return s;
    }

    public void sendMessageToActivity(int msg){
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
