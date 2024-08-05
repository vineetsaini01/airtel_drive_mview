package com.dashboard.roomdb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.dashboard.roomdb.gagdagent.GagdAgentDao;
import com.dashboard.roomdb.gagdagent.LoggingAgent;
import com.dashboard.roomdb.gagdagent.LoggingAgentDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {DashboardEntity.class}, version = 6, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DashboardDatabase extends RoomDatabase {
    public abstract DashboardDao dashboardDao();
//    public abstract ContactDao contactDao();
//    public abstract GagdAgentDao gagdAgentDao();
//    public abstract LoggingAgentDao loggingAgentDao();



    private static volatile DashboardDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static DashboardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DashboardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    DashboardDatabase.class, "dashboard_database")
//                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Migration MIGRATION_1_2 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Migration logic if needed
            database.execSQL("CREATE TABLE IF NOT EXISTS `gagd_agent` (" +
                    "`id` TEXT NOT NULL, " +
                    "`url` TEXT, " +
                    "`agent_name` TEXT, " +
                    "`event_type` TEXT, " +
                    "`status` TEXT, " +
                    "PRIMARY KEY(`id`)" +
                    ")");
        }
    };
}
