package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.other.Constants;
import com.services.AllServices;
import com.services.PhoneStateService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by functionapps on 11/28/2018.
 */
// registering it  for receiving intent when the phone is rebooted
public class RebootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = MviewApplication.ctx.getSharedPreferences(Constants.service_toggle, MODE_PRIVATE);
        if(prefs!=null) {
            boolean service_tg = prefs.getBoolean("service_key", false);
            System.out.println("service key is " + service_tg);
           /* if (!service_tg) {
                context.startService(new Intent(context.getApplicationContext(), Background_service.class));
            }*/
       //     context.startActivity(new Intent(context.getApplicationContext(), ImupService.class));

            if(Constants.service_started) {
//                context.startService(new Intent(context.getApplicationContext(), AllServices.class));
            }
            else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
//                Intent serviceIntent = new Intent(context, PhoneStateService.class);
//                context.startService(serviceIntent);
            }
        }

    }
}
