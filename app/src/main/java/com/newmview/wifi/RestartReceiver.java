package com.newmview.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.services.WifiService;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.other.Utils;
import com.services.AllServices;
import com.services.ImupService;


/**
 * Created by functionapps on 11/22/2018.
 */

public class RestartReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
       // Log.e(TAG,"onReceive");
        Toast.makeText(MviewApplication.ctx, "calling listen service from restart", Toast.LENGTH_SHORT).show();
System.out.println("in on restarting service from restart rxr.");
if(!Utils.isMyServiceRunning(listenService.class))
    context.startService(new Intent(context.getApplicationContext(), listenService.class));

//        context.startService(new Intent(context.getApplicationContext(), AllServices.class));

        if(!Utils.isMyServiceRunning(ImupService.class))
        { context.startService(new Intent(context.getApplicationContext(), ImupService.class));

        } if(!Utils.isMyServiceRunning(WifiService.class))
        { context.startService(new Intent(context.getApplicationContext(), WifiService.class));

        }

    }

}
