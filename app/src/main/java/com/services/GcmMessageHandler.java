package com.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mview.airtel.R;
import com.newmview.wifi.activity.TransparentActivity;
import com.newmview.wifi.activity.TransparentImageActivity;
import com.newmview.wifi.activity.VideoPlayer;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GcmMessageHandler extends FirebaseMessagingService {
    public static final String TAG = "GCM Demo";
    String msg, followimgurl, followvideourl, followid, followtitle, followdesc, genralmsg, messagetype;
    Context context;
    NotificationCompat.Builder builder;
    String vr;
    int bcount = 0;
    ArrayList<String> numlist = new ArrayList<>();
    private NotificationManager mNotificationManager;
    private String sendername;
    private String source_name;
    private String pshare;
    private String number;
    private String count;
    private ArrayList<HashMap<String, String>> arraylistfinal;
    private String key_;
    private String GroupTest = "Group_test";
    private String imgurl;
    private String video_path;

    public GcmMessageHandler() {
        super();

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Utils.sendRegistrationToServer(GcmMessageHandler.this,token);



    }
    public void onCreate() {
        super.onCreate();

        arraylistfinal = new ArrayList<HashMap<String, String>>();
        numlist = new ArrayList<String>();
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        System.out.println("message rxcd "+message);
        key_ = message.getData().get("message");
        msg=message.getData().get("msg");
        imgurl=message.getData().get("image_path");
        video_path=message.getData().get("video_path");
        


        if (key_ != null && key_.equalsIgnoreCase("sim_alert"))
        {
sendAlertToUser();


        }
        else if(key_!=null && key_.equalsIgnoreCase("image") )

        {
            openImageAlert();
        }
        else if(key_!=null && key_.equalsIgnoreCase("video") )
        {



openVideoAlert();

        //    new GeneratePictureStyleNotification(this,msg,imgurl).execute();

        }


    }

    private void openVideoAlert() {

        Intent myintent = new Intent(this,VideoPlayer.class);

        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myintent.putExtra("msg", msg);
        myintent.putExtra("key", key_);
        myintent.putExtra("image",imgurl);
        myintent.putExtra("video",video_path);
        startActivity(myintent);

    }

    private void openImageAlert() {

        Intent myintent = new Intent(this, TransparentImageActivity.class);

        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myintent.putExtra("msg", msg);
        myintent.putExtra("key", key_);
        myintent.putExtra("image",imgurl);
        startActivity(myintent);


    }


    private void sendAlertToUser() {


       /* Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notify);
        AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
        r.play();*/
        Intent myintent = new Intent(this, TransparentActivity.class);

        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myintent.putExtra("msg", msg);
        myintent.putExtra("key", key_);
        startActivity(myintent);

    }

    public class GeneratePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public GeneratePictureStyleNotification( Context context,String title,  String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.imageUrl = imageUrl;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
                return null;

        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            System.out.println("bitmap result is "+ result +"context is "+mContext);


           /* Intent intent = new Intent(mContext, MyOpenableActivity.class);
            intent.putExtra("key", "value");
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_ONE_SHOT);*/
if(result!=null) {
    if(Utils.checkContext(mContext)) {

//openNotification(title,result,mContext);
        //openNotification(msg,null,GcmMessageHandler.this);
        int notifyID = 1;
        String CHANNEL_ID = Constants.app_name+"fapps.";// The id of the channel.
        CharSequence name = Constants.app_name;// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        Intent myintent = new Intent(mContext, VideoPlayer.class);
        myintent.putExtra("video_path",video_path);
        myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, iUniqueId,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.pviewicon)
                          .setLargeIcon(result)
                        .setContentTitle(Html.fromHtml("mView"))
                        .setChannelId(CHANNEL_ID)
                       .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result).bigLargeIcon(null)).
                        setContentIntent(contentIntent).
                        setPriority(Notification.PRIORITY_HIGH);



     //   mBuilder.setContentIntent(contentIntent);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

        mNotificationManager.notify(m, mBuilder.build());
        mNotificationManager = (NotificationManager)
            mContext.getSystemService(Context.NOTIFICATION_SERVICE);























      /*  NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myintent = new Intent(mContext, TransparentActivity.class);
        myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, iUniqueId,
                myintent, PendingIntent.FLAG_UPDATE_CURRENT);



        Notification notif = new Notification.Builder(mContext)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setLargeIcon(result)
                .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                .build();
       // notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notif);*/
    }
}
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openNotification(String title, Bitmap result, Context context) {

        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "test";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        Notification notification =
                new NotificationCompat.Builder(GcmMessageHandler.this)
                        .setSmallIcon(R.drawable.pviewicon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setChannelId(CHANNEL_ID).build();



        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(mChannel);

// Issue the notification.
        mNotificationManager.notify(notifyID , notification);

    }


}
















/*
    private void sendNotification(String key, String pshare, String source_name, String followtitle, String followid2, String followvideourl2, String followimgurl2, String followdesc2, String cat_name, String cat_id, String origin_type) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (key != null && key.equalsIgnoreCase("videor")) {

            Intent myintent = new Intent(this, Gcmnotificationclass.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myintent.putExtra("title", msg);
            myintent.putExtra("pShare", pshare);
            myintent.putExtra("id", followid2);
            myintent.putExtra("videourl", followvideourl2);
            myintent.putExtra("imgurl", followimgurl2);
            myintent.putExtra("source_name", source_name);
            myintent.putExtra("amount", "0");
            myintent.putExtra("desc", followdesc2);
            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

            PendingIntent contentIntent = PendingIntent.getActivity(this, iUniqueId,
                    myintent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.appicon)
                            .setContentTitle(Html.fromHtml(msg))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(followdesc2))
                            .setSound(soundUri)
                            .setContentText(Html.fromHtml(followdesc2));

            mBuilder.setContentIntent(contentIntent);
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(m, mBuilder.build());
        } else if (key != null && key.equalsIgnoreCase("follow")) {


            Intent myintent = new Intent(this, MainActivity.class);
            myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            myintent.putExtra("pos", "1000");
            myintent.putExtra("id", cat_id);
            myintent.putExtra("name", cat_name);
            myintent.putExtra("origin_type", origin_type);

            int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
            PendingIntent contentIntent = PendingIntent.getActivity(this, iUniqueId,
                    myintent, PendingIntent.FLAG_UPDATE_CURRENT);


            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.appicon)
                            .setContentTitle(Html.fromHtml(followtitle))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(followdesc2))
                            .setSound(soundUri)
                            .setContentText(Html.fromHtml(followdesc2));

            mBuilder.setContentIntent(contentIntent);
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            mNotificationManager.notify(m, mBuilder.build());


        }


    }
*/


