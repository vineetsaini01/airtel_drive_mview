package com.newmview.wifi.viewmodel;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageViewModel extends ViewModel {

    private  MutableLiveData<String> source=new MutableLiveData<>();
    final MediatorLiveData<String> mData = new MediatorLiveData<>();

    public MutableLiveData<String> childFragmentClicked() {
        return source;
    }
    public void setSource(MutableLiveData<String> source)
    {
        this.source=source;
    }
    public void getSource()
    {
        mData.setValue("HeatMap");
    }

    /*public void setSource(String source) {
        this.source = source;
    }*/
}
