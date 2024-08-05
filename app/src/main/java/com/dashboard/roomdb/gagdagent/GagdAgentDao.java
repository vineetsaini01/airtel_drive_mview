package com.dashboard.roomdb.gagdagent;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GagdAgentDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(GagdAgent gagdAgent);
//
//    @Query("SELECT * FROM Gagd_Agent WHERE status = :status")
//    List<GagdAgent> getAgentsByStatus(String status);
//
//    @Query("UPDATE Gagd_Agent SET status = :status WHERE id = :id")
//    void updateStatus(String  id, String status);
}