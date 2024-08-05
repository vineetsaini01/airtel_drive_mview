package com.newmview.wifi.repository;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.bean.WifiModel;
import com.newmview.wifi.other.WifiConfig;

import java.util.ArrayList;
import java.util.List;

import static com.newmview.wifi.other.WifiConfig.getConnectedWifiDetails;

public class WifiListRepository {
    private static WifiListRepository wifiListRepo;
    final MediatorLiveData<List<WifiModel>> mData = new MediatorLiveData<>();
    private List<ScanResult> mScanResults;
    private static String TAG="WifiListRepository";
    private ArrayList<WifiModel> finalScanResults=new ArrayList<>();

    public static WifiListRepository getInstance() {
        if (wifiListRepo == null) {
            wifiListRepo = new WifiListRepository();
        }
        return wifiListRepo;
    }

    public void addDataSource(WifiManager wifiManager) {
        //mData.addSource(data, mData::setValue);
//        mData.addSource(getWifiList(wifiManager), mData::setValue);
    }

   /* public void removeDataSource(MutableLiveData<List<WifiModel>> data) {
       mData.removeSource(data);

    }*/
    public void removeDataSource(WifiManager wifiManager) {
     //   mData.removeSource(getWifiList(wifiManager));

    }
    public MutableLiveData<List<WifiModel>> getWifiList(WifiManager wifiManager)
    {
        {
            mScanResults = wifiManager.getScanResults();
            Log.i(TAG,"Scan Results from wifimanger are "+mScanResults);
            finalScanResults=new ArrayList<>();
            WifiModel connectedWifi= getConnectedWifiDetails();
            String connectedWifiBSSID="";

            if(connectedWifi!=null)
                connectedWifiBSSID=connectedWifi.getBSSID();

            for(int i=0;i<mScanResults.size();i++)
            {

                WifiModel wifiModel=new WifiModel();

                String ssidName=mScanResults.get(i).SSID;
                String bssid=mScanResults.get(i).BSSID;
                int freq=mScanResults.get(i).frequency;
                int channelNo = WifiConfig.convertFrequencyToChannel(freq);
               // Log.i(TAG,"Connected wifi bssid for "+connectedWifi.getSsidName() +" is "+connectedWifiBSSID +" and for "+ssidName+" is "+bssid);
                int level=mScanResults.get(i).level;
                String finalssidName;
                if(TextUtils.isEmpty(ssidName))
                    finalssidName="Unknown";
                else
                    finalssidName=ssidName;

                if(!TextUtils.isEmpty(connectedWifiBSSID) && !TextUtils.isEmpty(bssid))
                    {
                        if(!connectedWifiBSSID.equalsIgnoreCase(bssid))
                        {
                            wifiModel.setLinkSpeed(0);
                            wifiModel.setIsConnected(false);

                        }
                        else
                        {
                            wifiModel.setLinkSpeed(connectedWifi.getLinkSpeed());
                            wifiModel.setIsConnected(true);
                           wifiModel.setFrequencyBandwidth(connectedWifi.getFrequencyBandwidth());
                           wifiModel.setSupport5GHzBand(wifiManager.is5GHzBandSupported());

                            Log.i(TAG,"Yes BSSID is same for "+finalssidName);
                        }
                        wifiModel.setChannelNo(channelNo+"");
                        wifiModel.setSsidName(finalssidName);
                        wifiModel.setSignalStrength(level);
                        wifiModel.setDetailsForWifi(ssidName + ": " + level + " dbm" +" Ch-"+channelNo);
                        finalScanResults.add(wifiModel);
                    }




            }
            Log.i(TAG, "Scan Results  " + mScanResults);
            mData.setValue(finalScanResults);

        }
        mData.setValue(finalScanResults);
        return mData;
    }
}
