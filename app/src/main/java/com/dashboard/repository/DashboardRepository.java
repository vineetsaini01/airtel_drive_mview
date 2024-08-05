package com.dashboard.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.dashboard.api.ApiService;
import com.dashboard.api.RetrofitBuilder;
import com.dashboard.model.DashboardApiResponse;
import com.dashboard.roomdb.DashboardDao;
import com.dashboard.roomdb.DashboardDatabase;
import com.dashboard.roomdb.DashboardEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardRepository {
    private ApiService apiService;
    private DashboardDao dashboardDao;
    private LiveData<List<DashboardEntity>> allDashboards;

    public DashboardRepository(Application application) {
        apiService = RetrofitBuilder.getClient("http://198.12.250.223:8080/charting_backend_new/").create(ApiService.class);
        DashboardDatabase db = DashboardDatabase.getDatabase(application);
        dashboardDao = db.dashboardDao();
        allDashboards = dashboardDao.getAllDashboards();
        Log.d("TAG", "list allDashboards: "+allDashboards.toString());
    }

    public LiveData<List<DashboardEntity>> getAllDashboards() {
        Log.d("TAG", "getAllDashboards: "+allDashboards);
        return allDashboards;
    }

    public void insert(List<DashboardEntity> dashboards) {
        DashboardDatabase.databaseWriteExecutor.execute(() -> {
            dashboardDao.insertAll(dashboards);
            Log.d("TAG", "insert: dashboard"+dashboards);
        });
    }

    public void fetchDataFromApi() {
        apiService.getDashboardData("getDashboards", "admin").enqueue(new Callback<DashboardApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardApiResponse> call, @NonNull Response<DashboardApiResponse> response) {
                if (response.isSuccessful()) {
                    DashboardApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        Log.d("TAG", "onResponse getDashboardData: " + apiResponse.getDashboardData());

                        insert(apiResponse.getDashboardData());
                    }
                } else {
                    Log.d("TAG", "onResponse: Error is occured while getting response");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardApiResponse> call, Throwable t) {

            }

        });
    }
}
