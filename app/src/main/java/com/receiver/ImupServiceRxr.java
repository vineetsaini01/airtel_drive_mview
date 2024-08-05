package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.newmview.wifi.helper.RequestResponse;

public class ImupServiceRxr extends BroadcastReceiver {
    private static final String TAG ="ImupServiceRxr" ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Send imup Request");
//       RequestResponse.sendImupRequest();
//       RequestResponse.sendInitRequest();
    }
}
