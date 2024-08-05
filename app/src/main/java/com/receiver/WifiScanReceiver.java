package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.bean.WifiModel;

import java.util.ArrayList;
import java.util.List;

import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class WifiScanReceiver extends BroadcastReceiver {
    private List<ScanResult> mScanResults;
    private WifiManager mWifiManager;
    private final MutableLiveData<List<WifiModel>> mData = new MutableLiveData<>();
    private List<WifiModel> finalScanResults;
private static String TAG="WifiScanReceiver";

    public WifiScanReceiver(WifiManager wifiManager) {
        mWifiManager = wifiManager;
    }
    public MutableLiveData<List<WifiModel>> getData() {
        return mData;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        {
            mScanResults = mWifiManager.getScanResults();
             List<WifiModel> finalScanResults=new ArrayList<>();
           WifiModel connectedWifi= getConnectedWifiDetails();
            for(int i=0;i<mScanResults.size();i++)
            {

                WifiModel wifiModel=new WifiModel();
                if(i==0) {
                    wifiModel.setLinkSpeed(connectedWifi.getLinkSpeed());
                }
                else
                    wifiModel.setLinkSpeed(0);
               wifiModel.setSsidName(mScanResults.get(i).SSID);
               wifiModel.setSignalStrength(mScanResults.get(i).level);
               finalScanResults.add(wifiModel);
            }
             Log.i(TAG, "Scan Results  " + mScanResults);
             mData.setValue(finalScanResults);
             if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                mScanResults = mWifiManager.getScanResults();
//                Log.i(TAG, "Scan Results  " + mScanResults);
               mData.setValue(finalScanResults);

            }
        }
    }
}