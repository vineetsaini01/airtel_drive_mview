package com.newmview.wifi.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.newmview.wifi.bean.TestResults;

import com.newmview.wifi.repository.MapListRepository;
import com.newmview.wifi.repository.TestResultsListRepository;

import java.util.List;

public class TestResultsVM extends ViewModel {

        private final MutableLiveData<List<TestResults>> testResultsObservable;
    private final MutableLiveData<TestResults>  selectedMarker = new MutableLiveData<TestResults>();

    public TestResultsVM() {
            testResultsObservable= TestResultsListRepository.getInstance().getTestResultsList();
        }
        public MutableLiveData<List<TestResults>> getTestResultsObservable()
        {
            return testResultsObservable;
        }
        public boolean removePlan(String id)
        {
            boolean successfullResult=MapListRepository.getInstance().removePlan(id);
            if(successfullResult)
                refresh();
            return successfullResult;
        }
    public MediatorLiveData<List<TestResults>> getTestResultsObservableAtId(int id)
    {
        return TestResultsListRepository.getInstance().getTestResultsAt(id);
    }
        public void refresh() {
            TestResultsListRepository.getInstance().getTestResultsList();
        }

    public MediatorLiveData<TestResults> getTestResultsObservableAtLocation(int id) {
        return TestResultsListRepository.getInstance().getTestResultsAtLocation(id);
    }
    public void selectMarker(TestResults marker) {
        selectedMarker.setValue(marker);
    }
    public LiveData<TestResults> getSelectedMarker() {
        return selectedMarker;
    }

    public void insertNewTestResult(TestResults testResult) {
        TestResultsListRepository.getInstance().addNewTestResult(testResult);
    }

    public void updateTestResults(String mapId, String id, String res, String testId) {
        TestResultsListRepository.getInstance().updateTestResult(mapId,id,res,testId);
    }


}
