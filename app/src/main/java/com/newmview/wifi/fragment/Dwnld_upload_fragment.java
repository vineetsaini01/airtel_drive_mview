package com.newmview.wifi.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newmview.wifi.AsyncTaskTools;
import com.newmview.wifi.CommonFunctions;
import com.mview.airtel.R;
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.UploadToFtp;
import com.newmview.wifi.activity.MainActivity;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.Pinger;
import com.newmview.wifi.helper.Interfaces;
import com.newmview.wifi.helper.SFTPConnectionDownload;
import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.SFTPconnection;
import com.newmview.wifi.other.Utils;
import com.newmview.wifi.other.WifiConfig;
import com.droidbond.loadingbutton.LoadingButton;

import com.github.anastr.speedviewlib.PointerSpeedometer;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.gpsTracker.GPSTracker;import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * Created by functionapps on 10/31/2018.
 */

public class Dwnld_upload_fragment extends Fragment{

    private static final int U_PERMISSION = 1;
    public static Context mContext;
    TextView resultTextView,textview,currentspeed,statustext,resultHdr;
    TextView statustext1,resultTextView1,currentspeed1,sizetext,resultHdr1;
    TextView dwnldtime,dwnldsize,dwnldspeed;
    TextView uploadtime,uploadsize,uploadspeed;
    Float currntbnd;

    //ColorArcProgressBar progBar;
    com.github.anastr.speedviewlib.PointerSpeedometer PointerSpeedometer;

    Button startUploadBtn,selectBtn;
    long starttime;
    String starttimeN;
    LoadingButton startBtn;
    LoadingButton getStartBtn;
    ProgressBar getProgBar;

    long endtime;

    public String LOG_TAG = "Download mview";
    String downloadResponse = "";
    float time,size,speed;

    public static String sourceUrl = "";


    public static String filename = "b.mp3";
    //    public static String desDirectory = "upload";
    public static String desDirectory = "";
    /* public static String desDirectory = "/";*/

    //private ProgressDialog mProgressDialog;
    ProgressBar progBarUpload,progBar;


    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    public String downloadfileURL = "http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
    //  public static String ftpserver = "hi-dj.com";
    public static String ftpserver=  "ftp://speedtest.tele2.net";
    //  public static String ftpserver=  "ftp://ftp.dlptest.com/";
    public String ftpuser ="dlpuser@dlptest.com" , ftppwd="fLDScD4Ynth0p4OJ6bW6qCxjh";
    // public String ftpuser ="hidjftp" , ftppwd="hidj@2015";


    public static final int FILE_SELECT = 780;
    public static final int SETTINGS_ACTIVITY = 781;

    private float bandvalue;
    private Button uploadbtn;
    private TextView uploaddownload;
    public static float MAX_SPEED_INDIA= 10;
    private String newbandvalue;
    private View view;
    private DownloadFileAsync downloadFileAsync;
    private UploadTask uploadTask;
    private long dlspeed;
    private long tottalband;
    public static boolean uploadcancel=false;
    private Button stopbtn;
    private FTPClient mFTPClient;
    private Bundle args;
    private String lat,lon;
    private String type;
    private String path;
    private SFTPconnection sftPconnection;
    private SFTPConnectionDownload sftpConnectionDownload;
    private List<Float> ticks;
    private LinearLayout connecting_layout;
    private RelativeLayout gauuge_layout;
    private Button start;
    private TextView ping_tv,pcktlossval;
    private LineChart realTimeLineChart;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;
    private LineDataSet dataset;
    private boolean valuesset;
    private String uploadResponse;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
        lbm.registerReceiver(receiver, new IntentFilter("speed_result"));

    }

    private void seekPermissions() {
        if (getActivity() != null) {
            if (checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, U_PERMISSION);
            }
            else {
                startTheTest();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == U_PERMISSION)
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTheTest();
            }
    }
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras()!=null) {
                int msg = Integer.parseInt(intent.getStringExtra("msg"));
                String msgshow = intent.getStringExtra("msgshow");
                String index=intent.getStringExtra("index");
                String isLast= intent.getStringExtra("isLast");

                Log.i("Index","In reciever class "+isLast);
                sendMessageToActivity(msg, msgshow,index,isLast);
                //        System.out.println("in case of broadcast"+msg+"   "+msgshow);
            }


        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.uploaddownload_new,container,false);

        init();


        if(getActivity() instanceof MainActivity) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.back_new);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getActivity()!=null) {
                        getActivity().onBackPressed();
                    }

                }
            });
        }



/*
            TinyDB db = new TinyDB(getActivity());

            String t1 = db.getString("downloadurl");
            if( t1 == null || t1.equals(""))
                downloadfileURL = "ftp://speedtest.tele2.net/5MB.zip"; //http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
            else
                downloadfileURL = t1;

            t1 = db.getString("ftpserverurl");

            if( t1 == null || t1.equals(""))
                ftpserver = "speedtest.tele2.net";
            else
                ftpserver = t1;

            t1 = db.getString("ftpusername");

            if( t1 == null || t1.equals(""))
                ftpuser = "anonymous";
            else
                ftpuser = t1;

            t1 = db.getString("ftppwd");

            if( t1 == null || t1.equals(""))
                ftppwd = "admin@mview.com";
            else
                ftppwd = t1;

            t1 = db.getString("ftpuploaddir");
            if( t1 == null || t1.equals(""))
                desDirectory = "upload";
            else
                desDirectory = t1;


            mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
            mView_HealthStatus.mySpeedTest.uploadtest = (MySpeedTest.UploadDownload)db.getObject("upload", MySpeedTest.UploadDownload.class );
            mView_HealthStatus.mySpeedTest.downloadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
            mView_HealthStatus.mySpeedTest.downloadtest = (MySpeedTest.UploadDownload)db.getObject("download", MySpeedTest.UploadDownload.class );

            if(mView_HealthStatus.mySpeedTest.uploadtest != null)
            {
                long tt = mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS;
                long sz = mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes;
                float bandInbps = ((sz)/((tt)/1000));
                float band = bandInbps/(1024*1024); //Mbps
                float szInMB = sz/(1024*1024);
                float ttInSecs = tt/1000;
                 uploadResponse = String.format("%.2f", band) + "Mbps";

//                String uploadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec, FileSize=" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";

               String displaydate=mView_HealthStatus.mySpeedTest.uploadtest.startTime;

                resultHdr1.setText("Test Results (" + displaydate +")");
            }
            if(mView_HealthStatus.mySpeedTest.downloadtest != null) {
                long tt = mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS;
                long sz = mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes;

                float bandInbps = ((sz) / ((tt) / 1024F));
                float band = bandInbps / (1024F * 1024F); //Mbps
                float szInMB = sz / (1024F * 1024F);
                float ttInSecs = tt / 1000F;
//                downloadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec, FileSize =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";
                //resultTextView.setText(downloadResponse);

                time = ttInSecs;
                size = szInMB;
                speed = band;
//                downloadResponse =  String.format("%.2f", speed) + "Mbps";

            }*/
/*
			dwnldtime.setText(String.format("%ssec", String.format("%.2f", ttInSecs)));
			dwnldsize.setText(String.format("%sMb", String.format("%.2f", szInMB)));
			dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", band)));*/


                /*SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM hh:mm");
                Date resultdate = new Date(mView_HealthStatus.mySpeedTest.downloadtest.startTime);
                String displaydate = sdf.format(resultdate);*/
        //uploaddownload.setText(displaydate);

        //	resultHdr.setText("Test Results (" + displaydate +")");



        setColours(progBarUpload,
                0xff303030,   //bgCol1  grey
                0xff909090,   //bgCol2  lighter grey
                0xff0000FF,   //fg1Col1 blue
                0xffFFFFFF,   //fg1Col2 white
                0,           //value1
                0xffFF0000,   //fg2Col1 red
                0xffFFFFFF,   //fg2Col2 white
                0);          //value2

        if( mView_HealthStatus.connectionTypeIdentifier == 1)
            textview.setText(mView_HealthStatus.connectionType+"("+ WifiConfig.getConnectedWifiDetails().getSsidName() + ")" );
            //   textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType);
        else
            textview.setText(mView_HealthStatus.OperatorName + "  " + mView_HealthStatus.connectionType + " " + mView_HealthStatus.strCurrentNetworkState);

        start.setOnClickListener(v -> {

            {


                PointerSpeedometer.realSpeedTo(0);
                seekPermissions();

            }


        });





        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath();;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Uri myUri = Uri.parse(folderPath);
                intent.setDataAndType(myUri, "*/*");
                startActivityForResult(intent, FILE_SELECT);
            }
        });
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview" );
        if (!file.exists()) {
            file.mkdirs();
        }

        sourceUrl = path + "/mview/b.mp3";



        return view;
    }

    private void startTheTest() {

        if (MainActivity.ud.equalsIgnoreCase("download")) {
           initiateDwnldTest();

        }
        else if (MainActivity.ud.equalsIgnoreCase("upload")) {
            initiateUploadTest();



        }
    }

    private void initiateUploadTest() {
        if(CommonFunctions.chkDataConnectivity(getActivity()).equalsIgnoreCase("offline"))
        {
            showAlertDialog("Speed Test", Constants.NO_INTERNET);
        }
        else  if( uploadstatus == 0) {
            System.out.println("status in upload status is zero");
            getProgBar.setVisibility(View.VISIBLE);
            dwnldtime.setText("");
            dwnldsize.setText("");
            dwnldspeed.setText("");
            startBtn.setEnabled(false);
            uploadTask = new UploadTask();
            AsyncTaskTools.execute( uploadTask);
        }
    }

    private void initiateDwnldTest() {

        if(CommonFunctions.chkDataConnectivity(getActivity()).equalsIgnoreCase("offline"))
        {
            showAlertDialog("Speed Test",Constants.NO_INTERNET);
        }
        else
        if (downloadstatus == 0) {
            dwnldtime.setText("");
            dwnldsize.setText("");
            dwnldspeed.setText("");
            getProgBar.setVisibility(View.VISIBLE);
            downloadFileAsync = new DownloadFileAsync();
            AsyncTaskTools.execute(downloadFileAsync, downloadfileURL);
        } else {
            if (downloadFileAsync != null)
            {
                sendMessageToActivity(5, "", "0", null);
            }
            {
                downloadstatus = 0;
                currentspeed.setText("");
                PointerSpeedometer.realSpeedTo(0);
                progBar.setProgress(0);
            }
        }
    }

    private void pingBeforeDownloadingOrUploading() {

        Utils.ping2(Constants.alpha2host, new Interfaces.PingResult() {
            @Override
            public void onPingResultObtained(String response) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null) {
                            String seg[] = response.split("/");
                            if (seg[1].length() > 0) {
                                ping_tv.setText(seg[0]);
                            }
                            if (seg[1].length() > 0) {
                                pcktlossval.setText(seg[1]);
                            }
                        }
                    }
                });
            }

            @Override
            public void parsePingResult(Pinger response) {

            }
        }, null);
    }

               /* getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null) {
                            String seg[] = response.split("/");
                            if (seg[1].length() > 0) {
                                ping_tv.setText(seg[0]);
                            }
                            if (seg[1].length() > 0) {
                                pcktlossval.setText(seg[1]);
                            }
                        }*/





    private void showAlertDialog(String title,String message) {
        AlertDialog.Builder alert=new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        alert.setMessage(message);
        alert.setTitle(title);
        alert.setCancelable(true);
        alert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    void showAlert(String msg, Context context) {

        final Dialog dialog = new Dialog(context, R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alertdialog, null);
        TextView textView = view.findViewById(R.id.msg);
        textView.setText(msg);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }

    void showspeedAlert(String dmsg, String umsg) {

        final Dialog dialog = new Dialog(getActivity(), R.style.AlertDialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.speed_test, null);
        TextView dtextView = view.findViewById(R.id.dtxtview);
        TextView utextView = view.findViewById(R.id.utxtview);
        TextView latencytxtview = view.findViewById(R.id.latencttxtview);
        TextView packetlosstxtview = view.findViewById(R.id.packetlosstxtview);

        TextView linkspeedtxtview = view.findViewById(R.id.linkspeedtxtview);



        linkspeedtxtview.setText(""+ mView_HealthStatus.mySpeedTest.uploadtest.linkspeed);
        if (Utils.checkifavailable(dmsg)) {


            dtextView.setText(dmsg);
        } else
        {
            dtextView.setText("Not available");

        }
        if (Utils.checkifavailable(umsg)) {

            utextView.setText(umsg);
        }
        else
        {
            utextView.setText("Not available");

        }
        if (Utils.checkifavailable(ping_tv.getText().toString()))
        {

            latencytxtview.setText(ping_tv.getText().toString());
            packetlosstxtview.setText(pcktlossval.getText().toString());

        }
        else
        {
            latencytxtview.setText("Not available");
            packetlosstxtview.setText("Not available");


        }
       /* if (Utils.checkifavailable(pcktlossval.getText().toString()))
        {

            packetlosstxtview.setText(pcktlossval.getText().toString());
        }
        else
        {

*/
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        Button okbutton = view.findViewById(R.id.ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });
    }


    private void init() {
        mContext=getActivity();
        ping_tv=view.findViewById(R.id.pingime);
        pcktlossval=view.findViewById(R.id.lossval);
        connecting_layout=view.findViewById(R.id.connecting_layout);
        gauuge_layout=view.findViewById(R.id.gauge_layout);
        textview = (TextView) view.findViewById(R.id.textview);
        currentspeed = (TextView) view.findViewById(R.id.currentspeed);
        start=(Button)view.findViewById(R.id.start);
        getProgBar=(ProgressBar)view.findViewById(R.id.progress);
        realTimeLineChart=view.findViewById(R.id.realTimeGraph);
        //resultTextView = (TextView) view.findViewById(R.id.result);
        //statustext = (TextView) view.findViewById(R.id.statustext);

        startBtn = (LoadingButton) view.findViewById(R.id.startdwnldBtn);
        getStartBtn=(LoadingButton)view.findViewById(R.id.startBtn);
        //startUploadBtn = (Button)view.findViewById(R.id.startuploadBtn);
        // uploadbtn=(Button)view.findViewById(R.id.startuploadBtn);

        statustext1 = (TextView) view.findViewById(R.id.statustext1);
        resultTextView1 = (TextView) view.findViewById(R.id.result1);
        currentspeed1 = (TextView) view.findViewById(R.id.currentspeed1);
        sizetext = (TextView) view.findViewById(R.id.sizetext);

        //resultHdr = (TextView) view.findViewById(R.id.resultHdr);
        resultHdr1 = (TextView) view.findViewById(R.id.resultHdr1);

        selectBtn = (Button) view.findViewById(R.id.selectBtn);
        progBar = (ProgressBar) view.findViewById(R.id.loading);

        progBarUpload = (ProgressBar) view.findViewById(R.id.loading1);
        dwnldsize = (TextView) view.findViewById(R.id.dwnldsize);
        dwnldtime = (TextView) view.findViewById(R.id.dwnldtime);
        dwnldspeed = (TextView) view.findViewById(R.id.dwnldspeed);
        uploadsize = (TextView) view.findViewById(R.id.uploadsize);
        uploadtime = (TextView) view.findViewById(R.id.uploadtime);
        uploadspeed = (TextView) view.findViewById(R.id.uploadspeed);

        PointerSpeedometer = (PointerSpeedometer)view.findViewById(R.id.speedView);

        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wm.getConnectionInfo();
        int freq=wifiInfo.getFrequency();
        int linkspeed=wifiInfo.getLinkSpeed();

        if (linkspeed<100)
        {
//                            Log.i("Linkspeed","1 case");

            PointerSpeedometer.setMaxSpeed(100);

        }
        else if (linkspeed>=100&&linkspeed<500)
        {
//                            Log.i("Linkspeed","2 case");

            PointerSpeedometer.setMaxSpeed(500);

        }

        else if (linkspeed>=500&&linkspeed<1000)
        {

            PointerSpeedometer.setMaxSpeed(1000);

        }
        else if(linkspeed>=1000)
        {
            PointerSpeedometer.setMaxSpeed(2000);

        }




//        PointerSpeedometer.setMaxSpeed(100);
        PointerSpeedometer.setTickNumber(8);






        PointerSpeedometer.setUnit("Mbps");
        PointerSpeedometer.setIndicatorWidth(9);
        PointerSpeedometer.setUnitTextSize(35);

        PointerSpeedometer.setWithIndicatorLight(false);



        args=getArguments();
        if(args!=null)
            type=args.getString("type");
        GPSTracker gps=GPSTracker.getGps(getActivity());
        if(gps!=null)
        {
            lat=gps.getLatitude()+"";
            lon=gps.getLongitude()+"";

        }
        else {
            lat="0.0";
            lon="0.0";
        }


        entries = new ArrayList<>();
        labels=new ArrayList<>();
        //stopbtn=view.findViewById(R.id.stopdwnldBtn);

        chartCustomization(realTimeLineChart);

    }

    private void chartCustomization(LineChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLimitLinesBehindData(false);
        chart.setDescription("");
        xAxis.setSpaceBetweenLabels(4);
        realTimeLineChart.getAxisRight().setEnabled(false);
        realTimeLineChart.getAxisRight().setEnabled(false);
        realTimeLineChart.getXAxis().setEnabled(false);
        realTimeLineChart.getAxisLeft().setDrawGridLines(false);
        realTimeLineChart.getAxisRight().setDrawAxisLine(false);
        realTimeLineChart.getXAxis().setDrawGridLines(false);
        // realTimeLineChart.getLegend().setEnabled(false);


        //  chart.setBackgroundColor(getResources().getColor(R.color.textColor));//Set as a black
        chart.setDrawGridBackground(false);

        chart.invalidate();


    }


    private void setValuesToRealTimeGraph(String message, int i) {

        float val = Float.parseFloat(message);
        entries.add(new Entry(val, i));
        labels.add(String.valueOf(i));

        dataset = new LineDataSet(entries, "Speed in Mbps");
        dataset.setDrawValues(false);
        dataset.setCircleSize(0f);
        dataset.setColor(getResources().getColor(R.color.textColor));

        LineData data = new LineData(labels, dataset);
        realTimeLineChart.setData(data);
        realTimeLineChart.invalidate();

    }

    public void setColours(ProgressBar progressBar,
                           int bgCol1, int bgCol2,
                           int fg1Col1, int fg1Col2, int value1,
                           int fg2Col1, int fg2Col2, int value2) {
        //If solid colours are required for an element, then set
        //that elements Col1 param s the same as its Col2 param
        //(eg fg1Col1 == fg1Col2).

        //fgGradDirection and/or bgGradDirection could be parameters
        //if you require other gradient directions eg LEFT_RIGHT.

        GradientDrawable.Orientation fgGradDirection
                = GradientDrawable.Orientation.TOP_BOTTOM;
        GradientDrawable.Orientation bgGradDirection
                = GradientDrawable.Orientation.TOP_BOTTOM;

        //Background
        GradientDrawable bgGradDrawable = new GradientDrawable(
                bgGradDirection, new int[]{bgCol1, bgCol2});
        bgGradDrawable.setShape(GradientDrawable.RECTANGLE);
        bgGradDrawable.setCornerRadius(5);
        ClipDrawable bgclip = new ClipDrawable(
                bgGradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
        bgclip.setLevel(10000);

        //SecondaryProgress
        GradientDrawable fg2GradDrawable = new GradientDrawable(
                fgGradDirection, new int[]{fg2Col1, fg2Col2});
        fg2GradDrawable.setShape(GradientDrawable.RECTANGLE);
        fg2GradDrawable.setCornerRadius(5);
        ClipDrawable fg2clip = new ClipDrawable(
                fg2GradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        //Progress
        GradientDrawable fg1GradDrawable = new GradientDrawable(
                fgGradDirection, new int[]{fg1Col1, fg1Col2});
        fg1GradDrawable.setShape(GradientDrawable.RECTANGLE);
        fg1GradDrawable.setCornerRadius(5);
        ClipDrawable fg1clip = new ClipDrawable(
                fg1GradDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgclip, fg2clip, fg1clip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);

        //Copy the existing ProgressDrawable bounds to the new one.
        Drawable aa = progressBar.getProgressDrawable();
        if( aa != null) {
            Rect bounds = progressBar.getProgressDrawable().getBounds();
            progressBar.setProgressDrawable(progressLayerDrawable);
            progressBar.getProgressDrawable().setBounds(bounds);
        }

        // setProgress() ignores a change to the same value, so:
        if (value1 == 0)
            progressBar.setProgress(1);
        else
            progressBar.setProgress(0);
        progressBar.setProgress(value1);

        // setSecondaryProgress() ignores a change to the same value, so:
        if (value2 == 0)
            progressBar.setSecondaryProgress(1);
        else
            progressBar.setSecondaryProgress(0);
        progressBar.setSecondaryProgress(value2);

        //now force a redraw
        progressBar.invalidate();
    }




    public static int uploadstatus = 0;

    UploadToFtp uploadToFtp;

    public void testing() {
        System.out.println("testing............");
    }


    public class UploadTask extends AsyncTask<Void, Integer, Void> {
        ProgressDialog pDialog;
        int uploadStat;
        UploadToFtp utp = new UploadToFtp();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            settoolbar("Upload test");
//            pingBeforeDownloadingOrUploading();

            uploadstatus = 1;
            progBarUpload.setProgress(0);
            progBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            System.out.println("source url -> " + sourceUrl);
            System.out.println("filename -> " + filename);
            System.out.println("desDirectory -> " + desDirectory);
            System.out.println("status in doin");
            //if(!isCancelled()) {


            sftPconnection=new SFTPconnection();
            uploadstatus= sftPconnection.uploadFile(selectedFilePath,mContext,progBar,PointerSpeedometer,connecting_layout,gauuge_layout);
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            System.out.println("Result-->" + result);
            System.out.println("status in postexe"+uploadstatus);
            super.onPostExecute(result);
//            uploadResponse = String.format("%.2f", band) + "Mbps";

            try {
                if (uploadstatus == 1) {
                    uploadstatus = 0;
                } else {
                    String title="Upload Test";
                    if (uploadstatus == -3) {
                        showAlertDialog(title, "Unknown FTP host. Please check Settings!");
                    }
                    else if (uploadstatus == -1) {
                        showAlertDialog(title,Constants.CONNECTION_ERROR);
//                        start.setText("Start");
                        getProgBar.setVisibility(View.GONE);
                    } else if (uploadstatus == -2) {
                        showAlertDialog(title,"Connection timed out error, please try again!");

                        //  startBtn.setText("START");
                    } else if (uploadstatus == -4) {
                        showAlertDialog(title,"FTP username/pwd may not be correct. Please check Settings!");
                    }
                    uploadstatus = 0;
                    statustext1.setText("");
                    currentspeed1.setText("");
                    progBarUpload.setProgress(0);
                    progBar.setProgress(0);

                }
            }catch(Exception e)
            {
                System.out.println("Exception in speed test is "+e.toString());
            }
            finally {
                uploadstatus = 0;
//                start.setText("Start");
                getProgBar.setVisibility(View.GONE);
                settoolbar("Speed test");
//                Spanned strMessage = new SpannableString(Html.fromHtml("Download speed "+" "+mView_HealthStatus.mySpeedTest.downloadtest.dspeed+  "\n" + " Upload speed "+ mView_HealthStatus.mySpeedTest.uploadtest.uspeed));
//                showAlertDialog("Speed test",strMessage.toString());

                showspeedAlert(mView_HealthStatus.mySpeedTest.downloadtest.dspeed,mView_HealthStatus.mySpeedTest.uploadtest.uspeed);












































































































































































































































                // startBtn.setText("Start");
                startBtn.setEnabled(true);
                //startUploadBtn.setText("Start");
            }
        }//end onPostExecute

    }

    @Override
    public void onPause() {
        super.onPause();
        //   Toast.makeText(mContext, "on pause", Toast.LENGTH_SHORT).show();
        //  LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //    Toast.makeText(mContext, "ondestroy", Toast.LENGTH_SHORT).show();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        try {
            if (downloadFileAsync != null)
                downloadFileAsync.cancel(true);
        }
        catch(Exception e)
        {
            System.out.println("exception is "+e.toString());
        }
        try {

            if( uploadTask != null) {
                uploadTask.cancel(true);
                if (uploadToFtp != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadToFtp.abortFTP();
                        }
                    }).start();

                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();// android.os.NetworkOnMainThreadException
        }
      /*  try {
            Toast.makeText(mContext, "upload task is "+uploadTask +"upload to ftp "+uploadToFtp, Toast.LENGTH_SHORT).show();
            if( uploadTask != null)
                uploadTask.cancel(true);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (uploadToFtp != null) {
                        //uploadcancel=true;
                        uploadToFtp.abortFTP();
                    }




                }
            }).start();


        }catch(Exception e)
        {
            System.out.println("exception is "+e.toString());

        }*/
    }
    ////////////////new sharad1010///////////////////////

    float currentband;
    int downloadstatus = 0;

    //this is our download file asynctask
    class DownloadFileAsync extends AsyncTask<String, Integer, Void> {

        private long lastTxBytes,lastRxBytes,lastTimestamp;
        private long bandInbps;
        private String type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            start.setText( "Starting speed test");
            settoolbar("Download test");
            pingBeforeDownloadingOrUploading();
            PointerSpeedometer.realSpeedTo(0);
            progBarUpload.setProgress(0);
            downloadstatus = 1;
        }
        @Override
        protected Void doInBackground(String... aurl) {
            try {
                sftpConnectionDownload=new SFTPConnectionDownload();
                // BY SWAPNIL 26/04/2023
                String path = MviewApplication.ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
               // String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                File file = new File(path, "mview" );
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fname="Delhi.json";
                //this is where the file will be seen after the download
                String finalPath=path + "/mview/"+fname;
                FileOutputStream f = new FileOutputStream(new File(path + "/mview/", fname));
                downloadstatus= sftpConnectionDownload.downloadTask(mContext,f,progBar,getProgBar,connecting_layout,gauuge_layout,finalPath);
            }
            catch (Exception e) {
                System.out.println("exception in dwnldng msg "+e.getMessage());
                e.printStackTrace();
                Log.d(LOG_TAG, e.getMessage());
                downloadstatus = -1;
            }
            finally {
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void unused) {
            try {
                if (downloadstatus == 2) {
                    dwnldtime.setText(String.format("%ssec", String.format("%.2f", time)));
                    dwnldsize.setText(String.format("%sMb", String.format("%.2f", size)));
                    dwnldspeed.setText(String.format("%sMbps", String.format("%.2f", speed)));
                    downloadResponse =  String.format("%.2f", speed) + "Mbps";

                }
                else if(downloadstatus==-1)
                {
                    downloadResponse =  "Not available";

                    showAlertDialog("Download Test",Constants.CONNECTION_ERROR);
                    getProgBar.setVisibility(View.GONE);
//                    start.setText("Start");

                }
                else if (downloadstatus < 0) {
                    downloadResponse =  "Not available";

                    showAlertDialog("Download Test",Constants.INCORRECT_URL);


                }
                downloadstatus = 0;
                currentspeed.setText("");
                startBtn.setEnabled(true);
            }
            catch(Exception e)
            {
                System.out.println("exception is "+e.toString());
            }
            finally {
                downloadstatus = 0;
//               MainActivity.ud="upload";
//                startTheTest();
/*
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
          /*      MainActivity.ud="upload";
                startTheTest();*/
//                initiateUploadTest();

            }
        }//end onPostExecute
    }

    private void settoolbar(String status) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(status);
    }

    private void makeFtpConnection() {

        try {
            int port = 21;


            mFTPClient.connect(ftpserver, port); // connecting to the host
            mFTPClient.login(ftpuser, ftppwd); // Authenticate using username and password
            mFTPClient.changeWorkingDirectory(desDirectory); // change directory
            System.out.println("Dest Directory-->" + desDirectory); // to that directory where image will be uploaded
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            Utils.getFileSize(mFTPClient,downloadfileURL);



        } catch(FileNotFoundException e1){
            //status = -1;
            e1.printStackTrace();
        }
        catch(UnknownHostException e2)
        {
            // status = -3;
            e2.printStackTrace();
        }catch (Exception e) {
            // status = -2;
            e.printStackTrace();
        }
        finally {
            try {
               /* if (outputStream != null) {
                    outputStream.close();
                }*/
                if (mFTPClient.isConnected()) {
                    mFTPClient.logout();
                    mFTPClient.disconnect();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }

    String message = "";
    int i=0;
    String last_packet;

    public void sendMessageToActivity(int msg, String msgShow, String index, String isLast) {
        //System.out.println("calling send mesg to activity");
        Message m = new Message();
        m.what = msg;
        message = msgShow;
        last_packet=isLast;

        Log.i("Index","At index "+index+" value is "+message+" islast value is "+last_packet);

        try {


            if (index != null) {
                i = Integer.parseInt(index);
            } else {
                i = 0;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        updateHandler.sendMessage(m);



        //this.currntbnd=currentband;

    }





    public Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) { try {


                int event = msg.what;
                switch (event) {
                    case 1:
                        if (message != null) {
//                            PointerSpeedometer.realSpeedTo(0);
                            if(!valuesset&& MainActivity.ud.equalsIgnoreCase("upload")) {
                                valuesset=true;
                                entries = new ArrayList<>();
                                PointerSpeedometer.speedTo(0F);

                                labels = new ArrayList<>();
                            }

//                            PointerSpeedometer.speedTo();
                            float speed=0F;
                            try {
                                speed=Float.parseFloat(message);


                            }
                            catch (Exception e)
                            {
                                Log.i("Index","Exception  while casting speed "+e.toString());
                            }
//                            PointerSpeedometer.realSpeedTo(speed);
                            Log.i("Index","Speedo meter going to "+speed);
                            PointerSpeedometer.speedTo(speed);
                            setValuesToRealTimeGraph(message,i);
                            if (last_packet.equalsIgnoreCase("true"))
                            {
                               /* Thread.sleep(5000);

                                MainActivity.ud="upload";
                startTheTest();*/
//                                Thread.sleep(5000);

                               /* new CountDownTimer(5000,0) {
                                    public void onFinish() {
                                        // When timer is finished
                                        // Execute your code here
                                        initiateUploadTest();

                                    }

                                    public void onTick(long millisUntilFinished) {
                                        // millisUntilFinished    The amount of time until finished.
                                    }
                                }.start();*/
                                Handler    handler=new Handler();
                                Runnable r=new Runnable() {
                                    public void run() {
                                        //what ever you do here will be done after 3 seconds delay.

                                        MainActivity.ud="upload";
                                        startTheTest();
                                    }
                                };
                                handler.postDelayed(r, 3000);
//                                while (i<5000)
//                                {
//                                    i++;
//                                    if (i==5000)
//                                    {
//                                        initiateUploadTest();
//                                    }
//
//                                }


                            }
                            //dwnldspeed.setText(String.valueOf(Float.parseFloat(message)));
                        }
                        break;
                    case 2:
                        //  Toast.makeText(getActivity(), "case "+ event, Toast.LENGTH_SHORT).show();
                        // System.out.println("calling in case 2 message"+message);
                        String band=null;
                        if (message != null)
                        {

                            String seg[] = message.split("/");
                            String ttInSecs = seg[1];
                            String szInMB = seg[2];
                            band = seg[3];
                            dwnldtime.setText(String.format("%ssec", ttInSecs));
                            dwnldsize.setText(String.format("%sMB", szInMB));
                            if (message.contains("Kbps")) {
                                dwnldspeed.setText(band);
                            } else {
                                dwnldspeed.setText(band);
                            }
                        }
                        startBtn.setEnabled(true);
                        if (type == null) {
                            type = "";
                        }
                    {
                        String testres = null;
                        Float val = (Float.parseFloat(band.replace("Mbps", "")));
                        if(MainActivity.ud.equalsIgnoreCase("upload")) {



                            if (val < 1) {
                                testres = "Poor";
                            } else if (val >= 1 && val <= 5) {
                                testres = "Good";
                            } else if
                            (val > 5) {
                                testres = "Very Good";
                            }
//                            CommonAlertDialog.showResultAlertDialog("Upload Test", getActivity(), 0, testres,
//                                    R.layout.dl_ul_result,type,lat,lon,"Data Bandwidth issue");
                            MainActivity.testflag=true;
                        }

                        else if(MainActivity.ud.equalsIgnoreCase("download"))
                        {
                            if (val < 1) {
                                testres = "Bad";
                            } else if (val >= 1 && val <= 5) {
                                testres = "Poor";
                            } else if (val > 5 && val<=20 ){
                                testres = "Good";
                            } else if(val>20)
                            {
                                testres="Very Good";
                            }
//                            CommonAlertDialog.showResultAlertDialog("Download Test",
//                                    getActivity(), 0, testres, R.layout.dl_ul_result, type, lat, lon,"Data Bandwidth issue");

                            MainActivity.testflag=true;

                        }
                    }

/*

                    if (MainActivity.REPORT_FLAG) {
                            if (type == null) {
                                type = "";
                            }
                            {
                                String testres = null;
                                Float val = (Float.parseFloat(band.replace("Mbps", "")));
                                if(MainActivity.ud.equalsIgnoreCase("upload")) {



                                    if (val < 1) {
                                        testres = "Poor";
                                    } else if (val >= 1 && val <= 5) {
                                        testres = "Good";
                                    } else if
                                    (val > 5) {
                                        testres = "Very Good";
                                    }
                                    CommonAlertDialog.showResultAlertDialog("Upload Test", getActivity(), 0, testres,
                                            R.layout.dl_ul_result,type,lat,lon,"Data Bandwidth issue");
                                    MainActivity.testflag=true;
                                }
                                else if(MainActivity.ud.equalsIgnoreCase("download"))
                                {
                                    if (val < 1) {
                                        testres = "Bad";
                                    } else if (val >= 1 && val <= 5) {
                                        testres = "Poor";
                                    } else if (val > 5 && val<=20 ){
                                        testres = "Good";
                                    } else if(val>20)
                                    {
                                        testres="Very Good";
                                    }
                                    CommonAlertDialog.showResultAlertDialog("Download Test",
                                            getActivity(), 0, testres, R.layout.dl_ul_result, type, lat, lon,"Data Bandwidth issue");

                                    MainActivity.testflag=true;

                                }
                            }




                            //Utils.showReportIssueAlert(type, getActivity(), lat, lon, Constants.UL_DL_TEST_DONE);
                        }
                        else if(MainActivity.RESULT_FLAG)
                        {
                            String testres = null;
                            Float val = (Float.parseFloat(band.replace("Mbps", "")));
                            if(MainActivity.ud.equalsIgnoreCase("upload")) {



                                if (val < 1) {
                                    testres = "Poor";
                                } else if (val >= 1 && val <= 5) {
                                    testres = "Good";
                                } else if (val > 5) {
                                    testres = "Poor";
                                }
                                CommonAlertDialog.showResultAlertDialog("Upload Test", getActivity(), 0, testres,
                                        R.layout.dl_ul_result,type,lat,lon,"Data Bandwidth issue");
                            }
                            else if(MainActivity.ud.equalsIgnoreCase("download"))
                            {
                                if (val < 1) {
                                    testres = "Bad";
                                } else if (val >= 1 && val <= 5) {
                                    testres = "Poor";
                                } else if (val > 5 && val<=20 ){
                                    testres = "Good";
                                } else if(val>20)
                                {
                                    testres="Very Good";
                                }
                                CommonAlertDialog.showResultAlertDialog("Download Test",
                                        getActivity(), 0, testres, R.layout.dl_ul_result, type, lat, lon, "Data Bandwidth issue");
                            }
                        }
*/


                    break;
                    case 3:
                        //   Toast.makeText(getActivity(), "case "+ event, Toast.LENGTH_SHORT).show();
                        //     System.out.println("in case 3 the message is"+message);
                        // sizetext.setText(message);
                        break;
                    case 4:
                        //  Toast.makeText(getActivity(), "case "+ event, Toast.LENGTH_SHORT).show();
                        //   System.out.println("status in case 4");
                        statustext1.setText("");
                        //startBtn.setText("Start");
                        startBtn.setEnabled(true);
                        //  startUploadBtn.setText("Start");
                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    uploadstatus = 0;

                                    uploadTask.cancel(true);

                                    uploadTask = null;
                                    if (uploadToFtp != null) {
                                        uploadcancel = true;
                                        System.out.println("status in abort status in dwnld/upload class ");
                                    }
                                    // uploadToFtp.abortFTP();
                                } catch (Exception e) {
                                    System.out.println("exception is " + e.toString());
                                    e.printStackTrace();
                                    int a = 0;
                                }
                            }
                        });

                        thread.start();

                        break;
                    case 5:
                        // Toast.makeText(getActivity(), "case "+ event, Toast.LENGTH_SHORT).show();
                        // statustext.setText("");
                        currentspeed.setText("");
                        //  startBtn.setText("Start");
                        startBtn.setEnabled(true);

                        Thread thread1 = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    downloadstatus = 0;

                                    downloadFileAsync.cancel(true);

                                    downloadFileAsync = null;
                                } catch (Exception e) {
                                    System.out.println("exception is " + e.toString());
                                    e.printStackTrace();
                                    int a = 0;
                                }
                                downloadstatus = 0;
                            }
                        });

                        thread1.start();

                        break;
                }//end of switch
            }
            catch (Exception e)
            {

            }
        }
    }; //end of updateHandler

    String selectedFilePath = "";
    int selected_file_sizeInKB = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            System.out.println("status in upload status n onactivity "+uploadstatus);
            switch (requestCode) {
                case FILE_SELECT:
                {
                    Uri uri = data.getData();


                    try {
                        path = getPathOfSelectedFile(uri);
                    } catch (URISyntaxException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if( path == null) {
                        path = getPathOfThisUri(getActivity(), uri);
                    }

                    if( path != null) {
                        System.out.println("path.."+path);
                        File file = new File(path);
                        long t = file.length();
                        selected_file_sizeInKB = Integer.parseInt(String.valueOf(t / 1024));
                        String msg = "Selected file size is " +selected_file_sizeInKB +"kb";
                        Toast.makeText(mContext, "selected file size and path  is   "+selected_file_sizeInKB+"   "+path, Toast.LENGTH_SHORT).show();
                        sendMessageToActivity(3, msg, "0", null);
                        getProgBar.setVisibility(View.VISIBLE);
                        start.setText("Connecting..");


                    }

                    selectedFilePath = path;
                    if(selectedFilePath.equals(""))
                    {
                      /*  AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                                getActivity());
                        alertDialog.setTitle("Upload Test");
                        alertDialog.setMessage("Please select the file to upload!");
                        alertDialog.setCancelable(true);

                        alertDialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                        alertDialog.show();*/
                    }else {
                        if( uploadstatus == 0) {
                            System.out.println("status in upload status is zero");
                            //statustext1.setText("Upload In progress");
                            //	startUploadBtn.setText("Stop");
                            dwnldtime.setText("");
                            dwnldsize.setText("");
                            dwnldspeed.setText("");
                            //   Utils.showAlert(Constants.TEST_START_MSG,getActivity());
                            //
                            //  startBtn.setText("IN PROGRESS");

                            startBtn.setEnabled(false);
                            //filename=path;


                            Utils.ping2(Constants.alpha3host, new Interfaces.PingResult() {
                                @Override
                                public void onPingResultObtained(final String time) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(time!=null) {
                                                try {
                                                    String seg[] = time.split("/");
                                                    if(seg[1]!=null) {
                                                        ping_tv.setText(seg[1]);
                                                    }
                                                    if(seg[1]!=null) {
                                                        pcktlossval.setText(seg[1]);
                                                    }

                                                    System.out.println("ping in test is " + time);
                                                }
                                                catch (ArrayIndexOutOfBoundsException ae)
                                                {
                                                    ae.printStackTrace();
                                                }
                                                finally {
                                                    uploadTask = new UploadTask();
                                                    AsyncTaskTools.execute( uploadTask);
                                                }
                                            }



                                        }
                                    });

                                }

                                @Override
                                public void parsePingResult(Pinger response) {

                                }
                            }, null);

                            //
                            // uploadTask.execute();
                        }else {
                            System.out.println("status in upload status is  "+uploadstatus);
                            if(uploadTask != null) {
                                try {

                                    sendMessageToActivity(4,"", "0", null);

                                }catch(Exception e)
                                {
                                    System.out.println(" status exception is "+e.toString());
                                    e.printStackTrace();
                                    int a =0;
                                }
                            }
                            else
                            {
                                System.out.println("status : upload task is null");
                            }
                        }
                    }
                    break;
                }
                case SETTINGS_ACTIVITY:
                {
                    TinyDB db = new TinyDB(getActivity());

                    String t1 = db.getString("downloadurl");
                    if( t1 == null || t1.equals(""))
                        downloadfileURL = "http://hi-dj.com/hidj/dj_songs/2016DJ000000010151.mp3";
                    else
                        downloadfileURL = t1;

                    t1 = db.getString("ftpserverurl");
                    if( t1 == null || t1.equals(""))
                        ftpserver = "hi-dj.com";
                    else
                        ftpserver = t1;

                    t1 = db.getString("ftpusername");
                    if( t1 == null || t1.equals(""))
                        ftpuser = "hidjftp";
                    else
                        ftpuser = t1;

                    t1 = db.getString("ftppwd");
                    if( t1 == null || t1.equals(""))
                        ftppwd = "hidj@2015";
                    else
                        ftppwd = t1;
                    break;
                }
            }//end switch
        }//end if
    }//end function

    public static String getPathOfSelectedFile(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = Dwnld_upload_fragment.mContext.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getPathOfThisUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    //our progress bar settings
//	@Override
//	protected Dialog onCreateDialog(int id) {
//		switch (id) {
//			case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
//				mProgressDialog = new ProgressDialog(this);
//				mProgressDialog.setMessage("Downloading file…");
//				mProgressDialog.setIndeterminate(false);
//				mProgressDialog.setMax(100);
//				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//				mProgressDialog.setCancelable(true);
//
//
//				GradientDrawable progressGradientDrawable = new GradientDrawable(
//						GradientDrawable.Orientation.LEFT_RIGHT, new int[]{
//						0xff1e90ff,0xff006ab6,0xff367ba8});
//				ClipDrawable progressClipDrawable = new ClipDrawable(
//						progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
//				Drawable[] progressDrawables = {
//						new ColorDrawable(0xffffffff),
//						progressClipDrawable, progressClipDrawable};
//				LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
//
//				progressLayerDrawable.setId(0, android.R.id.background);
//				progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
//				progressLayerDrawable.setId(2, android.R.id.progress);
//
//				mProgressDialog.setProgressDrawable(progressLayerDrawable);
//
//				mProgressDialog.show();
//				return mProgressDialog;
//			default:
//				return null;
//		}
//	}
    /////////////////////////////////////////

    ////////////////

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

/////////////////



    /*@Override
    public  onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        //sharad - commented to hide teh top right menu
//        if (navItemIndex == 0) {
        getActivity().getMenuInflater().inflate(R.menu.settings, menu);
//        }

        // when fragment is notifications, load the menu created for notifications
//		if (navItemIndex == 3) {
//			getMenuInflater().inflate(R.menu.notifications, menu);
//		}
        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            getActivity().onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    ///////////////////

    private Boolean downloadAndSaveFile(String server, int portNumber,
                                        String user, String password, String filename, File localFile)
            throws IOException {
        FTPClient ftp = null;
        Boolean commandOK = false;

        try {
            starttime = System.currentTimeMillis();
            starttimeN=Utils.getDateTime();
            ftp = new FTPClient();
            ftp.setControlEncoding("UTF-8");
            ftp.connect(server, portNumber);
            Log.d(LOG_TAG, "Connected. Reply: " + ftp.getReplyString());

            ftp.login(user, password);
            Log.d(LOG_TAG, "Logged in");
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d(LOG_TAG, "Downloading");
            ftp.enterLocalPassiveMode();
            //ftp.setAutodetectUTF8(true);
            //ftp.enterLocalPassiveMode();
            // OutputStream outputStream = null;
            boolean success = false;
            String errmsg = "";

            try {

                InputStream inputStream = ftp.retrieveFileStream(filename);
                FileOutputStream fileOutputStream = new FileOutputStream(localFile);
                //Using org.apache.commons.io.IOUtils
                IOUtils.copy(inputStream, fileOutputStream);
                fileOutputStream.flush();
                IOUtils.closeQuietly(fileOutputStream);
                IOUtils.closeQuietly(inputStream);
                commandOK = ftp.completePendingCommand();

                //outputStream = new BufferedOutputStream(new FileOutputStream(localFile));
                //success = ftp.retrieveFile(filename, outputStream);
            } catch(Exception e)
            {
                errmsg = e.getMessage();
            }

            finally {
                if (commandOK == true) { //outputStream != null) {
                    //outputStream.close();
                    endtime = System.currentTimeMillis();
                    long tt = endtime-starttime;
                    long sz = localFile.length();
                    float bandInbps = ((sz)/((endtime-starttime)/1000));
                    float band = bandInbps/(1024*1024); //Mbps
                    float szInMB = sz/(1024*1024);
                    float ttInSecs = tt/1000;
                    downloadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + "sec, Size =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";

                }else {
                    String error = ftp.getReplyString();
                    Log.e("ddd", error);
                }
            }

            return success;
        } finally {
            if (ftp != null) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }



}
