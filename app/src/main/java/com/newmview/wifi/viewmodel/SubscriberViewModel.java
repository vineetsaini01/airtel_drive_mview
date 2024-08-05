package com.newmview.wifi.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.newmview.wifi.bean.SubscriberModel;
import com.newmview.wifi.repository.SubscriberDetailsRepository;

import java.util.ArrayList;

public class SubscriberViewModel extends ViewModel {
    private  MutableLiveData<ArrayList<SubscriberModel>> subscriberListObservable=new MutableLiveData<>();
    private final MutableLiveData<SubscriberModel>  selectedMarker = new MutableLiveData<SubscriberModel>();
    public SubscriberViewModel() {
        subscriberListObservable= SubscriberDetailsRepository.getInstance().getSubscriberDetails();
    }
    public MutableLiveData<ArrayList<SubscriberModel>> getSubscriberListObservable()
    {
        return subscriberListObservable;
    }

    public void insertNewSubsriber(SubscriberModel subscriberModel) {
        SubscriberDetailsRepository.getInstance().insertSubscriberDetail(subscriberModel);
    }
}
