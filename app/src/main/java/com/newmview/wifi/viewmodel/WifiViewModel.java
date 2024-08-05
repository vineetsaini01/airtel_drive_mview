package com.newmview.wifi.viewmodel;

import android.content.Context;
import android.net.wifi.WifiManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.newmview.wifi.bean.WifiModel;
import com.newmview.wifi.repository.WifiListRepository;

import java.util.List;

public class WifiViewModel extends ViewModel {
    private final MutableLiveData<List<WifiModel>> wifiListObservable;


    public WifiViewModel(Context mContext, WifiManager wifiManager) {
        wifiListObservable= WifiListRepository.getInstance().getWifiList(wifiManager);
    }
    public MutableLiveData<List<WifiModel>> getWifiDetailsObservable()
    {
        return wifiListObservable;
    }

    public void refresh(WifiManager wifiManager) {
        WifiListRepository.getInstance().getWifiList(wifiManager);
    }
}
