package com.dashboard.viewmodel;



import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dashboard.repository.DashboardRepository;
import com.dashboard.roomdb.DashboardEntity;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {
    private DashboardRepository repository;
    private LiveData<List<DashboardEntity>> allDashboards;

    public DashboardViewModel(Application application) {
        super(application);
        repository = new DashboardRepository(application);
        allDashboards = repository.getAllDashboards();
    }

    public LiveData<List<DashboardEntity>> getAllDashboards() {
        Log.d("TAG", "getAllDashboards:viewmodel "+allDashboards.toString());
        return allDashboards;
    }

    public void fetchDataFromApi() {
        repository.fetchDataFromApi();
    }
}

