package com.newmview.wifi.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.newmview.wifi.TinyDB;
import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.mView_HealthStatus;
import com.droidbond.loadingbutton.LoadingButton;
import com.github.anastr.speedviewlib.PointerSpeedometer;

import com.gpsTracker.GPSTracker;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;


import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SFTPconnection {
    private CopyStreamAdapter streamListener;
    private long startTime=0;
    private String startTimeN;
    private String uploadResponse;
    int status = 0;
    private Context context;
    ProgressBar pDialog;
    String host = "180.179.214.56", username = "mview_ftp",
            password = "92zbVZ";
    String type = null;
    PointerSpeedometer pointerSpeedometer;
    private Dialog dialog;
    private LoadingButton startbtn;
    private RelativeLayout gauge_layout;
    private LinearLayout connecting_layout;
    private ProgressBar connectingProg;
    private int index;
    private ChannelSftp sftpChannel;
    private String fileName;
    private FileOutputStream out;
    private String file;
    private InputStream inputStream;
    private String remoteFilePath;
    private  long initcount;
    private int maxBuffer=500000;//0.5Mb
    private Session session;
    private byte[] buffer;
    private Channel channel;
    private String srcfinalpath;

    //private CallbackContext callbacks = null;

    public int uploadFile(String filename, Context context, ProgressBar progressDialog, PointerSpeedometer pointerSpeedometer, LinearLayout connecting_layout, RelativeLayout gauuge_layout) {
        type = "upload";
        String srcFilePath = filename;
        this.context = context;
        pDialog = progressDialog;
        // startTime = System.currentTimeMillis();
        startTimeN = Utils.getDateTime();
        status = 1;
        this.pointerSpeedometer=pointerSpeedometer;
        this.connecting_layout=connecting_layout;
        this.gauge_layout=gauuge_layout;
        startTime=System.currentTimeMillis();
        index=0;
        remoteFilePath = "/home/mview_ftp";
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(username, host, 30030);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            session.setTimeout(Constants.CONNECTION_TIMEOUT);
            channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
            buffer = new byte[maxBuffer];


            for(int i=0;i<20;i++) {
                writeToFile(buffer, remoteFilePath, sftpChannel,i);
            }

            sftpChannel.exit();
            session.disconnect();
        } catch (JSchException e) {
            e.printStackTrace();
            status=-1;
        } /*catch (SftpException e) {
            e.printStackTrace();

        }*/ catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(srcfinalpath!=null) {
                Utils.deletefileFromFileManager(srcfinalpath);
            }
        }
        return status;

    }

    private void writeToFile(byte[] buffer, String remoteFilePath, ChannelSftp sftpChannel, int i) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        File file = new File(path, "mview");
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path, "mview" + "/" + "upload.txt");

        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            out.close();
            System.out.println("writing  ");
            srcfinalpath=path+"/"+"mview" + "/" + "upload.txt";
            System.out.println("sftp calling "+ i +"time");
            sftpChannel.put(srcfinalpath, remoteFilePath, new SfProgressMonitor());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();
        }


    }


    private class SfProgressMonitor implements SftpProgressMonitor {

        private long max = 0;
        private long count = 0;
        private long percent = 0;
        private float tt = 0F;
        float bandInbps =0F;

        long endtime = 0;


        public void SftpProgressMonitor() {

        }


        @Override
        public void init(int op, String src, String dest, long max) {
            this.max = max;

            System.out.println("starting");
            if(dialog!=null && dialog.isShowing())
            {
                dialog.dismiss();
            }
            if(context!=null) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        connecting_layout.setVisibility(View.GONE);
                        gauge_layout.setVisibility(View.VISIBLE);
                    }
                });
            }


            System.out.println("sftp source "+src); // Origin destination
            System.out.println("sftp des "+dest); // Destination path
            System.out.println("sftp max "+max); // Total filesize

        }

        @Override
        public boolean count(long bytes) {
//bytes tranferred
            //initcount //total bytes transferred till yet for for all turns
            //count is bytes transferred in this count
            this.count += bytes;
            initcount+=bytes;
            long percentNow = this.count * 100 / maxBuffer;//percentage of bytes transferred
            long finalpercentNow=(initcount * 100)/(maxBuffer*20);
            System.out.println("sftp count "+bytes +"init count "+initcount);

            //  System.out.println("type is "+type);
            if (type.equalsIgnoreCase("upload")) {
                if (percentNow > this.percent)
                {
                    //current progress has increases from prev progress
                    this.percent = percentNow;


                    pDialog.setProgress((int) finalpercentNow);


                    System.out.println("sftp progress" + this.percent); // Progress 0,0
                    System.out.println("sftp progress count" + this.count); // Progress in bytes from the total
                    tt = System.currentTimeMillis() - startTime;
//                    Log.i("Linkspeed","time before "+tt);

                    tt = tt / 1.2F;
//                    Log.i("Linkspeed","time after "+tt);

                    float bandvalue = 0F;
                    try {
//bits per second
                        bandInbps = ((initcount * 8) / ((tt) / 1000F));//converted bytes into bits and time from ms to sec
//                        final float currentband = bandInbps / (1024);
                        bandvalue = bandInbps / (1024F * 1024F);//bps to mbps
                        float ct = initcount / (tt / 1000F);//bytes transferred per second
                        System.out.println("sftp band progress mbps " + bandvalue + " count " + ct);
                    } catch (ArithmeticException ae) {
                        ae.printStackTrace();
                    }
//bandvalue=bandvalue*3.6;


                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wm.getConnectionInfo();
                    int freq=wifiInfo.getFrequency();
                    int linkspeed=wifiInfo.getLinkSpeed();

                    if (linkspeed<100)
                    {
//                        Log.i("Linkspeed","1 case");

                        bandvalue = bandvalue * 1.5F;
                    }
                    else if (linkspeed>=100&&linkspeed<500)
                    {
//                        Log.i("Linkspeed","2 case");

                        bandvalue=bandvalue*1.5F;
                    }

                    else if (linkspeed>=500)
                    {
//                        Log.i("Linkspeed","3 case");

                        bandvalue=bandvalue*6F;
                    }


/*

                    if (freq==2.4) {


                        bandvalue = bandvalue * 1.2F;

                    }
                    else if (freq==5)
                    {
                        bandvalue = bandvalue * 2.5F;
                    }

                    else if (freq==6)
                    {
                        bandvalue = bandvalue * 3.5F;

                    }
                    else
                    {
                        bandvalue = bandvalue * 1.2F;

                    }
*/




                    final String str = String.format("%.2f", bandvalue);
//                    final double finalBandvalue = bandvalue;

                    System.out.println("str is " + str + "tt is " + tt + "bps" + bandInbps);

                    index++;
                    Intent sendmsg = new Intent("speed_result");
                    sendmsg.putExtra("msg", "1");
                    sendmsg.putExtra("msgshow", str);
                    sendmsg.putExtra("index", String.valueOf(index));
                    sendmsg.putExtra("isLast", false);

                    System.out.println("index is " + index);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg);
                }
                System.out.println("sftp Total bytes transferred till yet "+initcount +" max buffer "+maxBuffer*20);
                if (initcount == maxBuffer*20)
                {
                    System.out.println("sftp finally Total bytes transferred till yet "+initcount +" max buffer "+maxBuffer*10);
                    endtime = System.currentTimeMillis();
                    tt =( endtime - startTime)/1000F;

                    tt = tt / 1.2F;
                    bandInbps = ((initcount * 8) / ((tt) ));
                    float band = (bandInbps) / (1024F * 1024F); //Mbps
                    float szInMB = (float) initcount / (1000 * 1000);
                    float ttInSecs = tt / 1000;
                    float orgband=band;
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wm.getConnectionInfo();
                    int freq=wifiInfo.getFrequency();
                    int linkspeed=wifiInfo.getLinkSpeed();

                    if (linkspeed<100)
                    {
//                        Log.i("Linkspeed","1 case");
                        band = band * 1.5F;
                    }
                    else if (linkspeed>=100&&linkspeed<500)
                    {
//                        Log.i("Linkspeed","2 case");


                        band=band*1.5F;
                    }

                    else if (linkspeed>=500)
                    {
                        Log.i("Linkspeed","3 case");

                        band=band*6F;
                    }


                   /* if (freq==2.4) {


                        band = band * 1.2F;

                    }
                    else if (freq==5)
                    {
                        band = band * 2.5F;
                    }

                    else if (freq==6)
                    {
                        band = band * 3.5F;

                    }
                    else
                    {
                        band = band * 1.2F;

                    }
*/

                    uploadResponse = "/" + String.format("%.2f", ttInSecs) + "/" + String.format("%.2f", szInMB) + "/" + String.format("%.2f", band) + "Mbps";




                    mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
//                    mView_HealthStatus.mySpeedTest.uploadtest.speed = String.format("%.2f", band) + "Mbps";
                    mView_HealthStatus.mySpeedTest.uploadtest.linkspeed =linkspeed;

                    mView_HealthStatus.mySpeedTest.uploadtest.isRoaming = mView_HealthStatus.roaming;
                    mView_HealthStatus.mySpeedTest.uploadtest.orig_uspeed = ""+orgband+ "Mbps";

                    mView_HealthStatus.mySpeedTest.uploadtest.uspeed = String.format("%.2f", band) + "Mbps";

                    mView_HealthStatus.mySpeedTest.uploadtest.lat = new GPSTracker(context).getLatitude();
                    mView_HealthStatus.mySpeedTest.uploadtest.lon = new GPSTracker(context).getLongitude();
                    mView_HealthStatus.mySpeedTest.uploadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
                    mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes = maxBuffer*10;
                    mView_HealthStatus.mySpeedTest.uploadtest.startTime = startTimeN;
                    mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS = (long) tt;
                    mView_HealthStatus.mySpeedTest.uploadtest.type = 1;
                    mView_HealthStatus.mySpeedTest.uploadtest.protocol = mView_HealthStatus.connectionType;
                         /*   Intent sendmsg1 = new Intent("speed_result");
                            sendmsg1.putExtra("msg", "2");
                            sendmsg1.putExtra("msgshow", uploadResponse);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg1);*/
                    index++;
                    Intent sendmsg = new Intent("speed_result");
                    sendmsg.putExtra("msg", "1");
                    sendmsg.putExtra("msgshow", String.format("%.2f", band));
                    sendmsg.putExtra("index", String.valueOf(index));
                    System.out.println("index is " + index);
                    sendmsg.putExtra("isLast", false);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg);



                    status = 1;
                    try {
                        JSONObject uploadJsonObj=new JSONObject();
                        uploadJsonObj.put("startdatetime", mView_HealthStatus.mySpeedTest.uploadtest.startTime);
                        uploadJsonObj.put("sizeInBytes", mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes);
                        uploadJsonObj.put("durationTakenInMS", mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS);

                        JSONArray uploadArray=new JSONArray();
                        uploadArray.put(uploadJsonObj);
                        RequestResponse.sendEvent(uploadArray, AllInOneAsyncTaskForNetwork.AsyncTaskPurpose.UPLOAD_EVT,
                                "upload_speed_test");
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                           /* new WebService.Async_SendUporDownloadtestResults().execute(1);
                            new WebService.Async_SendNeighboringCellsInfo().execute();
*/
                    TinyDB db = new TinyDB(Dwnld_upload_fragment.mContext);
                    db.putObject("upload", mView_HealthStatus.mySpeedTest.uploadtest);



                }
            }






            return (true);

        }

        @Override
        public void end() {
            System.out.println("sftp finished");// The process is over
            System.out.println(this.percent); // Progress
            System.out.println("sftp end "+max); // Total filesize
            System.out.println("sftp count "+this.count); // Process in bytes from the total





        }

    }
}

