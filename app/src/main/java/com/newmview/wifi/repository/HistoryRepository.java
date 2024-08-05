package com.newmview.wifi.repository;

import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.database.DB_handler;

import java.util.ArrayList;

public class HistoryRepository {
    private static HistoryRepository historyRepository;
    private ArrayList<HistoryModel> resultsList=new ArrayList<>();
    private MutableLiveData<ArrayList<HistoryModel>> mData=new MutableLiveData<>();

    public static HistoryRepository getInstance() {
        if ( historyRepository == null) {
            historyRepository = new HistoryRepository();
        }
        return historyRepository;
    }

    public MutableLiveData<ArrayList<HistoryModel>> getHistory(String tag) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            resultsList = db_handler.readHistory(tag);
           // Collections.reverse(resultsList);
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
    public void insertEnterInHistory(HistoryModel historyModel)
    {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            db_handler.insertTagHistory(historyModel);
            refresh(historyModel.getTag());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db_handler.close();
        }
    }

    private void refresh(String tag) {
        getHistory(tag);
    }
}
