package com.dashboard.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DashboardDao {
    @Query("SELECT * FROM dashboard")
    LiveData<List<DashboardEntity>> getAllDashboards();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<DashboardEntity> dashboards);
}

