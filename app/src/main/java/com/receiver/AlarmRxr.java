package com.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.newmview.wifi.mView_HealthStatus;
import com.newmview.wifi.other.Utils;

import static com.newmview.wifi.application.MviewApplication.batteryList;
import static com.newmview.wifi.application.MviewApplication.timeStamp;

public class AlarmRxr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            int currSize = timeStamp.size();
            if(currSize  == mView_HealthStatus.MaxPeriodicDataToSaveInDB && currSize >= 1)
            { batteryList.remove(0);
            timeStamp.remove(0);
            }
            batteryList.add(Float.valueOf(Utils.getBattery(context)));
            timeStamp.add(Utils.getCurrentHourMin());

        System.out.println("in alarm rxr of listen"  +"battery lsit "+batteryList +"time "+timeStamp);

    }
}
