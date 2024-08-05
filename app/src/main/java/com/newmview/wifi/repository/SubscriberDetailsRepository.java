package com.newmview.wifi.repository;

import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.SubscriberModel;
import com.newmview.wifi.database.DB_handler;

import java.util.ArrayList;
import java.util.Collections;

public class SubscriberDetailsRepository {
    private static SubscriberDetailsRepository subscriberDetailsRepository;
    private ArrayList<SubscriberModel> resultsList=new ArrayList<>();
    private MutableLiveData<ArrayList<SubscriberModel>> mData=new MutableLiveData<>();

    public static SubscriberDetailsRepository getInstance() {
        if ( subscriberDetailsRepository == null) {
            subscriberDetailsRepository = new SubscriberDetailsRepository();
        }
        return subscriberDetailsRepository;
    }

    public MutableLiveData<ArrayList<SubscriberModel>> getSubscriberDetails() {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            resultsList = db_handler.readSubscriberDetails();
            Collections.reverse(resultsList);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db_handler.close();
        }
        mData.setValue(resultsList);
        return mData;
    }
    public void insertSubscriberDetail(SubscriberModel subscriberModel)
    {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            db_handler.insertSubscriberDetails(subscriberModel);
            refresh();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db_handler.close();
        }
    }

    private void refresh() {
        getSubscriberDetails();
    }
}
