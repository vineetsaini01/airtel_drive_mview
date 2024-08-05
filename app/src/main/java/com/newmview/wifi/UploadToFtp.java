package com.newmview.wifi;

import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.newmview.wifi.fragment.Dwnld_upload_fragment;
import com.newmview.wifi.other.Constants;
import com.github.anastr.speedviewlib.PointerSpeedometer;
import com.gpsTracker.GPSTracker;



import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.apache.commons.net.io.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

public class UploadToFtp {
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
    private boolean status1;
    private String uploadResponse;
    private long bytestrnsfrd=0;
    private FileInputStream inputStream;
    private int total;
    private OutputStream outputStream;

    public void abortFTP()
    {
        try {
            if (mFTPClient != null) {
                outputStream.close();


               boolean abort=mFTPClient.abort();

                System.out.println("abort is "+abort);

            }
        }
        catch(Exception e)
        {

            e.printStackTrace();
        }

    }

    public int ftpUpload1(String srcFilePath, String desFileName,

                          String desDirectory, String host, String username, String password,
                          final ProgressBar pDialog, final TextView t1, Context mContext, final PointerSpeedometer pointerSpeedometer) {
        this.pDialog = pDialog;

        this.host = host;
        this.username = username;
        this.password = password;
        int port = 21;
        startTime = System.currentTimeMillis();
        mFTPClient = new FTPClient();
        status = 1;
        context=mContext;
        try {


            mFTPClient.connect(host, port); // connecting to the host
            mFTPClient.login(username, password); // Authenticate using username and password
            mFTPClient.changeWorkingDirectory(desDirectory); // change directory
            System.out.println("Dest Directory-->" + desDirectory); // to that directory where image will be uploaded
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.setConnectTimeout(Constants.CONNECTION_TIMEOUT);

            BufferedInputStream buffIn = null;
            final File file = new File(srcFilePath);


            System.out.println("on going file-->" + srcFilePath);
            buffIn = new BufferedInputStream(new FileInputStream(file), 8192);

            mFTPClient.enterLocalPassiveMode();


            streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred,
                                             int bytesTransferred, long streamSize) {

                    try {
                        // this method will be called everytime some
                        // bytes are transferred
/*

*/
                        System.out.println("bytes transferred " + totalBytesTransferred);
                        int percent = (int) (totalBytesTransferred * 100 / file
                                .length());


                        pDialog.setProgress(percent);

                        long tt = System.currentTimeMillis() - startTime;
                        float bandInbps = ((totalBytesTransferred) / ((tt) / 1000));
                        final float currentband = bandInbps / (1024);

                        final String str = String.format("%.2f", currentband);


                        Intent sendmsg = new Intent("speed_result");
                        sendmsg.putExtra("msg", "1");
                        sendmsg.putExtra("msgshow", str);

                        LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg);
                        if (totalBytesTransferred == file.length()) {
                            System.out.println("100% transfered");

                            removeCopyStreamListener(streamListener);

                            long endtime = System.currentTimeMillis();
                            tt = endtime - startTime;
                            long sz = totalBytesTransferred;
                            bandInbps = ((sz) / ((tt) / 1000));
                            float band = bandInbps / (1024 * 1024); //Mbps
                            float szInMB = (float) sz / (1024 * 1024);
                            float ttInSecs = tt / 1000;
                            // String uploadResponse = "";
                            if (band < 1) {
                                float kbps = bandInbps / (1024);
                                uploadResponse = "/" + String.format("%.2f", ttInSecs) + "/" + String.format("%.2f", szInMB) + "/" + String.format("%.2f", kbps) + "Kbps";

                            } else {
                                uploadResponse = String.format("%.2f", ttInSecs) + String.format("%.2f", szInMB) + String.format("%.2f", band) + "Mbps";

                            }
                            GPSTracker gps=GPSTracker.getGps(context);
                            if(gps!=null)
                            {
                                mView_HealthStatus.mySpeedTest.uploadtest.lat = gps.getLatitude();
                                mView_HealthStatus.mySpeedTest.uploadtest.lon = gps.getLongitude();
                            }


                            mView_HealthStatus.mySpeedTest.uploadtest = mView_HealthStatus.mySpeedTest.new UploadDownload();
                            mView_HealthStatus.mySpeedTest.uploadtest.isRoaming = mView_HealthStatus.roaming;

                            mView_HealthStatus.mySpeedTest.uploadtest.networkType = mView_HealthStatus.iCurrentNetworkState;
                            mView_HealthStatus.mySpeedTest.uploadtest.sizeInBytes = totalBytesTransferred;
                         //   mView_HealthStatus.mySpeedTest.uploadtest.startTime = startTime;
                            mView_HealthStatus.mySpeedTest.uploadtest.timeTakenInMS = tt;
                            mView_HealthStatus.mySpeedTest.uploadtest.type = 1;
                            mView_HealthStatus.mySpeedTest.uploadtest.protocol = mView_HealthStatus.connectionType;
                            Intent sendmsg1=new Intent("speed_result");
                            sendmsg1.putExtra("msg","2");
                            sendmsg1.putExtra("msgshow",uploadResponse);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(sendmsg1);
                            status=1;
                          //  new WebService.Async_SendUporDownloadtestResults().execute(1);
                            TinyDB db = new TinyDB(Dwnld_upload_fragment.mContext);
                            db.putObject("upload", mView_HealthStatus.mySpeedTest.uploadtest);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            };
            mFTPClient.setCopyStreamListener(streamListener);//Set the listener to be used when performing store/retrieve operations.


           // boolean status1 = mFTPClient.storeFile(desFileName, buffIn);
            outputStream = mFTPClient.storeFileStream(desFileName);
            System.out.println("reply code "+mFTPClient.getReplyCode());
            System.out.println("output stream "+outputStream);
            System.out.println("Status Value-->" + status);
            System.out.println("file name"+desFileName);
            Util.copyStream(buffIn, outputStream, mFTPClient.getBufferSize(), file.length(), streamListener);
          ;

                buffIn.close();
                outputStream.close();
            if(mFTPClient.isConnected()) {
                mFTPClient.logout();
                mFTPClient.disconnect();
            }

        } catch(FileNotFoundException e1){
            status = -1;
            e1.printStackTrace();
        } catch (FTPConnectionClosedException e)
        {
            e.printStackTrace();
        } catch(UnknownHostException e2)
        {
            status = -3;
            e2.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();

        } catch (Exception e) {

            status = -2;
            e.printStackTrace();
           /* try {
                System.out.println("reply is "+mFTPClient.getReply());
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/
        }
        finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
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

        return status;
    }

}
