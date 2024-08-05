package com.newmview.wifi.repository;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.database.DB_handler;

import java.util.ArrayList;
import java.util.List;

public class TestResultsListRepository {
    private static final String TAG = "TestResultsRepo";
    private static TestResultsListRepository testResultsListRepository;
    private List<TestResults> resultsList=new ArrayList<>();
    final MediatorLiveData<List<TestResults>> mData = new MediatorLiveData<>();
    final MediatorLiveData<List<TestResults>> resultData = new MediatorLiveData<>();
    final MediatorLiveData<TestResults> markerData = new MediatorLiveData<>();
    public static TestResultsListRepository getInstance() {
        if ( testResultsListRepository == null) {
            testResultsListRepository = new TestResultsListRepository();
        }
        return testResultsListRepository;
    }
    public MutableLiveData<List<TestResults>> getTestResultsList()
    {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            resultsList = db_handler.readTestResults();
            Log.i(TAG,"Results List "+resultsList);
            for(int i=0;i<resultsList.size();i++)
            {
                Log.i(TAG,"X coord"+resultsList.get(i).getX()
                        +" Y coord "+resultsList.get(i).getY()
                        +" test id "+resultsList.get(i).getTestId()
                        +" map id "+resultsList.get(i).getMapId() +" id "+resultsList.get(i).getId());
            }
            db_handler.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mData.setValue(resultsList);
        return mData;
    }

    public MediatorLiveData<List<TestResults>> getTestResultsAt(int id) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        List<TestResults> testResultsList=new ArrayList<>();
        try {
            db_handler.open();
           testResultsList = db_handler.readTestResultsAt(id);
            /*if(testResultsList!=null) {
                Log.i(TAG, "For particular id X coord" + testResults.getX()
                        + " Y coord " + testResults.getY()
                        + " test id " + testResults.getTestId()
                        + " map id " + testResults.getMapId() + " id " + testResults.getId());
            }*/

            db_handler.close();
            resultData.setValue(testResultsList);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return resultData;
    }

    public MediatorLiveData<TestResults> getTestResultsAtLocation(int id) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
       TestResults testResults=new TestResults();
        try {
            db_handler.open();
            testResults = db_handler.readTestResultsAtMarkerLocation(id);
            if(testResults!=null) {
                Log.i(TAG, "For particular id X coord" + testResults.getX()
                        + " Y coord " + testResults.getY()
                        + " test id " + testResults.getTestId()
                        + " map id " + testResults.getMapId() + " id " + testResults.getId());
            }

            db_handler.close();
            markerData.setValue(testResults);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return markerData;
    }

    public void addNewTestResult(TestResults testResult) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        db_handler.insertTestResults(testResult);
        db_handler.close();
    }

    public void updateTestResult(String mapId, String id, String res, String testId) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        db_handler.updateExistingTestResult(mapId, id, res, testId);
        db_handler.close();
    }
}
