package com.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.newmview.wifi.MyPhoneStateListener;
import com.newmview.wifi.other.Constants;

public class PhoneStateService extends Service {
    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener(PhoneStateService.this, telephonyManager, Constants.readPhoneStatePermissionDenied);
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Restart the listener in case the service is restarted
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }
}