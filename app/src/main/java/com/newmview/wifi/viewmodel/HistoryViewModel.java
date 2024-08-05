package com.newmview.wifi.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.repository.HistoryRepository;

import java.util.ArrayList;

public class HistoryViewModel  extends ViewModel {
    private MutableLiveData<ArrayList<HistoryModel>> historyListObservable=new MutableLiveData<>();
    private final MutableLiveData<HistoryModel>  selectedMarker = new MutableLiveData<HistoryModel>();
    public HistoryViewModel(String tag) {
        historyListObservable= HistoryRepository.getInstance().getHistory(tag);
    }
    public MutableLiveData<ArrayList<HistoryModel>> getHistoryObservable()
    {
        return historyListObservable;
    }

    public void insertNewEntry(HistoryModel historyModel) {
        HistoryRepository.getInstance().insertEnterInHistory(historyModel);
    }
}

