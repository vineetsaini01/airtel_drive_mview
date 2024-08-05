package com.dashboard.roomdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(Contact contact);
//
//    @Query("SELECT * FROM contacts WHERE id = :id")
//    LiveData<Contact> getContactById(int id);
//
//    @Query("SELECT * FROM contacts")
//    List<Contact> getAllContactsSync(); // Synchronous query method


    // Add more DAO methods as needed, e.g., update, delete, query operations
}
