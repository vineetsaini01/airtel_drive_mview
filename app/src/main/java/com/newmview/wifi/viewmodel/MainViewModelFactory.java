package com.newmview.wifi.viewmodel;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;



import java.util.ArrayList;
import java.util.HashMap;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private  Bundle bundle;
    //private final Bundle mBundle;
    private  WifiManager mWifiManager;
    private LiveData<ArrayList<HashMap<String, String>>> todListObservable,gtcdListObservable,specialCatObservable;

    private Context mContext;
    private String mSource;
    private String mAction;
   /* public MainViewModel(@NonNull Application application) {
        super(application);
        todListObservable = MainRepository.getInstance().getTodList();
    }*/

    /* public MainViewModelFactory(Context context, String source, String action) {
         mContext = context;
         mSource = source;
         mAction = action;
         gtcdListObservable=MainRepository.getInstance().getGtcdList(context);
         todListObservable = MainRepository.getInstance().getTodList(source, action, context);

         specialCatObservable=  MainRepository.getInstance().getSpecialCatList(context);
         MainRepository.getInstance().getItemsList();

     }*/
    public MainViewModelFactory(Context context, WifiManager wifiManager)
    {
        mContext=context;
        mWifiManager=wifiManager;
    }
public MainViewModelFactory()
{

}
public MainViewModelFactory(Bundle bundle)
{
    this.bundle=bundle;
}

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //     return (T) new MainViewModel(mApplication,mSource,mAction);
        if (modelClass == WifiViewModel.class) {
            WifiViewModel mainViewModel = new WifiViewModel(mContext,mWifiManager );
            return (T) mainViewModel;
        }
        else if(modelClass==MapVM.class)
        {
            MapVM mapVM=new MapVM();
            return (T) mapVM;
        }
        else if(modelClass==TestResultsVM.class)
        {
            TestResultsVM testResultsVM=new TestResultsVM();
            return (T) testResultsVM;
        }
        else if(modelClass==SubscriberViewModel.class)
        {
            SubscriberViewModel subscriberViewModel=new SubscriberViewModel();
            return (T) subscriberViewModel;
        }
        else if(modelClass==HistoryViewModel.class)
        {
            String tag=bundle.getString("tag");
            HistoryViewModel historyViewModel=new HistoryViewModel(tag);
            return (T) historyViewModel;
        }

        return null;
    }


    /*@NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        return (T) new MainViewModel(mApplication,mSource,mAction);

    }*/

    /*  @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return null;
    }*/
}
