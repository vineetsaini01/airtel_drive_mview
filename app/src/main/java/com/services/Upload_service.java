package com.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.database.DB_handler;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.newmview.wifi.other.Config.downloaded_root;


public class Upload_service extends IntentService {
    private static final String TAG = "UploadService";
    public static boolean UPLOAD_STATUS = false;
    private  DB_handler adp;
    //ArrayList<Videosdata> uploaddataitemslist;
    FileInputStream fileInputStream;
    LocalBroadcastManager manager;
    LocalBroadcastManager meta_data_manager;
    //private String file;
    private int serverResponseCode = 0;
    private int i;
    private String serverResponsemsg;
    private String astring;
    private String countstr = "";
    private String countstr1 = "";
    private String uploadsuccessfull = "notuploaded";
    private String feature;
    private int percentagecal;
    private String lock_flag_set;
    private int uploadCount=0;
    File SDCardRoot = downloaded_root.getAbsoluteFile();
    String filename = Utils.getRandomString() + ".png";
    String path=SDCardRoot+"/"+filename;
    File file = new File(SDCardRoot, filename);
    String SC_PATH=SDCardRoot+"/";
    private Thread.UncaughtExceptionHandler defaultUEH;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            ex.printStackTrace();

            //Same as done in onTaskRemoved()
            PendingIntent service = PendingIntent.getService(
                    getApplicationContext(),
                    1001,
                    new Intent(getApplicationContext(), Upload_service.class),
                    PendingIntent.FLAG_ONE_SHOT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
            System.exit(2);
        }
    };
    private String catname;
    public boolean uploadinit = true;
    private LocalBroadcastManager sharebrodacast;
    private ArrayList<String> numberList, nameList;
    private ArrayList<String> idList;
    private String lockflag;
    private int position;
    private String catnameForGall;
    private boolean cleanuprequest = false;
    private ArrayList<String> profilepicList;
    private ArrayList<String> itemtypeList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    public Upload_service() {
        super("Upload_service");
    }
    @Override
    public void onCreate() {
        manager = LocalBroadcastManager.getInstance(this);
        meta_data_manager = LocalBroadcastManager.getInstance(this);
        sharebrodacast = LocalBroadcastManager.getInstance(this);
        UPLOAD_STATUS = false;
        adp = new DB_handler(MviewApplication.ctx);
        //========Schdeule task after every minute for upload====
        adp.open();

        Timer timer = new Timer();
        TimerTask hourlyTask = new TimerTask() {
            @Override
            public void run() {

                try {
                    uploadMethod();
                } catch (Exception exp) {
                    exp.printStackTrace();

                }
                finally
                {
                    uploadinit=true;
                    UPLOAD_STATUS=false;
                }
            }
        };

        timer.schedule(hourlyTask, 0l, 1000);

    }

    private void uploadMethod() {
        uploadinit =true;
        Log.i(TAG,"Entering uploadMethod");
        if (Config.isNetworkAvailable(Upload_service.this)) {
            Cursor uploaddatacur = adp.selectuploaddata();
            if (uploaddatacur.getCount() > 0) {
                while (uploaddatacur.moveToNext()) {
                    String status = uploaddatacur.getString(uploaddatacur.getColumnIndex("status"));

                    Log.i(TAG,"UploadInit is "+uploadinit);
                    if (status.equals("init")) {
                        if (uploadinit) {
                            Log.i(TAG," UploadInit in init is "+uploadinit);
                            uploadinit =false  ;
                            String file_name =  uploaddatacur.getString(uploaddatacur.getColumnIndex("file_name"));
                            String filesize = uploaddatacur.getString(uploaddatacur.getColumnIndex("file_size"));
                            String url_id =uploaddatacur.getString(uploaddatacur.getColumnIndex("url_id"));
                            Log.i(TAG," url id is"+url_id);
                            String description =uploaddatacur.getString(uploaddatacur.getColumnIndex("comments"));
                            if (filesize.equals("0"))
                            {
                                try {
                                    adp.updateStatusForImageStartNew(file_name,"err",url_id,"");
                                    uploadinit = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    adp.updateStatusForImageStartNew(file_name,"US",url_id,"");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                RequestResponse request = new RequestResponse();
//                                request.uploadContent(file_name, url_id, "image", status, filesize,description);
                            }
                        }
                    }
                    else if (status.equalsIgnoreCase("started")) {
                        Log.i(TAG," entering  started check");
                        if (!UPLOAD_STATUS) {
                            UPLOAD_STATUS = true;
                            uploadsuccessfull = "notuploaded";
                            String startedtitle = null;
                            String startedfilesize = null;
                            String uploadId=null,
                                    category1=null;
                            String sttus1=uploaddatacur.getString(uploaddatacur.getColumnIndex("status"));
                            uploadId = uploaddatacur.getString(uploaddatacur.getColumnIndex("upload_id"));
                            category1 = uploaddatacur.getString(uploaddatacur.getColumnIndex("url_id"));
                            startedtitle = uploaddatacur.getString(uploaddatacur.getColumnIndex("file_name"));
                            startedfilesize =uploaddatacur.getString(uploaddatacur.getColumnIndex("file_size"));
                            final String finalStartedid = uploadId ;
                            final String category2=category1;
                            final String finalStartedtitle =startedtitle;
                            final String finalStartedfilesize = startedfilesize;
//                            RequestResponse a = new RequestResponse();
//                            try {
//                                a.getCountNumber(uploadId, "", "", "");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            //Upload_service.Uploadfile uploadfile=new Upload_service.Uploadfile(finalStartedtitle,finalStartedfilesize, category2,"0",finalStartedid);
                           // uploadfile.run();
                            starteduploadVideo(finalStartedid,finalStartedtitle,"",category2,"","","","","","",finalStartedfilesize,"0");
                        }
                    }
                    else if (Utils.checkifavailable(status) && status.equalsIgnoreCase("US")) {
                        if (!cleanuprequest && uploadinit) {
                            cleanuprequest = true;
                          //  String  category_id = uploaddatacur.getString(uploaddatacur.getColumnIndex("url_id"));
                            // by swapnil bansal 10/27/2022
                            // sendUSrequest(category_id);
                        }

                    }
                    else if (status != null && status.equalsIgnoreCase("uploaded")) {
                        if (!UPLOAD_STATUS && uploadinit) {
                            Log.i(TAG," upload init in status uploaded is "+uploadinit);
                            String category_id =uploaddatacur.getString(uploaddatacur.getColumnIndex("upload_id"));
                            Log.i(TAG," category id uploaded is to remove is"+category_id);
                            adp.removeuploadedcontentupload(category_id);
                        }
                    }
                }
            }
            uploaddatacur.close();

        }



    }

    public String starteduploadVideo(String categoryid, String file, String imsi, String id, String msisdn, String title, String desc, String amount, String mediatype1, String categoryname1, String filesize, String count) {
      //  String count1;
        String fileName = file;
        Log.i(TAG," fileName si "+fileName);
        int pos = fileName.lastIndexOf("/");
        int bytesAvailable1;
        String count1=count;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        byte[] buffer;
        Log.i(TAG," category id is "+categoryid+"id is "+id);
        String boundary = "*****";
        int bytesRead, bufferSize;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(SC_PATH+fileName);
        Log.i(TAG," fileName si is "+sourceFile);

        if (!sourceFile.isFile()) {

            Log.i(TAG," entering sourceFile.isFile() fileName si is "+sourceFile+"count is "+count);
            UPLOAD_STATUS = false;
            uploadsuccessfull = "failed";
            adp.updateStatusForImageStartNew(file, "failed", categoryid,"");



            //Config.callEventUF(id, Upload_service.this, "File Not Found", "ugc", "UF");
            //sendResult("Failed", id, "",categoryid);
            return uploadsuccessfull;
        }
        int fi = Integer.valueOf(filesize);

        int co = Integer.valueOf(count);
        int maxcount1 = fi / maxBufferSize;
        try {

            fileInputStream = new FileInputStream(sourceFile);

        } catch (FileNotFoundException e) {
            UPLOAD_STATUS = false;
            e.printStackTrace();
            uploadsuccessfull = "failed";

            adp.updateStatusForImageStartNew(file, "failed", categoryid,"");
            //adp.updateuploaddata1(categoryid, "failed", id, count);


            //Config.callEventUF(id, Upload_service.this, e.toString(), "ugc", "UF");
            //sendResult("Failed", id, "", categoryid);


            return uploadsuccessfull;

        }

//============Adjusting pointer for streaming after unsuccessfull case====
        for (int adptr = 0; adptr < co; adptr++) {

            Log.i(TAG,"Uploading.......is in process no need to start again");


            try {
                bytesAvailable1 = fileInputStream.available();

                buffer = null;
//                buffer = new byte[bytesAvailable1];
                bufferSize = Math.min(bytesAvailable1, maxBufferSize);

                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            } catch (IOException e) {
                e.printStackTrace();
                UPLOAD_STATUS = false;
                uploadsuccessfull = "notuploaded";

                return uploadsuccessfull;
            }

        }
//====================Streaming of video============================================
        if (!count.equals("end") && Config.isNetworkAvailable(Upload_service.this)) {
            for (; co <= maxcount1; ) {

                Log.i(TAG,"UploadTest Going  count is"+co + "and max count is " + maxcount1+"count is"+count);


                if (co == maxcount1) {
                    count1 = "end";
                   // sendResult("100", id, "", categoryid);


                } else {
                    count1 = String.valueOf(co);
                    System.out.println(" count 1 is "+count1);
                    float percentagecalf = ((float) co) / ((float) maxcount1);

                    float percentag = percentagecalf * 100;
                    percentagecal = Math.round(percentag);

                }
                try {
                    URL url = new URL("http://198.12.250.223/mtantu/uploaddataurlfile");
                    Log.i(TAG," url is "+url);
                    conn = (HttpURLConnection) url.openConnection();
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setChunkedStreamingMode(1024);
                    conn.setConnectTimeout(300000);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Encoding", "");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("myFile", fileName);
                    conn.setRequestProperty("imsi",Utils.getImsi(MviewApplication.ctx));
                    conn.setRequestProperty("description","");
                    conn.setRequestProperty("msisdn",Utils.getMyContactNum(MviewApplication.ctx));
                    conn.setRequestProperty("status", "Unpublish");
                    conn.setRequestProperty("media_type","image");
                    conn.setRequestProperty("category_name","");
                    conn.setRequestProperty("title",fileName);
                    conn.setRequestProperty("id",categoryid);
                    conn.setRequestProperty("amount","0");
                    conn.setRequestProperty("total_size", filesize);
                    conn.setRequestProperty("count", count1);
                    dos = new DataOutputStream(conn.getOutputStream());
                    int mincounttoread = co * maxBufferSize;
//=================Adding imsi=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"imsi\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(imsi);
                    dos.writeBytes(lineEnd);

                    //=================Adding DESC=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(""); // desc is String variable
                    dos.writeBytes(lineEnd);

                    //=================Adding status=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"status\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes("Unpublish"); // msisdn is String variable
                    dos.writeBytes(lineEnd);

                    //=================Adding count=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"count\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(count1); //count
                    dos.writeBytes(lineEnd);


                    //=================Adding Media_type=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"media_type\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes("image"); // msisdn is String variable
                    dos.writeBytes(lineEnd);

                    //=================Adding Total_File_Size=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"total_size\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(filesize); // msisdn is String variable
                    dos.writeBytes(lineEnd);
//=================Adding categoryname=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"category_name\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(""); // msisdn is String variable
                    dos.writeBytes(lineEnd);

                    //=================Adding title=================
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(file); // msisdn is String variable
                    dos.writeBytes(lineEnd);


                    //===============Adding video id========
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(categoryid); // msisdn is String variable
                    dos.writeBytes(lineEnd);

                    //===============Adding amount=======
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"amount\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(""); // msisdn is String variable
                    dos.writeBytes(lineEnd);
//============Adding msisdn==========
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"msisdn\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(Utils.getMyContactNum(MviewApplication.ctx)); // msisdn is String variable
                    dos.writeBytes(lineEnd);

//=============Adding paramter media file(video)============
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + fileName.substring(fileName.lastIndexOf('/') + 1) + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);


                    bytesAvailable1 = fileInputStream.available();


                    bufferSize = Math.min(bytesAvailable1, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    if (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    serverResponseCode = conn.getResponseCode();
                    serverResponsemsg = conn.getResponseMessage();

                    if (serverResponseCode == 200) {


                        StringBuilder sb = null;
                        BufferedReader rd;
                        InputStream is;
                        sb = null;

                        sb = new StringBuilder();
                        try {

                            is = conn.getInputStream();
                            rd = new BufferedReader(new InputStreamReader(is));
                            while ((countstr1 = rd.readLine()) != null) {

                                sb.append(countstr1);
                            }
                            rd.close();

                            countstr = String.valueOf(sb);
                            try {
                                JSONObject jsonObject = new JSONObject(countstr);
                                astring = jsonObject.getString("count");
                                Log.i(TAG,"astring is "+ astring);

                            } catch (Exception e) {
                                e.printStackTrace();
                                //Config.callEventUF(id, this, "Exception catched after reponse code 200", "ugc", "CE");

                                UPLOAD_STATUS = false;
                                uploadsuccessfull = "notuploaded";
                                return uploadsuccessfull;
                            }
                            if (astring.equals("404 page not found")) {
                               // Config.callEventUF(id, this, "Page bot found 4040 after reponse code 200", "ugc", "CE");


                                UPLOAD_STATUS = false;

                                uploadsuccessfull = "notuploaded";
                                return uploadsuccessfull;


                            } else if (astring != null && astring == count && !astring.equals("404 page not found")) {
                                if (astring.equals("end")) {

//                                    adp.updateuploaddata1(category_id, "uploaded", id, "end");
                                   // adp.updatecountandstatus("uploaded", categoryid, id, "end");
                                   // sendResult("100", id, "", categoryid);

                                    UPLOAD_STATUS = false;
                                    uploadsuccessfull = "uploaded";


                                    return uploadsuccessfull;


                                } else {

                                    UPLOAD_STATUS = false;
                                    uploadsuccessfull = "notuploaded";

                                    return uploadsuccessfull;

                                }

                            } else if (Utils.checkifavailable(astring) && !astring.equals("end")) {

                               // adp.updateuploaddata1(categoryid, "started", id, astring);
                                Log.i("UploadTest", "line no 609");
                                try {
                                    co = Integer.valueOf(astring);

                                    Log.i("UploadTest", "line no 612");

//                                    if (co % 5 == 0) {
                                    float percentagecalf = ((float) co) / ((float) maxcount1);

                                    float percentag = percentagecalf * 100;
                                    int percentagecal = Math.round(percentag);


//                                        sendResult(String.valueOf(percentagecal), category_id, ownid, id);
                                   // sendResult(String.valueOf(percentagecal), id, "", categoryid);


//                                    }


                                } catch (NumberFormatException e) {

                                    e.printStackTrace();
                                    UPLOAD_STATUS = false;
                                    uploadsuccessfull = "notuploaded";
                                    return uploadsuccessfull;

                                }

                            } else if (astring.equals("end")) {
                                adp.updateStatusForImageStartNew(fileName,"uploaded","",categoryid);
                               // adp.updateuploaddata1(categoryid, "uploaded", id, astring);

                                //sendResult("100", id, "", categoryid);

                                UPLOAD_STATUS = false;
                                uploadsuccessfull = "uploaded";

                                return uploadsuccessfull;
                            }
                        } catch (IOException ioex) {


                            UPLOAD_STATUS = false;
                            uploadsuccessfull = "notuploaded";

                            return uploadsuccessfull;

                        }

                    } else {

                       // Config.callEventUF(id, this, "Server response code is not 200", "ugc", "CE");

                        uploadsuccessfull = "notuploaded";
                        UPLOAD_STATUS = false;

                        return uploadsuccessfull;


                    }
                } catch (SocketTimeoutException e) {
                   // Config.callEventUF(id, this, "Socket timeoutexception while sending chunks", "ugc", "CE");

                    UPLOAD_STATUS = false;


                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";


                    return uploadsuccessfull;

                } catch (FileNotFoundException e) {

                  //  Config.callEventUF(id, this, "File not found  while sending chunks", "ugc", "CE");


                    UPLOAD_STATUS = false;


                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";

                    return uploadsuccessfull;

                } catch (MalformedURLException e) {
                   // Config.callEventUF(id, this, "MalformedURLException while sending chunks", "ugc", "CE");

                    UPLOAD_STATUS = false;


                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";

                    return uploadsuccessfull;

                } catch (ProtocolException e) {

                  //  Config.callEventUF(id, this, "Protocol not found  while sending chunks", "ugc", "CE");

                    UPLOAD_STATUS = false;


                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";

                    return uploadsuccessfull;

                } catch (IOException e) {
                   // Config.callEventUF(id, this, "IO found  while sending chunks", "ugc", "CE");


                    UPLOAD_STATUS = false;

                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";

                    return uploadsuccessfull;

                } catch (Exception e) {
                    //Config.callEventUF(id, this, "Some other exception   found  while sending chunks and exception is " + e.toString(), "ugc", "CE");

                    UPLOAD_STATUS = false;
                    e.printStackTrace();
                    uploadsuccessfull = "notuploaded";

                    return uploadsuccessfull;

                }


            }


        } else {
            UPLOAD_STATUS = false;
            uploadsuccessfull = "notuploaded";
            return uploadsuccessfull;
        }
        return uploadsuccessfull;

    }

    private class Uploadfile
    {

        String file_name;
        String file_size;
        String count1;
        String upload_id;
        private FileInputStream fileInputStream;
        private int bytesAvailable1;
        private String countstr1;
        private String countstr;
        private String astring;
        private int percentagecal;
        String url_id;
        public Uploadfile(String filename, String filesize, String uploadid, String count,String urlid) {
            // TODO Auto-generated constructor stub
            file_name=filename;
            file_size=filesize;
            count1=count;
            upload_id=uploadid;
            url_id=urlid;

        }

        public String run(){
            //	     int pos = filename.lastIndexOf("/");
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            //logger.debug(" : checking for screenshot file with path "+SC_PATH+file_name);
            File sourceFile = new File(SC_PATH+file_name);
            if(sourceFile.exists())
            {
                System.out.println();
                int fi = Integer.valueOf(file_size);
                int co = Integer.valueOf(count1);
                int maxcount1 = fi / maxBufferSize;
                try {
                    fileInputStream = new FileInputStream(sourceFile);
                    Log.i(TAG,"fileInputStream"+fileInputStream);
                } catch (FileNotFoundException e) {
                    System.out.println(" : via run file not found...");
                    //  logger.error("Exception",e);
                }



                if (!count1.equals("end") ) {
                    for (; co <= maxcount1; ) {
                        if (co == maxcount1) {
                            count1 = "end"; } else {
                            count1 = String.valueOf(co);
                            Log.i(TAG,"count 1 is "+count1);
                            float percentagecalf = ((float) co) / ((float) maxcount1);
                            float percentag = percentagecalf * 100;
                            percentagecal = Math.round(percentag);
                        }
                        try {
                            System.out.println("entering tr catch");
                            try {
                                URL url = new URL("http://198.12.250.223/mtantu/uploaddataurlfile");
//  URL url = new URL("http://10.107.146.130:12202/mtantu/uploaddataurlfile");// URL url = new URL("http://10.107.146.130/mtantu/uploaddataurlfile");
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                conn = (HttpURLConnection) url.openConnection();
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                conn.setUseCaches(false);
                                conn.setChunkedStreamingMode(1024);
                                conn.setConnectTimeout(300000);
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Accept-Encoding", "");
                                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                                conn.setRequestProperty("myFile", file_name);
                                conn.setRequestProperty("imsi", Utils.getImsi(MviewApplication.ctx));
                                conn.setRequestProperty("description", " ");
                                conn.setRequestProperty("msisdn", Utils.getMyContactNum(MviewApplication.ctx));
                                conn.setRequestProperty("status", "Unpublish");
                                conn.setRequestProperty("media_type", "image");
                                conn.setRequestProperty("category_name", " ");
                                conn.setRequestProperty("title", file_name);
                                conn.setRequestProperty("id", url_id);
                                conn.setRequestProperty("amount", "0");
                                conn.setRequestProperty("total_size", file_size);
                                conn.setRequestProperty("count", count1);
                                dos = new DataOutputStream(conn.getOutputStream());
                            }catch (Exception e)
                            {
                                System.out.println(" ecxeption is"+e.getMessage());
                                e.printStackTrace();
                            }
                            System.out.println(" dos is "+dos);

                            //=================Adding imsi=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"imsi\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes("10000000dad1938e");
                            dos.writeBytes(lineEnd);

                            //=================Adding DESC=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(" "); // desc is String variable
                            dos.writeBytes(lineEnd);

                            //=================Adding status=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"status\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes("Unpublish"); // msisdn is String variable
                            dos.writeBytes(lineEnd);

                            //=================Adding count=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"count\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(count1); //count
                            dos.writeBytes(lineEnd);


                            //=================Adding Media_type=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"media_type\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes("image"); // msisdn is String variable
                            dos.writeBytes(lineEnd);

                            //=================Adding Total_File_Size=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"total_size\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(file_size); // msisdn is String variable
                            dos.writeBytes(lineEnd);
                            //=================Adding categoryname=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"category_name\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(" "); // msisdn is String variable
                            dos.writeBytes(lineEnd);

                            //=================Adding title=================
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(file_name); // msisdn is String variable
                            dos.writeBytes(lineEnd);


                            //===============Adding video id========
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(url_id); // msisdn is String variable
                            dos.writeBytes(lineEnd);

                            //===============Adding amount=======
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"amount\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(" "); // msisdn is String variable
                            dos.writeBytes(lineEnd);
                            //============Adding msisdn==========
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"msisdn\"" + lineEnd);
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(Utils.getMyContactNum(MviewApplication.ctx)); // msisdn is String variable
                            dos.writeBytes(lineEnd);

                            //=============Adding paramter media file(video)============
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"myFile\";filename=\"" + file_name + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);

                            System.out.println(" : via run file input stream here next is "+fileInputStream);
                            bytesAvailable1 = fileInputStream.available();
                            System.out.println(" : via run bytes available to write are  "+bytesAvailable1);
                            buffer = new byte[bytesAvailable1];
                            bufferSize = Math.min(bytesAvailable1, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            if (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                            }
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                            int serverResponseCode;
                            String serverResponsemsg;
                            serverResponseCode = conn.getResponseCode();
                            serverResponsemsg = conn.getResponseMessage();
                            if (serverResponseCode == 200) {
                                System.out.println(" entering this check for end ");
                               //adp.updateStatusForImageStartNew(file_name,"uploaded","",upload_id);
                               // UPLOAD_STATUS = false;
                               // uploadsuccessfull = "uploaded";
                                StringBuilder sb = null;
                                BufferedReader rd;
                                InputStream is;
                                sb = null;
                                sb = new StringBuilder();
                                try {
                                    is = conn.getInputStream();
                                    rd = new BufferedReader(new InputStreamReader(is));
                                    while ((countstr1 = rd.readLine()) != null) {
                                        sb.append(countstr1);
                                    }
                                    rd.close();
                                    countstr = String.valueOf(sb);
                                    System.out.println(" : via run UploadScreenshots response is "+countstr);
                                    try {
                                        JSONObject jsonObject = new JSONObject(countstr);
                                        astring = jsonObject.getString("count");
                                        System.out.println("  astring is "+ astring);
                                    } catch (Exception e) {
                                        System.out.println(" EXCEPTION IS"+e.getMessage());
                                        e.printStackTrace();
	                                      UPLOAD_STATUS = false;
	                                      uploadsuccessfull = "notuploaded";
	                                      return uploadsuccessfull;
                                    }
                                    if (astring.equals("404 page not found")) {
                                        UPLOAD_STATUS = false;
                                        uploadsuccessfull = "notuploaded";
                                        return uploadsuccessfull;
                                    } else if (astring != null && astring == count1 && !astring.equals("404 page not found")) {
                                        if (astring.equals("end")) {

                                            UPLOAD_STATUS = false;
                                            uploadsuccessfull = "uploaded";
                                            return uploadsuccessfull;
                                        }
                                        else {

                                            UPLOAD_STATUS = false;
                                            uploadsuccessfull = "notuploaded";
                                            return uploadsuccessfull;

                                        }

                                    } else if (astring != null && !astring.equals("") && !astring.equals("end")) {
                                        try {
                                            co = Integer.valueOf(astring);
                                        } catch (NumberFormatException e) {

                                            UPLOAD_STATUS = false;
                                            uploadsuccessfull = "notuploaded";
                                            return uploadsuccessfull;

                                        }

                                    } else if (astring.equals("end")) {

                                        adp.updateStatusForImageStartNew(file_name,"uploaded","",upload_id);
                                        UPLOAD_STATUS = false;
                                        uploadsuccessfull = "uploaded";
                                        return uploadsuccessfull;
                                        //Object[] params= {SC_PATH+file_name};
                                       // Constants.removeFile(file_name);
                                        //updateStatusOfManualScanfile(url_id, "uploaded");
                                        /*
                                         * uploadinit=true;
                                         * System.out.println("Value of upload init from 11 changed to "
                                         * +uploadinit);
                                         */
                                       // return lineEnd;


                                    }
                                } catch (IOException ioex) {

//
//		                                  UPLOAD_STATUS = false;
//		                                  uploadsuccessfull = "notuploaded";
//
//		                                  return uploadsuccessfull;

                                    /*
                                     * uploadinit=true;
                                     * System.out.println("Value of upload init from 12 changed to "+uploadinit)
                                     * ;
                                     */
                                    return lineEnd;

                                }

                            }
                            else {

//		                              Config.callEventUF(id, this, "Server response code is not 200","ugc");
//
//		                              uploadsuccessfull = "notuploaded";
//		                              UPLOAD_STATUS = false;
//
//		                              return uploadsuccessfull;
                                /*
                                 * uploadinit=true;
                                 * System.out.println("Value of upload init from 13 changed to "+uploadinit);
                                 */
                                return lineEnd;


                            }
                        } catch (SocketTimeoutException e) {
                            System.out.println(Config.getDateTime()+"  : via run Exception occured in upload class in 7... "+e.getMessage());
                        } catch (FileNotFoundException e) {

                        } catch (MalformedURLException e) {
                        } catch (ProtocolException e) {
                            //  System.out.println(helper.Config.getDateTime()+" : via run Exception occured in upload class in 10... "+e.getMessage());
//		                          Config.callEventUF(id, this, "Protocol not found  while sending chunks","ugc");

//		                          UPLOAD_STATUS = false;


                            // logger.error("Exception",e);
                            /*
                             * uploadinit=true;
                             * System.out.println("Value of upload init from 17 changed to "+uploadinit);
                             */
                            return lineEnd;
//		                          uploadsuccessfull = "notuploaded";

//		                          return uploadsuccessfull;

                        } catch (IOException e) {
                        } catch (Exception e) {

                        }
                    }
                }
            }
//		        else
//		        {
//		        	//logger.debug(" : via run Screenshot File doesn't exist for "+file_name );
//
//		        	 updatestatusofmanualscanfile(url_id,"error");
//
//		        }

            return uploadsuccessfull;
        }




    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {

            catname = intent.getStringExtra("catname");
            path = intent.getStringExtra("path");
            lockflag = intent.getStringExtra("lock");
            position = intent.getIntExtra("position", 0);
            feature = intent.getStringExtra("feature");
            catnameForGall = intent.getStringExtra("catnameForGallery");//added by Sonal on 03-06-2020
            numberList = (ArrayList<String>) intent.getSerializableExtra("numberList");
            nameList = (ArrayList<String>) intent.getSerializableExtra("nameList");
            idList = (ArrayList<String>) intent.getSerializableExtra("idList");
            profilepicList=(ArrayList<String>)intent.getSerializableExtra("profilepicList");
            itemtypeList=(ArrayList<String>)intent.getSerializableExtra("itemtypeList");
            Log.i(TAG,"profile list in upload service "+profilepicList);
        }
        return START_STICKY;
    }


    @Override public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        PendingIntent service = PendingIntent.getService(
                getApplicationContext(),
                1001,
                new Intent(getApplicationContext(), Upload_service.class),
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);
    }




}
