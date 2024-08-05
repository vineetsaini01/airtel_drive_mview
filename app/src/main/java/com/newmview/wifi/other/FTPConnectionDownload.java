package com.newmview.wifi.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.newmview.wifi.TinyDB;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.AllInOneAsyncTaskForNetwork;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.helper.SFTPConnectionDownload;
import com.newmview.wifi.mView_HealthStatus;

import org.apache.commons.net.io.CopyStreamAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FTPConnectionDownload {

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
    //    private String finalPath;
    private ChannelSftp sftpChannel;
    private long prevTime;
    private long start=0;
    float downloadedFileSize = 0F;
    private long end=0;


    //private CallbackContext callbacks = null;


    public int downloadTask(Context mContext, FileOutputStream f, String finalPath) {
        type = "download";
        this.context = mContext;
        String src = "/home/mview_ftp/download/Delhi.json";

//        this.finalPath=finalPath;
        startTime = System.currentTimeMillis();
        startTimeN = Utils.getDateTime();
        status = 1;




        JSch jsch = new JSch();
        Session session = null;
        try {

            session = jsch.getSession(username, host, 30030);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect();
            session.setTimeout(Constants.CONNECTION_TIMEOUT);

            Channel channel = session.openChannel("sftp");
            channel.connect();
            System.out.println("download file stream "+f);

            sftpChannel = (ChannelSftp) channel;
            sftpChannel.get(src, f, new FTPConnectionDownload.SfProgressMonitor(context));
            sftpChannel.exit();
            session.disconnect();


        }


        catch (JSchException e) {
            e.printStackTrace();
            status=-1;//refused connection
        } catch (SftpException e) {
            e.printStackTrace();//file download failed

        }
        catch(ArithmeticException ae)
        {
            ae.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(session!=null && session.isConnected())
            {
                session.disconnect();
            }
            if(sftpChannel!=null && sftpChannel.isConnected())
            {
                sftpChannel.disconnect();
            }

        }




        return status;


    }


    private class SfProgressMonitor implements SftpProgressMonitor {
        private Context context;

        public SfProgressMonitor(Context context) {
            this.context = context;
        }

        private long max = 0;
        private long count = 0;
        private long percent = 0;
        private long tt = 0;
        float bandInbps =0;

        long endtime = 0;


        public void SftpProgressMonitor() {

        }


        @Override
        public void init(int op, String src, String dest, long max) {
            this.max = max;
            index=0;
            System.out.println("starting");


//            if(dialog!=null && dialog.isShowing())
//            {
//                dialog.dismiss();
//            }
            if(context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        connecting_layout.setVisibility(View.GONE);
                        gauge_layout.setVisibility(View.VISIBLE);
                    }
                });
            }

            startTime=System.currentTimeMillis();
            System.out.println("Download source"+src); // Origin destination
            System.out.println("Download Destination "+dest); // Destination path
            System.out.println("Download file size "+max); // Total filesize
            prevTime=startTime;

        }

        @Override
        public boolean count(long bytes) {

            this.count += bytes;
            long percentNow = (this.count * 100) / max;


            //  System.out.println("type is "+type);
            if (type.equalsIgnoreCase("download")) {

                if (percentNow > this.percent) {
                    this.percent = percentNow;
//                    pDialog.setProgress((int) this.percent);


                    System.out.println("download progress percent" + this.percent); // Progress 0,0


//                    int val1 = (int) ((count * 100) / max);
                    long currentTime=System.currentTimeMillis();

                    float tt = (System.currentTimeMillis() - startTime)/1000F;
//                    long newtt=currentTime-prevTime;

                    System.out.println("" +
                            "download progress count of bytes transferred till now "+this.count  +" time  "+tt +" current "+bytes); // Progress in bytes from the total

                    float bandvalue=0F;
                    try {

//                       bandInbps = ((this.count * 8) / ((tt) / 1000));//bits per se
                        bandInbps = (this.count * 8) ;//bits per se
                        System.out.println("download progress band in bps " + bandInbps);
//                        float sizeofiledownloaded = bandInbps / (1024F * 1024F);
                        float sizeofiledownloaded = bandInbps;
                        bandvalue = sizeofiledownloaded / tt;//Bandwidth in bps
                        bandvalue= (float) Math.round(bandvalue);


                        //this code has to be executed  only if wifi is active
                        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wm.getConnectionInfo();
                        int freq=wifiInfo.getFrequency();
                        int linkspeed=wifiInfo.getLinkSpeed();

                        if (linkspeed<100)
                        {
//                            Log.i("Linkspeed","1 case");

                            bandvalue = bandvalue * 1.5F;
                        }
                        else if (linkspeed>=100&&linkspeed<500)
                        {
//                            Log.i("Linkspeed","2 case");

                            bandvalue=bandvalue*1.5F;
                        }

                        else if (linkspeed>=500)
                        {
//                            Log.i("Linkspeed","3 case");

                            bandvalue=bandvalue*6F;
                        }

//                        Log.i("Freq","Frequency band is "+freq);

                       /* if (freq==2.4) {
                            bandvalue = bandvalue * 1.5F;
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
                            bandvalue = bandvalue * 1.5F;
                        }




*/

                    }
                    catch (ArithmeticException ae)
                    {
                        ae.printStackTrace();
                    }
                    index++;
                    Log.i("Index","From speed class  "+index+" value is "+String.format("%.2f", bandvalue));
//
//                    Intent sendmsg = new Intent("speed_result");
//                    sendmsg.putExtra("msg", "1");
//                    sendmsg.putExtra("msgshow", String.format("%.2f", bandvalue));
////                    sendmsg.putExtra("isLast", false);
//
//                    sendmsg.putExtra("index",String.valueOf(index));
//                    System.out.println("index is "+index);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg);
                }

                if (this.count == max) {
                    endtime = System.currentTimeMillis();
                    float tt = (endtime - startTime)/1000F;

                    long sz = max;//localFile.length();
                    bandInbps = ((sz) / (tt));// Bandwidth in bytes per sec
//                    float band = (bandInbps*8) / (1024F * 1024F); //Mbps
                    float band = (bandInbps*8); //bandwidth in bps
//                    float szInMB = sz / (1024F * 1024F);
//                    float ttInSecs = tt / 1000F;



//                    String downloadResponse = "/" + String.format("%.2f", ttInSecs) + "/" +
//                            String.format("%.2f", szInMB) + "/" + String.format("%.2f", band) + "Mbps";
                    float orgorgband=band;
                    float bw = 0F;
                    String bwUnit = null;
                    if (Config.getApnType(MviewApplication.ctx).equalsIgnoreCase("Wifi")){
                        Utils.appendLog("ELOG_WIFI_COEF: ");

                        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wm.getConnectionInfo();
                        int freq=wifiInfo.getFrequency();

                    /*if (freq==2.4) {
                        band = band * 1.5F;
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
                        band = band * 1.5F;
                    }*/
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
//                        Log.i("Linkspeed","3 case");

                            band=band*6F;
                        }
                        bw = band/(1024F * 1024F);
                        bwUnit = "Mbps";
                    }//this if enclosed code to adjust bandwidth coefficient for wifi
                    else {
                        Utils.appendLog("ELOG_MOBILE_DATA_COEF: "+band);


                        band = band * 2F;

                        if (band > 1000000F){
                            bw = band/(1024F * 1024F);
                            bwUnit = "Mbps";

                        }
                        else if (band > 1000F){
                            bw = band/1024;
                            bwUnit = "Kbps";

                        }
                        else {
                            bw = band;
                            bwUnit = "Bps";

                        }
                    }//this if enclosed code to adjust bandwidth coefficient for Mobile data
                    band = bw;

//                    mView_HealthStatus.mySpeedTest.downloadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
//                    mView_HealthStatus.mySpeedTest.downloadtest.dspeed = String.format("%.2f", band) + bwUnit;
//                    Log.d("TAG", "download speed is "+String.format("%.2f", band) + bwUnit);
//                    mView_HealthStatus.mySpeedTest.downloadtest.orig_dspeed = ""+orgorgband+ bwUnit;
//                    mView_HealthStatus.mySpeedTest.downloadtest.isRoaming = mView_HealthStatus.roaming;
//                    GPSTracker gps=GPSTracker.getGps(MviewApplication.ctx);
//                    mView_HealthStatus.mySpeedTest.downloadtest.lat = gps.getLatitude();
//                    mView_HealthStatus.mySpeedTest.downloadtest.lon = gps.getLongitude();
//                    mView_HealthStatus.mySpeedTest.downloadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
//                    mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes = max;
//                    mView_HealthStatus.mySpeedTest.downloadtest.startTime = startTimeN;
//                    mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS = (long) tt;
//                    mView_HealthStatus.mySpeedTest.downloadtest.type = 2;
//                    mView_HealthStatus.mySpeedTest.downloadtest.protocol = mView_HealthStatus.connectionType;

                    try {
                        JSONObject downloadJsonObj=new JSONObject();

                        downloadJsonObj.put("public_ip", Utils.publicIp());
                        downloadJsonObj.put("private_ip", Utils.getPIP());
                        downloadJsonObj.put("resolved_ip","180.179.214.56");
                        downloadJsonObj.put("latency",Utils.getLatency("180.179.214.56"));
                        downloadJsonObj.put("packet_loss",Utils.getPacketLoss("180.179.214.56"));
                        downloadJsonObj.put("startdatetime", startTimeN);
                        downloadJsonObj.put("dlSizeInBytes", max);
                        downloadJsonObj.put("durationTaken", (long) tt);
                        downloadJsonObj.put("download_speed",  String.format("%.2f", band) + bwUnit);


                        DateTimeFormatter dtf = null;
                        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        LocalDateTime now = LocalDateTime.now();
                        String date_time = now.format(dtf);


                        JSONArray uploadArray=new JSONArray();
                        uploadArray.put(downloadJsonObj);

                        Utils.appendLog("ELOG_FTP_JSON_INSERT_PANEL: is "+uploadArray);
                        DB_handler db_handler = new DB_handler(MviewApplication.ctx);
                        db_handler.open();
                        db_handler.insertInLoggingAgentTable("FTP", "ftp_report", uploadArray.toString(),date_time, "init");
                        db_handler.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    Utils.appendLog("ELOG_BANDWIDTH_BPS: is "+band + bwUnit);
                }
            }


            return (true);

        }

        @Override
        public void end() {

//            System.out.println("finished");// The process is over
//            System.out.println(this.percent); // Progress
//            System.out.println(max); // Total filesize
//            System.out.println(this.count); // Process in bytes from the total

//            bandInbps = ((this.count) / ( mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS));
//            float band = (bandInbps*8) / (1024F * 1024F); //Mbps
//
//            try {
//                JSONObject downloadJsonObj=new JSONObject();
//
//
//                downloadJsonObj.put("startdatetime", mView_HealthStatus.mySpeedTest.downloadtest.startTime);
//                downloadJsonObj.put("dlSizeInBytes", mView_HealthStatus.mySpeedTest.downloadtest.sizeInBytes);
//                downloadJsonObj.put("durationTaken", mView_HealthStatus.mySpeedTest.downloadtest.timeTakenInMS);
//                downloadJsonObj.put("download_speed", mView_HealthStatus.mySpeedTest.downloadtest.dspeed);
//
//
//                DateTimeFormatter dtf = null;
//                dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//                LocalDateTime now = LocalDateTime.now();
//                String date_time = now.format(dtf);
//
//
//                JSONArray uploadArray=new JSONArray();
//                uploadArray.put(downloadJsonObj);
//
//                Utils.appendLog("ELOG_FTP_JSON_INSERT_PANEL: is "+uploadArray);
//                DB_handler db_handler = new DB_handler(MviewApplication.ctx);
//                db_handler.open();
//                db_handler.insertInLoggingAgentTable("FTP", "ftp_report", uploadArray.toString(),date_time, "init");
//                db_handler.close();
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
        }

    }


}
