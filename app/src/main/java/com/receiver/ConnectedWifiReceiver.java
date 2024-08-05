package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.bean.WifiModel;

import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class ConnectedWifiReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectedWifiReceiver";
    private final MutableLiveData<WifiModel> mData = new MutableLiveData<>();

    public MutableLiveData<WifiModel> getData() {
        return mData;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Intent data "+intent.getData());
       mData.setValue(getConnectedWifiDetails());

    }
}
