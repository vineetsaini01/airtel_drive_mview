//package com.visionairtel.wifi;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//
//public class OutgoingCallReceiver  extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if(GlobalDefinitions.IsProgramRunning) {
//            //open your activity immediately after a call
//            Intent intent1 = new Intent(context, YOURACTIVITY.class);
//            intent1.setFlags(FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
//            GlobalDefinitions.IsProgramRunning=false;
//        }
//    }
//    public static void Call( Context context, String phoneNumber) {
//
//        if (!phoneNumber.equals("") ) {
//            GlobalDefinitions.IsProgramRunning=true;
//            Intent intent = new Intent(Intent.ACTION_CALL);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse("tel:" + phoneNumber));
//            context.startActivity(intent);
//        }
//    }
//
//}
