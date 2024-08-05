package com.newmview.wifi;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.newmview.wifi.activity.mView_UploadDownloadTest;
import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.github.anastr.speedviewlib.PointerSpeedometer;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class UploadToFtpNew {
    public FTPClient mFTPClient = null;
    String host;
    String username;
    String password;
    CopyStreamAdapter streamListener;
    ProgressBar pDialog;
    int status = 0;

    long startTime;
    long endTime;
    public static float bandvalue;
    private Context context;

    public void abortFTP()
    {
        try {
            if (mFTPClient != null)
                mFTPClient.abort();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public int ftpUpload1(String srcFilePath, String desFileName,

                          String desDirectory, String host, String username, String password,
                          final ProgressBar pDialog, final TextView t1, final PointerSpeedometer pointerSpeedometer) {
        this.pDialog = pDialog;

        this.host = host;
        this.username = username;
        this.password = password;
        int port = 21;
        startTime = System.currentTimeMillis();
        mFTPClient = new FTPClient();
        this.context=context;
        status = 1;
        try {

            mFTPClient.connect(host, port); // connecting to the host
            mFTPClient.login(username, password); // Authenticate using username
            // and password
            mFTPClient.changeWorkingDirectory(desDirectory); // change directory
            System.out.println("Dest Directory-->" + desDirectory); // to that
            // directory
            // where image
            // will be
            // uploaded
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream buffIn = null;
            final File file = new File(srcFilePath);

            System.out.println("on going file-->" + srcFilePath);
            buffIn = new BufferedInputStream(new FileInputStream(file), 8192);

            mFTPClient.enterLocalPassiveMode();
            streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred,
                                             int bytesTransferred, long streamSize) {
                    // this method will be called everytime some
                    // bytes are transferred
                    // System.out.println("Stream size" + file.length());
                    // System.out.println("byte transfeedd "
                    // + totalBytesTransferred);

                    try {
                        if (mView_UploadDownloadTest.uploadstatus == 0) {
                            pDialog.setProgress(0);
                            // ((mView_UploadDownloadTest)(mView_UploadDownloadTest.mContext)).sendMessageToActivity(1, "");
                            new Dwnld_upload_fragment().sendMessageToActivity(1,"", "0",null);
                           /* if(context instanceof MainActivity) {
                                ((MainActivity) context).callMessageFunction(1, "");
                            }*/
                            mFTPClient.disconnect();
                            return;
                        }
                    }catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                    int percent = (int) (totalBytesTransferred * 100 / file
                            .length());
                    //progBar.setProgress(Integer.parseInt(o.toString()));
                    pDialog.setProgress(percent);




                    long tt = System.currentTimeMillis() - startTime;

                    float bandInbps = ((totalBytesTransferred)/((tt)/1000));

                    float currentband = bandInbps/(1024);

                    bandvalue=bandInbps/(1024 * 1024);

                    //
                    // t1.setText();

                    String str = String.format("%.2f", currentband)  + "kbps (" + percent + "%)";

                    //((mView_UploadDownloadTest)(mView_UploadDownloadTest.mContext)).sendMessageToActivity(1, String.valueOf(str));




                    ((mView_UploadDownloadTest)(mView_UploadDownloadTest.mContext)).sendMessageToActivity(1, str);
                  /*  if(context instanceof MainActivity) {
                        ((MainActivity) context).callMessageFunction(1, str);
                    }*/
                    if (totalBytesTransferred == file.length()) {
                        System.out.println("100% transfered");

                        removeCopyStreamListener(streamListener);

                        long endtime = System.currentTimeMillis();
                        tt = endtime-startTime;
                        long sz = totalBytesTransferred;
                        bandInbps = ((sz)/((tt)/1000));
                        float band = bandInbps/(1024*1024); //Mbps
                        float szInMB = (float)sz/(1024*1024);
                        float ttInSecs = tt/1000;
                        String uploadResponse = "";
                        if( band < 1)
                        {
                            float kbps = (float)bandInbps/(1024);
                            uploadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec \nFileSize =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", kbps) + "Kbps";
                        }else {
                            uploadResponse = "TimeTaken=" + String.format("%.2f", ttInSecs) + " sec \nFileSize =" + String.format("%.2f", szInMB) + "Mb \nMeasured Speed = " + String.format("%.2f", band) + "Mbps";
                        }
                        //  ((mView_UploadDownloadTest)(mView_UploadDownloadTest.mContext)).sendMessageToActivity(2, uploadResponse);
                          new Dwnld_upload_fragment().sendMessageToActivity(2,uploadResponse, "0",null);
                        /*if (context instanceof MainActivity) {
                            ((MainActivity) context).callMessageFunction(2, uploadResponse);
                        }*/

                        mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
                        mView_HealthStatus.mySpeedTest.uploadtest.isRoaming = mView_HealthStatus.roaming;
                        mView_HealthStatus.mySpeedTest.uploadtest.lat = listenService.gps.getLatitude();
                        mView_HealthStatus.mySpeedTest.uploadtest.lon = listenService.gps.getLongitude();
                        mView_HealthStatus.mySpeedTest.uploadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
                        mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes = totalBytesTransferred;
                      //  mView_HealthStatus.mySpeedTest.uploadtest.startTime = startTime;
                        mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS = tt;
                        mView_HealthStatus.mySpeedTest.uploadtest.type = 1;
                        mView_HealthStatus.mySpeedTest.uploadtest.protocol = mView_HealthStatus.connectionType;
                      //  new WebService.Async_SendUporDownloadtestResults().execute(1);
                        TinyDB db = new TinyDB(mView_UploadDownloadTest.mContext);
                        db.putObject("upload", mView_HealthStatus.mySpeedTest.uploadtest);
                    }

                }

            };
            mFTPClient.setCopyStreamListener(streamListener);

            boolean status1 = mFTPClient.storeFile(desFileName, buffIn);
            if( status1 == true)
                status = 1;
            else
                status = -4;
            System.out.println("Status Value-->" + status);
            buffIn.close();
            mFTPClient.logout();
            mFTPClient.disconnect();
        } catch(FileNotFoundException e1){
            status = -1;
        }
        catch(UnknownHostException e2)
        {
            status = -3;
        }catch (Exception e) {
            status = -2;
            e.printStackTrace();
        }

        return status;
    }
}
