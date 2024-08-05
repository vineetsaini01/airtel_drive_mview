package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReceiverBroadcast extends BroadcastReceiver {

    public static String phoneNumber;
    private  SmsListener mListener;
    public Context context;
    String senderno;

    public interface SmsListener{
        public void messageReceived(String messageText);}

    public void onReceive(Context context, Intent intent) {
        Log.d("SmsReceiver", "Entering smsListner onReceive");
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                String message = null;
                phoneNumber = null;

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    senderno = phoneNumber;
                    message = currentMessage.getDisplayMessageBody();

                }
                String otp = message.replaceAll("\\D+", "");
                Intent in = new Intent("SMS_INTENT");
                in.putExtra("message", message);
                in.putExtra("otp", otp);
                in.putExtra("phno", phoneNumber);


                context.sendBroadcast(in);

            }
            // bundle is null
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    public  void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
