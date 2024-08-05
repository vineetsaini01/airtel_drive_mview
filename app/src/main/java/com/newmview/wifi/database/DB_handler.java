package com.newmview.wifi.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.dashboard.roomdb.gagdagent.GagdAgent;
import com.functionapps.mview_sdk2.main.Mview;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.HistoryModel;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.SubscriberModel;
import com.newmview.wifi.bean.TestResults;
import com.newmview.wifi.helper.AllInOneAsyncTaskForEVT;
import com.newmview.wifi.helper.RequestResponse;
import com.newmview.wifi.other.AgentDetailsModel;
import com.newmview.wifi.other.Config;
import com.newmview.wifi.other.Constants;
import com.newmview.wifi.other.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DB_handler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = Config.PRODUCT_VERSION+"mview.db";
    public static final int DATABASE_VERSION = 18;
    private static final String TAG ="DB_handler" ;


    private final Context context;
    private SQLiteDatabase db;



    public DB_handler open() {
        try {
            db = getWritableDatabase();
        } catch (Exception e) {
           // System.out.println("db error in db handler " + e.getMessage());
            //e.printStackTrace();
        }
        return this;
    }

    public DB_handler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, Context context1) {
        super(context, name, factory, version);
        this.context = context1;
       // getReadableDatabase(); // <-- add this, which triggers onCreate/onUpdate
    }
    private static final String CREATE_DASHBOARD="CREATE TABLE DASHBOARD_TABLE " +
            "(dbId INTEGER, " +
            "dbName VARCHAR(256)); ";

    private static final String CREATE_USER_TABLE="CREATE TABLE MSISDN_TABLE " +
            "(msisdn VARCHAR(256),"+
            "circle_name VARCHAR(1000), " +
            "userName  VARCHAR(256), " +
            "userType VARCHAR(100))";

    private static final String CREATE_NUMBER_TABLE = "CREATE TABLE SENDER_NUMBER" +
            "(senderNum VARCHAR(256))";

    private static final String CREATE_TABLE_GAGD_AGENT = "CREATE TABLE Gagd_Agent " +
            "(id int PRIMARY KEY NOT NULL, " +
            "url VARCHAR(10000), " +
            "agentName  VARCHAR(100), " +
            "eventType  VARCHAR(100), " +
            "status VARCHAR(500), " +
            "packetSize  VARCHAR(256), " +
            "totalPackets VARCHAR(100)," +
            "userType VARCHAR(100)," +
            "rule VARCHAR(100)," +
            "mobile VARCHAR(100)," +
            "third_party VAECHAR(100))";



    private static final String CREATE_GRAPHS="CREATE TABLE GRAPHS_TABLE " +
            "(dbId INTEGER, "+
            "graphId INTEGER, " +
            "graphName VARCHAR(256)); ";


    private static final  String CREATE_SIDELIST="CREATE TABLE SIDE_DRAWER "+
            "(id INTEGER, " +
            "Name VARCHAR(256)); ";

    private static final String CREATE_INIT_DATA_TABLE="CREATE TABLE INITUPDATE_TABLE " +
            "(status VARCHAR(500)); ";



    public DB_handler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = ctx;
    }

    // by  swapnil 10/03/2022 for for round robin
    private static final String CREATE_Scheduler_Agents_TABLE = "CREATE TABLE Scheduler_Agents" +
            "(row_id int  PRIMARY KEY, " +
            "device_sno   VARCHAR(1000)," +
            " file_name           VARCHAR(100)," +
            " file_size            VARCHAR(100)," +
            " file_cksum        VARCHAR(500)," +
            " jar_path        VARCHAR(500)," +
            "class_name        VARCHAR(500)," +
            "method_name        VARCHAR(500)," +
            "ssdt       VARCHAR(500)," +
            "sedt        VARCHAR(500)," +
            "period        VARCHAR(500)," +
            "type VARCHAR(500),"+
            "num_of_times        VARCHAR(500)," +
            "tcp_ip_ehernet        VARCHAR(500)," +
            "tcp_ip_wifi        VARCHAR(500)," +
            "tcp_ip_lte        VARCHAR(500)," +
            "tcp_port        VARCHAR(500)," +
            "upload_status        VARCHAR(500)," +
            "usdt        VARCHAR(500)," +
            "agent_version        VARCHAR(500)," +
            "ucdt        VARCHAR(500)," +
            "frequency        VARCHAR(500)," +
            "thread_name        VARCHAR(500)," +
            "running_status        VARCHAR(50000)," +
            "data         VARCHAR(500000)," +
            "scheduled_status       VARCHAR(100)," +
            "priority VARCHAR(100)," +
            "task_type VARCHAR(100)," +
            "last_scheduled_time VARCHAR(100)); ";


    private static final String CREATE_LOGGING_AGENT_TABLE = "CREATE TABLE Logging_Agent" +
            "(agentid_new int  PRIMARY KEY, " +
            "agent_name VARCHAR(10000)  NOT NULL," +
            "  evt_type           VARCHAR(100)," +
            " agent_output           VARCHAR(1000000000)," +
            " date_time        text," +
            " status         VARCHAR(500)); ";


    private static final String CREATE_MANUAL_URL_LIST = "CREATE TABLE manual_url_list " +
            "(id  INT(10000), " +
            "url    VARCHAR(10000) ," +
            "network          VARCHAR(100)," +
            "browser      VARCHAR(1000000000)," +
            "dns_ip        VARCHAR(100)," +
            "i    INT(1000));" ;
    // BY SWAPNIL 10/21/2022 - FOR CREATE_Screenshot_For_HeatMap
    private static final String CREATE_Screenshot_For_HeatMap = "CREATE TABLE Screenshot_For_HeatMap" +
            "(row_id int  PRIMARY KEY, " +
            "path   VARCHAR(1000)," +
            "url_id   VARCHAR(1000)," +
            "file_name           VARCHAR(100)," +
            "date_time           VARCHAR(100)," +
            "category_name        VARCHAR(500)," +
            "upload_id        VARCHAR(500)," +
            "media_type        VARCHAR(500)," +
            "file_size        VARCHAR(500)," +
            "comments        VARCHAR(500)," +
            "count      VARCHAR(500)," +
            "status       VARCHAR(500)); ";
    private static  final String CREATE_HEAT_MAP_TABLE=" CREATE TABLE IF NOT EXISTS Heat_Map_Table"+
            "(map_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "floor_plan_name VARCHAR(100),"+
            "map_name VARCHAR(100),"+
            "final_map_name VARCHAR(100),"+
            "ssid_name VARCHAR(100),"+
            "technology VARCHAR(100),"+
            "flat_type VARCHAR(100),"+
            "opening VARCHAR(100),"+
            "opening_type VARCHAR(100),"+
            "component VARCHAR(100),"+
            "survey VARCHAR(100),"+
            "location_type VARCHAR(100),"+
            "address VARCHAR(1000),"+
            "latitude VARCHAR(100),"+
            "longitude VARCHAR(100),"+
            "device_id VARCHAR(100),"+
            "work_order_id VARCHAR(100),"+
            "date_time VARCHAR(100),"+
            "floor_plan_path VARCHAR(500),"+
            "map_path VARCHAR(500),"+
            "msisdn VARCHAR(100),"+
            "wifi_x VARCHAR(100),"+
            "wifi_y VARCHAR(100),"+
            "ls_walkmap VARCHAR(100),"+
            "ls_walkmap_path VARCHAR(200),"+
            "ls_heatmap VARCHAR(100),"+
            "ls_heatmap_path VARCHAR(200),"+
            "ls_excel_percent VARCHAR(100),"+
            "ls_good_percent VARCHAR(100),"+
            "ls_fair_percent VARCHAR(100),"+
            "ls_poor_percent VARCHAR(100),"+
            "excel_percent VARCHAR(100),"+
            "good_percent VARCHAR(100),"+
            "fair_percent VARCHAR(100),"+
            "poor_percent VARCHAR(100),"+
            "subscriber_id VARCHAR(50),"+
            "subscriber_name VARCHAR(100),"+
            "device_model VARCHAR(100),"+
            "device_make VARCHAR(100)," +
            "floor_length VARCHAR(25),"+
            "floor_width VARCHAR(25),"+
            "sub_device_id VARCHAR(100),"+
            "points VARCHAR(2000),"+
            "walkmap_warning INTEGER DEFAULT 0,"+
            "status VARCHAR(50),"+
            "upload_id VARCHAR(500),"+
            "survey_id_server VARCHAR(500),"+
            "file_size VARCHAR(500),"+
            "count VARCHAR(500),"+
            "final_map_path VARCHAR(500));";
    // BY SWAPNIL 11/25/2022
    private static final String CREATE_IP_LIST_TRACEROUTE = "CREATE TABLE ip_list " +
            "(id  INT(10000), " +
            "ip  VARCHAR(100));" ;

    // BY SWAPNIL 11/25/2022
    private static final String CREATE_IP_LIST_PING = "CREATE TABLE ip_ping_list " +
            "(id  INT(10000), " +
            "ip  VARCHAR(100));" ;
    private static final String CREATE_VIDEO_TEST_TABLE="CREATE TABLE video_test "+
            "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "duration VARCHAR(100),"+
            "buffering VARCHAR(100),"+
            "play_time VARCHAR(100)," +
            "buffering_count VARCHAR(100),"+
            "x_coord VARCHAR(50),"+
            "y_coord VARCHAR(50),"+
            "map_id INTEGER,"+
            "FOREIGN KEY(map_id) REFERENCES Heat_Map_Table(map_id));";
    private static final String CREATE_SUBSCRIBER_DETAILS_TABLE = "CREATE TABLE SUBSCRIBER_DETAILS" +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "subscriber_name VARCHAR(100)," +
            "subscriber_id VARCHAR(50))";
    private static final String CREATE_HISTORY_TABLE = "CREATE TABLE HISTORY" +
            " (id INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "tag VARCHAR(50)," +
            "value VARCHAR(100))";

    private static final String CREATE_TEST_RESULTS_TABLE =
        "CREATE TABLE TEST_RESULTS"+
                "(id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "test_id VARCHAR(10),"+
                "test_results VARCHAR(200),"+
                "x_coord VARCHAR(50),"+
                "y_coord VARCHAR(50),"+
                "map_id INTEGER,"+
                "marker_color VARCHAR(50),"+
                "link_speed VARCHAR(50),"+
                "signal_strength VARCHAR(100));";

    // by swapnil  09/01/2022
    private static final String CREATE_LOGGING_TABLE = "CREATE TABLE Logging" +
            "(agentid_new int  PRIMARY KEY, " +
            "evt_type           VARCHAR(100)," +
            " agent_output           VARCHAR(1000000000)," +
            " date_time        text," +
            " status         VARCHAR(500)); ";

    @Override
    public void onCreate(SQLiteDatabase db) {

        // by swapnil 21/10/2022
        try {
            db.execSQL(CREATE_HISTORY_TABLE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i(TAG,"Exception while creating subscriber table"+e.getStackTrace());
        }
Log.i(TAG,"On Create called...");
try {
    db.execSQL(CREATE_SUBSCRIBER_DETAILS_TABLE);
}
catch (Exception e)
{
    e.printStackTrace();
    Log.i(TAG,"Exception while creating subscriber table"+e.getStackTrace());
}
        try {
            db.execSQL(CREATE_TEST_RESULTS_TABLE);
            Log.i(TAG,"Test Results table created");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i(TAG,"Exception while creating  test results  table "+e.getStackTrace());
        }
        try {
            db.execSQL( CREATE_IP_LIST_TRACEROUTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_IP_LIST_PING);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_HEAT_MAP_TABLE);
            Log.i(TAG,"Creating heat map table");
        } catch (SQLException e) {
            Log.i(TAG,"Error while inserting creating heatmap table "+e.toString());
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_Screenshot_For_HeatMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_DASHBOARD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_TABLE_GAGD_AGENT);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            db.execSQL(CREATE_GRAPHS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            db.execSQL(CREATE_USER_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {

            db.execSQL(CREATE_NUMBER_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL(CREATE_SIDELIST);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_INIT_DATA_TABLE);
        } catch (SQLException e) {
            System.out.println(" exception is"+e.getMessage());
            e.printStackTrace();
        }
// by  swapnil 10/03/2022
        try {
            db.execSQL(CREATE_Scheduler_Agents_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            db.execSQL(CREATE_LOGGING_AGENT_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            db.execSQL(CREATE_MANUAL_URL_LIST);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // BY SWAPNIL BANSAL 09/01/2022
        try {
            db.execSQL(CREATE_LOGGING_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // dropAndCreateDbTable();
        Log.i(TAG,"Old version "+oldVersion +"new version "+newVersion);
        if (oldVersion < 12){
            db.execSQL(CREATE_NUMBER_TABLE);
            db.execSQL("ALTER TABLE  Gagd_Agent ADD COLUMN rule VARCHAR");
            db.execSQL("ALTER TABLE  Gagd_Agent ADD COLUMN mobile VARCHAR");
            db.execSQL("ALTER TABLE  Gagd_Agent ADD COLUMN userType VARCHAR");
            db.execSQL("ALTER TABLE  Gagd_Agent ADD COLUMN third_party VARCHAR");

            Log.d(TAG, "onUpgrade: gagd table column added");

        }
        if(oldVersion<12)
        {
            try
            {
            db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN upload_id VARCHAR");
            db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN survey_id_server VARCHAR");
            db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN file_size VARCHAR");
            db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN count VARCHAR");
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        if(oldVersion<12) {
            try {
                db.execSQL(CREATE_HISTORY_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Exception while creating subscriber table" + e.getStackTrace());
            }
        }
        if(oldVersion<11)
        {
            try {
                db.execSQL(CREATE_SUBSCRIBER_DETAILS_TABLE);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.i(TAG,"Exception while creating subscriber table"+e.getStackTrace());
            }
        }
        if(oldVersion<10)
        {
            try {
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN points VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN walkmap_warning INTEGER DEFAULT 0");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
            else if(oldVersion<9)
        {
            try {
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN subscriber_id VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN subscriber_name VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN model_number VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN device_make VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN device_model VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN sub_device_id VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN floor_length VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN floor_width VARCHAR");

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
      else  if(oldVersion<8)
        {
            try {
                db.execSQL(CREATE_TEST_RESULTS_TABLE);
                Log.i(TAG,"Test Results table created");
            } catch (SQLException e) {
                e.printStackTrace();
                Log.i(TAG,"Exception while creating  test results  table "+e.getStackTrace());
            }
           /* try {
                db.execSQL(CREATE_VIDEO_TEST_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
                Log.i(TAG,"Eception while creating video test table "+e.getStackTrace());
            }*/

        }
       else if(oldVersion<7)
        {
            // by swapnil 11/25/2022
            try {
                db.execSQL( CREATE_IP_LIST_TRACEROUTE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(CREATE_IP_LIST_PING);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
       else if(oldVersion<6)
        {
            try {
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_walkmap VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_walkmap_path VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_heatmap VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_heatmap_path VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_excel_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_good_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_fair_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN ls_poor_percent VARCHAR");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }

      else  if(oldVersion<5)
        {
            try {
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN excel_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN good_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN fair_percent VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN poor_percent VARCHAR");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
       else if(oldVersion<4)
        {
            try {
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN wifi_x VARCHAR");
                db.execSQL("ALTER TABLE  Heat_Map_Table ADD COLUMN wifi_y VARCHAR");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        else
       if(oldVersion<3)
        {
            try {
                db.execSQL(CREATE_HEAT_MAP_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }









    private void dropAndCreateDbTable() {

        db.delete("DASHBOARD_TABLE", null, null);
        db.execSQL(CREATE_DASHBOARD);

    }
    public void insertUserMsisdn(String num,String name,String userName,String userType) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("msisdn", num);
        contentValues.put("circle_name", name);
        contentValues.put("userName", userName);
        contentValues.put("userType", userType);
        db.insert("MSISDN_TABLE", null, contentValues);
        System.out.println("inserted in insertUserMsisdn  " + contentValues);

    }

    public void insertSenderNumber(String num) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("senderNum", num);
        db.insert("SENDER_NUMBER", null, contentValues);
        System.out.println("inserted in insertUserMsisdn  " + contentValues);

    }


    public void insertTagHistory(HistoryModel historyModel)
    {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("tag", historyModel.getTag());
            contentValues.put("value", historyModel.getValue());
            // contentValues.put("id",subscriberDetails.getId());
            long i=db.insert("HISTORY", null, contentValues);
            Log.i(TAG,"Data inserted in HISTORY for tag "+historyModel.getTag() +" at i = "+i+"");
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception occured while inserting new sub entry in HISTORY "+e.getStackTrace());
            e.printStackTrace();
        }
    }

    public void insertSubscriberDetails(SubscriberModel subscriberDetails)
    {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("subscriber_name", subscriberDetails.getSubscriberName());
            contentValues.put("subscriber_id", subscriberDetails.getSubscriberId());
           // contentValues.put("id",subscriberDetails.getId());
            db.insert("SUBSCRIBER_DETAILS", null, contentValues);
            Log.i(TAG,"Data inserted in SUBSCRIBER_DETAILS");
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception occured while inserting new sub entry in SUBSCRIBER_DETAILS "+e.getStackTrace());
            e.printStackTrace();
        }
    }
    public void insertTestResults(TestResults testResult) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("test_id", testResult.getTestId());
            contentValues.put("test_results", testResult.getResult());
            contentValues.put("x_coord", testResult.getX());
            contentValues.put("y_coord", testResult.getY());
            contentValues.put("map_id",testResult.getMapId());
            contentValues.put("marker_color",testResult.getColor());
            // contentValues.put();
            db.insert("TEST_RESULTS", null, contentValues);
            Log.i(TAG,"Data inserted in TEST_RESULTS inserted");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void insertGagdAgent(String id, String url, String agentName, String eventType, String status, String packet_size, String no_of_packets, String user_type, String rule, String mobile, String third_party_mobile) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("url", url);
        values.put("agentName", agentName);
        values.put("eventType", eventType);
        values.put("packetSize", packet_size);
        values.put("totalPackets", no_of_packets);
        values.put("status", status);
        values.put("userType", user_type);
        values.put("rule", rule);
        values.put("mobile", mobile);
        values.put("third_party",third_party_mobile);


        db.insert("Gagd_Agent", null, values);
        Log.i(TAG,"Data inserted in GAGD AGENT");

    }

    public String getLastInsertedIdVolteCall() {
        String  lastId = null;
        Cursor cursor = null;
        try {
            // Query to get the last inserted ID with agentName 'VolteCall'
            String query = "SELECT id FROM Gagd_Agent WHERE agentName = 'VolteCall' ORDER BY ROWID DESC LIMIT 1";
            cursor = db.rawQuery(query, null);

            // Check if the cursor contains a result and move to the first row
            if (cursor != null && cursor.moveToFirst()) {
                lastId = cursor.getString(cursor.getColumnIndex("id"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching the last inserted ID with agentName 'VolteCall'", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lastId;
    }

    public String getLastInsertedUserType() {
        String  lastId = null;
        Cursor cursor = null;
        try {
            // Query to get the last inserted ID with agentName 'VolteCall'
            String query = "SELECT userType FROM Gagd_Agent WHERE agentName = 'VolteCall' ORDER BY ROWID DESC LIMIT 1";
            cursor = db.rawQuery(query, null);

            // Check if the cursor contains a result and move to the first row
            if (cursor != null && cursor.moveToFirst()) {
                lastId = cursor.getString(cursor.getColumnIndex("userType"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while fetching the last inserted ID with agentName 'VolteCall'", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return lastId;
    }

    public List<GagdAgent> getAgentsByNameAndStatus(String name, String status) {
        List<GagdAgent> agents = new ArrayList<>();

        String query = "SELECT * FROM Gagd_Agent WHERE agentName = ? AND status = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name, status});

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            String agentName = cursor.getString(cursor.getColumnIndexOrThrow("agentName"));
            String eventType = cursor.getString(cursor.getColumnIndexOrThrow("eventType"));
            String agentStatus = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            String packetSize = cursor.getString(cursor.getColumnIndexOrThrow("packetSize"));
            String totalPackets = cursor.getString(cursor.getColumnIndexOrThrow("totalPackets"));
            String userType = cursor.getString(cursor.getColumnIndexOrThrow("userType"));
            String rule = cursor.getString(cursor.getColumnIndexOrThrow("rule"));
            String mobile = cursor.getString(cursor.getColumnIndexOrThrow("mobile"));
            String third_party = cursor.getString(cursor.getColumnIndexOrThrow("third_party"));

            GagdAgent agent = new GagdAgent(id, url, agentName, eventType, agentStatus, packetSize, totalPackets,userType,rule,mobile,third_party);
            agents.add(agent);
        }

        cursor.close();
        return agents;
    }


    public void updateAgentStatus(String id, String newStatus) {
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        db.update("Gagd_Agent", values, "id = ?", new String[]{id});
    }

    public void updateAllAgentStatusToDeleted(String newStatus, String agentName) {
        ContentValues values = new ContentValues();
        values.put("status", newStatus);

        // Update rows where agent_name matches the specified value
        String selection = "agentName = ?";
        String[] selectionArgs = { agentName };

        db.update("Gagd_Agent", values, selection, selectionArgs);
    }
    public void insertTestResults(String result, int testId,String mapId,int color, float x_coord, float y_coord) {
     try {
         ContentValues contentValues = new ContentValues();
         contentValues.put("test_id", testId);
         contentValues.put("test_results", result);
         contentValues.put("x_coord", x_coord);
         contentValues.put("y_coord", y_coord);
         contentValues.put("map_id",mapId);
         contentValues.put("marker_color",color);
        // contentValues.put();
         db.insert("TEST_RESULTS", null, contentValues);
         Log.i(TAG,"Data inserted in TEST_RESULTS inserted");
     }
     catch (Exception e)
     {
         e.printStackTrace();
     }
    }
    public boolean insertIPDetails(String ip) {
        try {
            System.out.println(" insertUrlDetailsInDb  ");
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ip", ip);
            long result1 = db.insert("ip_list", null, contentValues);
            System.out.println(" the result is in insertIPDetails " + result1);
            if (result1 == -1) {
                return false;
            } else {
                System.out.println("true in logging agent");
                return true;
            }
        }
        catch (Exception e) {
            System.out.println("db error in inserting " + e.toString());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return false;

    }
    public void insertInitStatusData(String status1) {
        try {
            ContentValues contentValues1 = new ContentValues();
            contentValues1.put("status",status1);
            db.insert("INITUPDATE_TABLE",null,contentValues1);
            db.close();
            System.out.println("inserted in init data is " +contentValues1);
        }
        catch (Exception e)
        {
            System.out.println("db error in inserting "+e.getMessage());
            e.printStackTrace();
        }
    }
    public long insertMapData(MapModel mapModel) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ssid_name", mapModel.getSsidName());
            contentValues.put("floor_plan_name",mapModel.getFloorPlan());
            contentValues.put("final_map_name",mapModel.getFinalMapName());
            contentValues.put("technology", mapModel.getTechnology());
            contentValues.put("flat_type", mapModel.getFlatType());
            contentValues.put("opening", mapModel.getOpening());
            contentValues.put("opening_type", mapModel.getOpeningType());
            contentValues.put("component", mapModel.getComponent());
            contentValues.put("survey", mapModel.getSurveyFor());
            contentValues.put("location_type", mapModel.getLocationType());
            contentValues.put("address", mapModel.getAddress());
            contentValues.put("latitude", mapModel.getLatitude());
            contentValues.put("longitude", mapModel.getLongitude());
            contentValues.put("device_id", mapModel.getDeviceId());
            contentValues.put("work_order_id", mapModel.getWorkOrderId());
            contentValues.put("map_path",mapModel.getMapPath());
            contentValues.put("map_name","");
            contentValues.put("floor_plan_path",mapModel.getFloorPlanPath());
            contentValues.put("final_map_path",mapModel.getFinalMapPath());
            contentValues.put("msisdn",Utils.getMyContactNum(MviewApplication.ctx));
            contentValues.put("date_time", Utils.getDateTime());
            contentValues.put("wifi_x",mapModel.getWifiX());
            contentValues.put("wifi_y",mapModel.getWifiY());
            contentValues.put("latitude",mapModel.getLatitude());
            contentValues.put("longitude",mapModel.getLongitude());
            contentValues.put("subscriber_name",mapModel.getSubscriberName());
            contentValues.put("subscriber_id",mapModel.getSubscriberId());

            long insert= db.insert("Heat_Map_Table", null, contentValues);
            Log.i(TAG,"HeatMap entry inserted "+insert +" with name "+mapModel.getSubscriberName() +" id "+mapModel.getSubscriberId() +" lat "+mapModel.getLatitude());
            return insert;
        }
        catch (Exception e)
        {
            Log.i(TAG,"Esceotion in HeatMap entry insertion "+e.toString());
           // Utils.appendCrashLog("Exception  while saving floor map in table"+e.toString());
            e.printStackTrace();
            return -1;
        }

    }

    public int insertMapData(String ssidName, String floorPlanName,
                             String tech, String flatType, String opening,
                             String openingType, String component, String surveyFor,
                             String locationType, String address,
                             String latitude, String longitude, String deviceId,
                             String workOrderId, String mapPath, String finalMapNmae,
                             String final_map_path, String msisdn, String floorPlanPath, float wifiCoordsX, float wifiCoordsY) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ssid_name", ssidName);
            contentValues.put("floor_plan_name",floorPlanName);
            contentValues.put("final_map_name",finalMapNmae);
            contentValues.put("technology", tech);
            Log.i(TAG,"Inserting flat type as "+flatType);
            contentValues.put("flat_type", flatType);
            contentValues.put("opening", opening);
            contentValues.put("opening_type", openingType);
            contentValues.put("component", component);
            contentValues.put("survey", surveyFor);
            contentValues.put("location_type", locationType);
            contentValues.put("address", address);
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);
            contentValues.put("device_id", deviceId);
            contentValues.put("work_order_id", workOrderId);
            contentValues.put("map_path",mapPath);
            contentValues.put("map_name","");
            contentValues.put("floor_plan_path",floorPlanPath);
            contentValues.put("final_map_path",final_map_path);
            contentValues.put("msisdn",msisdn);
            contentValues.put("date_time", Utils.getDateTime());
            contentValues.put("wifi_x",wifiCoordsX);
            contentValues.put("wifi_y",wifiCoordsY);

           long insert= db.insert("Heat_Map_Table", null, contentValues);
           Log.i(TAG,"HeatMap entry inserted");
        }
        catch (Exception e)
        {
            Log.i(TAG,"Esceotion in HeatMap entry insertion "+e.toString());
           // Utils.appendCrashLog("Exception  while saving floor map in table"+e.toString());
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void insertDashboardsNames(String graphId, String graphName) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("dbId", graphId);
            contentValues.put("dbName", graphName);
            db.insert("DASHBOARD_TABLE", null, contentValues);
            System.out.println("dashboard inserted " + contentValues);
        }
        catch (Exception e)
        {
            System.out.println("db error in inserting "+e.toString());
            e.printStackTrace();
        }
    }
    public void insertSideDrawerItems(String id,String name) {
        ContentValues contentValues=new ContentValues();
        contentValues.put("id",id);
        contentValues.put("Name",name);
        db.insert("SIDE_DRAWER", null, contentValues);
    }
    public void insertGraphsNames(String graphId, String graphName,String dbId) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("graphId", graphId);
            contentValues.put("graphName", graphName);
            contentValues.put("dbId",dbId);
            db.insert("GRAPHS_TABLE", null, contentValues);
            System.out.println("inserted " + contentValues);
        }
        catch (Exception e)
        {
            System.out.println("db error in inserting "+e.toString());
            e.printStackTrace();
        }
    }
    public Cursor getGraphSList(String dbId) {
        return db.rawQuery("SELECT * FROM GRAPHS_TABLE WHERE dbId='" + dbId + "'", null);
    }
    public Cursor getSideList()
    {
        return db.rawQuery("SELECT * FROM SIDE_DRAWER  ", null);
    }
    public Cursor getDbList()
    {
        return db.rawQuery("SELECT * FROM DASHBOARD_TABLE ", null);
    }
    public void dropAndCreateGraphsTable() {

        // db.execSQL("DROP TABLE IF EXISTS REPORT_ROWCOL_NAME_TABLE");
    }

    public Cursor getUserNum() {

        return db.rawQuery("SELECT msisdn FROM MSISDN_TABLE ", null);
    }

    public Cursor getSenderNum() {

        return db.rawQuery("SELECT senderNum FROM SENDER_NUMBER ", null);
    }


    public void updateUserNumber( String newMsisdn) {
        ContentValues values = new ContentValues();
        values.put("msisdn", newMsisdn);
        db.update("MSISDN_TABLE", values, null, null);

    }

    public void updateSenderNumber( String newMsisdn) {
        ContentValues values = new ContentValues();
        values.put("senderNum", newMsisdn);
        db.update("SENDER_NUMBER", values, null, null);

    }
    public String getDeviceId() {
        String device_id = null;

        String sql = "SELECT * FROM Heat_Map_Table";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {

            device_id=c.getString(c.getColumnIndexOrThrow("device_id"));

            Log.d(Mview.TAG,"device_id  from db is " + device_id);
        }


        return device_id;
    }
    public Cursor getCircleName() {

        return db.rawQuery("SELECT circle_name FROM MSISDN_TABLE ", null);
    }
    public Cursor getUserName() {

        return db.rawQuery("SELECT userName FROM MSISDN_TABLE ", null);
    }
    public Cursor getUserType() {

        return db.rawQuery("SELECT userType FROM MSISDN_TABLE ", null);
    }

    public Cursor selectuploaddata() {
        return db.rawQuery("SELECT * FROM Screenshot_For_HeatMap", null);

    }
    public ArrayList<HashMap<String, String>> selectIPPingdetails() {

        ArrayList<HashMap<String,String>> detailsList=new ArrayList<>();
        System.out.println("Detail list is "+detailsList);
        try {
            open();
            String sql = "SELECT  * FROM  ip_ping_list";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            if (rs != null) { }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                String   ipDetails = rs.getString(rs.getColumnIndex("ip"));
                System.out.println(" ipDetails  is " +ipDetails);
                hashMap.put("ip", ipDetails);
                detailsList.add(hashMap);
            }
        } catch (Exception e) {
            System.out.println(": via selectUrlName() is " + e.getMessage());
            e.printStackTrace();

        }

        return detailsList;


    }
    public ArrayList<HashMap<String, String>> selectIPdetails() {

        ArrayList<HashMap<String,String>> detailsList=new ArrayList<>();
        System.out.println("Detail list is "+detailsList);
        try {
            open();
            String sql = "SELECT  * FROM  ip_list";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            if (rs != null) { }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                String   ipDetails = rs.getString(rs.getColumnIndex("ip"));
                System.out.println(" ipDetails  is " +ipDetails);
                hashMap.put("ip", ipDetails);
                detailsList.add(hashMap);
            }
        } catch (Exception e) {
            System.out.println(": via selectUrlName() is " + e.getMessage());
            e.printStackTrace();

        }

        return detailsList;


    }
    public Cursor selectDashboardsData() {
        return db.rawQuery("SELECT * FROM DASHBOARD_TABLE",null);
    }
    public  void updateStatusForImageStartNew(String filename, String status,String url_id,String upload_id) {


        try {
            open();
            ContentValues initialValues = new ContentValues();
            initialValues.put("status",status);
            initialValues.put("url_id", url_id);
            initialValues.put("upload_id",upload_id);
            db.update("Screenshot_For_HeatMap", initialValues, "file_name='" + filename + "'", null);
            Log.i(TAG,"updateAgentDetails updated are  " + initialValues);
            close();
        } catch (Exception e) {
            Log.i(TAG," error in updateAgentDetails is" + e.getMessage());
            e.printStackTrace();
        }




    }

    public void upDateDashboardData(String dbid, String dbName) {

        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("dbId", dbid);
            initialValues.put("dbName",dbName);
            db.update("DASHBOARD_TABLE", initialValues, "dbId=" + dbid, null);
            System.out.println("dashboard updated "+initialValues);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Cursor getAllDataForMultiFilterTable(String tablename) {
        return db.rawQuery("SELECT * FROM "+tablename, null);
    }

    public Cursor getMultiFilterData(String prevId,String table) {
        try {

            return db.rawQuery("SELECT * FROM " + table + " WHERE previousId='" + prevId + "'", null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public void updateGraphData(String graphId, String graphName, String dbid) {

        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put("graphId", graphId);
            initialValues.put("graphName",graphName);
            initialValues.put("dbId",dbid);
            db.update("GRAPHS_TABLE", initialValues, "dbId=" + dbid, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Cursor selectInitStatusData() {
        open();
        String res = "SELECT  * FROM " + "INITUPDATE_TABLE" ;
        System.out.println(" res is "+res);
        return db.rawQuery(res, null);


    }

    public void updateInitData(String status)
    {

        String sql = "UPDATE INITUPDATE_TABLE  SET status='"+status+"'";
        System.out.println("updated value is " + sql);
        try {
            db.execSQL(sql);
        } catch (Exception e) {;
        e.printStackTrace();
        }
    }

    // DONE BY  swapnil   for round robin part
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateScheduledTime(String last_scheduled_time, String fileName) {
        String sql = null;
        sql = "UPDATE Scheduler_Agents SET last_scheduled_time='" + last_scheduled_time
                + "' WHERE file_name='" + fileName + "'";


        try {
            db.execSQL(sql);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }


    // DONE BY  swapnil   for round robin part
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<AgentDetailsModel> getAgentsWithCompletedAndReadyStatusIs() {
        ArrayList<AgentDetailsModel> agentsDetailsList =  new ArrayList<>();
        String query = "SELECT * FROM  Scheduler_Agents WHERE upload_status = 'completed' AND scheduled_status = 'ready'"+ " ORDER BY priority";
        Cursor rs = db.rawQuery(query, null);

        if (rs != null) {

        }
        while (rs.moveToNext()) {
            System.out.println(Config.getDateTime() + " : via getUrlsWithCompletedAndReadyStatus  In try method of  select and run jar method of db to run jar if completed");
            String filename = " ", filesize, file_path = null, tcp_ip_eth, tcp_ip_wifi, tcp_port, file_checksum,
                    ssdt = " ", sedt = " ", noofiteration = " ", period = null, agent_type = "", classname = null,
                    m_name = null, jar_name = null, jar_size = null, status, agent_version = null,
                    currentiteration = "", frequnecy = null, running_state = null, thread_name, upload_status = " ",scheduled_status=" ";
            AgentDetailsModel agentsDetails = new AgentDetailsModel();
            filename = rs.getString(rs.getColumnIndex("file_name"));
            filesize = rs.getString(rs.getColumnIndex("file_size"));
            System.out.println(" : the file size is  " + filesize);
            file_path = rs.getString(rs.getColumnIndex("jar_path"));
            System.out.println(" : the file path is  " + file_path);
            tcp_ip_eth = rs.getString(rs.getColumnIndex("tcp_ip_ehernet"));
            System.out.println(" : the tcp_ip_eth is  " + tcp_ip_eth);
            tcp_ip_wifi = rs.getString(rs.getColumnIndex("tcp_ip_wifi"));
            System.out.println(" : the tcp_ip_wifi  " + tcp_ip_wifi);
            tcp_port = rs.getString(rs.getColumnIndex("tcp_port"));
            file_checksum = rs.getString(rs.getColumnIndex("file_cksum"));
            ssdt = rs.getString(rs.getColumnIndex("ssdt"));
            System.out.println(" : the ssdt is  " + ssdt);
            sedt = rs.getString(rs.getColumnIndex("sedt"));
            noofiteration = rs.getString(rs.getColumnIndex("num_of_times"));
            System.out.println(" : the noofiteration is  " + noofiteration);
            period = rs.getString(rs.getColumnIndex("period"));
            System.out.println(" : the priod is  " + period);
            System.out.println(" : the agent type is   " + agent_type);
            classname = rs.getString(rs.getColumnIndex("class_name"));
            m_name = rs.getString(rs.getColumnIndex("method_name"));
            jar_name = rs.getString(rs.getColumnIndex("file_name"));
            jar_size = rs.getString(rs.getColumnIndex("file_size"));
            status = rs.getString(rs.getColumnIndex("upload_status"));
            System.out.println(" uplaod status is getAgentsWithCompletedAndReadyStatusIs"+status);
            agent_version = rs.getString(rs.getColumnIndex("agent_version"));
            //  currentiteration = rs.getString(rs.getColumnIndex("current_iteration"));
            frequnecy = rs.getString(rs.getColumnIndex("frequency"));
            running_state = rs.getString(rs.getColumnIndex("running_status"));
            thread_name = rs.getString(rs.getColumnIndex("thread_name"));
            upload_status = rs.getString(rs.getColumnIndex("upload_status"));
            scheduled_status=rs.getString(rs.getColumnIndex("scheduled_status"));
            agentsDetails.setFileName(filename);
            agentsDetails.setFilesize(filesize);
            agentsDetails.setFile_path(file_path);
            agentsDetails.setTcp_ip_eth(tcp_ip_eth);
            agentsDetails.setTcp_ip_wifi(tcp_ip_wifi);
            agentsDetails.setTcp_port(tcp_port);
            agentsDetails.setFile_checksum(file_checksum);
            agentsDetails.setSsdt(ssdt);
            agentsDetails.setSedt(sedt);
            agentsDetails.setNoOfIteration(noofiteration);
            agentsDetails.setPeriod(period);
            agentsDetails.setAgent_type(agent_type);
            agentsDetails.setClassName(filesize);
            agentsDetails.setM_name(m_name);
            agentsDetails.setJar_name(jar_name);
            agentsDetails.setJar_size(jar_size);
            agentsDetails.setStatus(status);
            agentsDetails.setAgent_version(agent_version);
            agentsDetails.setCurrentiteration(currentiteration);
            agentsDetails.setFrequnecy(frequnecy);
            agentsDetails.setRunning_state(running_state);
            agentsDetails.setThread_name(thread_name);
            agentsDetails.setUpload_status(upload_status);
            agentsDetails.setScheduledStatus(rs.getString(rs.getColumnIndex("scheduled_status")));
            agentsDetails.setTaskType(rs.getString(rs.getColumnIndex("task_type")));
            agentsDetails.setPriority(rs.getString(rs.getColumnIndex("priority")));
            agentsDetails.setLastScheduledTime(rs.getString(rs.getColumnIndex("last_scheduled_time")));
            agentsDetails.setData(rs.getString(rs.getColumnIndex("data")));
            agentsDetailsList.add(agentsDetails);
            System.out.println(" : status  is   " +upload_status+" schedule status is"+scheduled_status+"and file name is "+filename);
            System.out.println(" : via getAgentsWithCompletedAndReadyStatus yes it has next val " + filename);
        }
        System.out.println("agent deatil list is" + agentsDetailsList);
        return agentsDetailsList;

    }

    // DONE BY  swapnil   for round robin part
    public void updateScheduledStatus(String scheduledStatus, String fileName) {
        String sql = null;

        if (fileName != null) {
            if (fileName.length() > 0) {

                sql = "UPDATE Scheduler_Agents SET scheduled_status='" + scheduledStatus
                        + "' WHERE file_name='" + fileName + "'";
            } else {

                sql = "UPDATE Scheduler_Agents SET scheduled_status='" + scheduledStatus + "'";
            }
        } else {

            sql = "UPDATE Scheduler_Agents SET scheduled_status='" + scheduledStatus + "'";
        }
        try {
            db.execSQL(sql);

        }
        catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception while updating status of " + fileName + " where status is " + scheduledStatus);
            e.printStackTrace();

        }

    }
    public boolean insertIPPingDetails(String ip) {
        try {
            System.out.println(" insertUrlDetailsInDb  ");
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ip", ip);
            long result1 = db.insert("ip_ping_list", null, contentValues);
            System.out.println(" the result is in insertIPDetails " + result1);
            if (result1 == -1) {
                return false;
            } else {
                System.out.println("true in logging agent");
                return true;
            }
        }
        catch (Exception e) {
            System.out.println("db error in inserting " + e.toString());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return false;

    }
    public boolean insertInHeatMapTable(String path,String url_id,String file_name, String date_time,
                                        String category_name,String upload_id,String media_type,String file_size,String comments,String count,String status ) {
        try {
            //open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("path",  path);
            Log.i(TAG," path is......."+ path);
            contentValues.put("url_id",url_id);
            Log.i(TAG," url_id is......."+url_id);
            contentValues.put("file_name",file_name);
            Log.i(TAG,"file_name......."+file_name);
            contentValues.put("date_time", date_time);
            Log.i(TAG,"date time  is......."+date_time);
            contentValues.put("category_name",category_name);
            Log.i(TAG," category_name is......."+category_name);
            contentValues.put("upload_id",upload_id);
            Log.i(TAG,"upload_id is......."+upload_id);
            contentValues.put("media_type",media_type);
            Log.i(TAG,"media_type  is......."+media_type);
            contentValues.put("file_size",file_size);
            Log.i(TAG,"file_size is......."+file_size);
            contentValues.put("comments",comments);
            Log.i(TAG,"comments is......."+comments);
            contentValues.put("count",count);
            Log.i(TAG,"count  is......."+count);
            contentValues.put("status", status);
            Log.i(TAG,"creenshot_For_HeatMap status  is......."+status);
            long result = db.insert("Screenshot_For_HeatMap", null, contentValues);
            Log.i(TAG,"Result in insertInHeatMapTable  is "+result);
            if (result == -1) {
                return true;
            } else {
                Log.i(TAG,"true in insertInHeatMapTable  agent");
            }


        }
        catch (Exception e) {
            Log.i(TAG,"db error in inserting " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return true;
    }



    public boolean insertInSchedulerAgentTable(String device_sno, String file_name, String file_size, String file_cksum,
                                               String jar_path, String class_name, String method_name, String ssdt, String sedt, String period, String num_of_times,
                                               String tcp_ip_ehernet, String tcp_ip_wifi, String tcp_ip_lte, String tcp_port, String upload_status,
                                               String usdt, String agent_version, String ucdt, String frequency,
                                               String thread_name, String running_status, String data, String scheduled_status, String priority, String task_type,
                                               String last_scheduled_time) {

        open();
        ContentValues contentValues = new ContentValues();
        contentValues.put("device_sno", device_sno);
        contentValues.put("file_name", file_name);
        contentValues.put("file_size", file_size);
        contentValues.put("file_cksum", file_cksum);
        contentValues.put("jar_path", jar_path);
        contentValues.put("class_name", class_name);
        contentValues.put("method_name", method_name);
        contentValues.put("ssdt", ssdt);
        contentValues.put("sedt", sedt);
        contentValues.put("period", period);
        contentValues.put("num_of_times", num_of_times);
        contentValues.put("tcp_ip_ehernet", tcp_ip_ehernet);
        contentValues.put("tcp_ip_wifi", tcp_ip_wifi);
        contentValues.put("tcp_ip_lte", tcp_ip_lte);
        contentValues.put("tcp_port", tcp_port);
        contentValues.put("upload_status", upload_status);
        contentValues.put("usdt", usdt);
        contentValues.put("agent_version", agent_version);
        contentValues.put("ucdt", ucdt);
        contentValues.put("frequency", frequency);
        contentValues.put("thread_name", thread_name);
        contentValues.put("running_status", running_status);
        contentValues.put("data", data);
        contentValues.put("scheduled_status", scheduled_status);
        contentValues.put("priority", priority);
        contentValues.put("task_type", task_type);
        contentValues.put("last_scheduled_time", last_scheduled_time);
        System.out.println("last_scheduled_time is " + last_scheduled_time);
        long result1 = db.insert("Scheduler_Agents", null, contentValues);
        // long result1 = db.insertWithOnConflict("Scheduler_Agents", null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        // if (result == -1) {
        System.out.println(" the result is in insertInSchedulerAgentTable "+result1);
        if (result1 == -1) {
            return false;
        } else {
            System.out.println("true in logging agent");
            return true;
        }
        //return result1;
        // } else {
        //   System.out.println("true and agent details are"+file_name+"and data is"+data+" schedule status is"+scheduled_status+" upload status is "+ upload_status);
        // }


        //} catch (Exception e) {
        //    System.out.println("db error in inserting " + e.toString());
        //    e.printStackTrace();
        // }
        // return false;

    }

    public void selectAgentData() {

        Cursor cursor = db.rawQuery("SELECT * FROM Scheduler_Agents", null);
        if (cursor != null) {
            System.out.println(cursor.getCount() + " count");
        }

        if (cursor.moveToFirst()) {
            do {
                String fileName = cursor.getString(cursor.getColumnIndex("file_name"));
                System.out.println("File name is " + fileName);
            } while (cursor.moveToNext());
        }


    }



    public String currentStatusOfSensor(String threadname) {
        String status = null;
        System.out.println("thread name is" + threadname);
        String sql = "SELECT * FROM Scheduler_Agents WHERE file_name='" + threadname + "'";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                status = c.getString(c.getColumnIndex("upload_status"));
                System.out.println("upload status  is " + status);
            } while (c.moveToNext());
        }

        return status;
    }
    public boolean checkIfRecordExists(String fileName) {
        String sql = "SELECT COUNT(*) FROM Scheduler_Agents WHERE file_name=?";
        Cursor cursor = db.rawQuery(sql, new String[]{fileName});
        boolean exists = false;
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    exists = cursor.getInt(0) > 0;
                }
            } finally {
                cursor.close();
            }
        }
        return exists;
    }

    public void  updateStatusOfJarToDownloadStart(String filename, String status) throws IOException {
        open();
        String sql = "UPDATE Scheduler_Agents SET  upload_status='" + status + "'" + " WHERE file_name='" + filename + "'";
        System.out.println(" uplaod status is in updateStatusOfJarToDownloadStart "+status+" file name is"+filename);

        try {
            db.execSQL(sql);
            //Log.i(TAG, " UpdateStatusOfAgentsEvents  query is executed ");
        } catch (Exception e) {
            // Log.e(TAG, "Exception in updateStatusOfAgentsEvents is " + e.getMessage());
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            close();
        }
    }


    public String getAgentData(String name) {

        String data = null;
        try {
            System.out.println(" entering getAgentDaata");
            data = "-1";
            open();
            Cursor rs = db.rawQuery("SELECT * from Scheduler_Agents WHERE file_name='" + name + "'", null);
            if (rs != null) {
                System.out.println(rs.getCount() + " count");
            }
            if (rs.moveToFirst()) {
                do {
                    data = rs.getString(rs.getColumnIndex("data"));
                    System.out.println(" in table data  are" + data);
                } while (rs.moveToNext());
            }
            return  data;

        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Exception while updating status of " + e.getMessage());
            e.printStackTrace();

        }
        return data;
    }

    public  int updateAgentDetails(String device_sno, String file_name, String file_size,
                                   String file_cksum,
                                   String jar_path, String class_name, String method_name, String ssdt, String sedt,
                                   String period,
                                   String num_of_times, String tcp_ip_ehernet, String tcp_ip_wifi, String tcp_ip_lte,
                                   String tcp_port,
                                   String agent_version,String type,
                                   String frequency,String priority,String task_type) {

        int count = 0;
        try {
            open();
            ContentValues initialValues = new ContentValues();
            initialValues.put("device_sno", device_sno);
            initialValues.put("file_size", file_size);
            initialValues.put("file_cksum", file_cksum);
            initialValues.put("jar_path", jar_path);
            initialValues.put("class_name", class_name);
            initialValues.put("method_name", method_name);
            initialValues.put("ssdt", ssdt);
            initialValues.put("sedt", sedt);
            initialValues.put("period", period);
            initialValues.put("num_of_times", num_of_times);
            initialValues.put("tcp_ip_ehernet", tcp_ip_ehernet);
            initialValues.put("tcp_ip_wifi", tcp_ip_wifi);
            initialValues.put("tcp_ip_lte", tcp_ip_lte);
            initialValues.put("tcp_port", tcp_port);
            initialValues.put("agent_version", agent_version);
            initialValues.put("type", type);
            initialValues.put("frequency", frequency);
            initialValues.put("priority", priority);
            initialValues.put("task_type", task_type);
            db.update("Scheduler_Agents", initialValues, "file_name='" + file_name + "'", null);
            System.out.println("count is  " + count);
            Utils.appendLog("ELOG_UD: updateAgentDetails updated are  " + initialValues);
            close();
        } catch (Exception e) {
            Utils.appendLog("ELOG_UD: error in updateAgentDetails is" + e.getMessage());
        }
        Utils.appendLog("ELOG_UD: count in updateAgentDetails is "+count);
        return count;


    }
    public boolean insertInLoggingAgentTable(String agent_name, String evt_type, String agent_output, String date_time,
                                             String status) {
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("agent_name ", agent_name);
            System.out.println("agent name is......."+agent_name);
            contentValues.put("evt_type", evt_type);
            System.out.println(" event type  is......."+evt_type);
            contentValues.put("agent_output", agent_output);
            System.out.println("agent  output is......."+agent_output);
            contentValues.put("date_time", date_time);
            System.out.println("date time  is......."+date_time);
            contentValues.put("status", status);
            System.out.println("insertInLoggingAgentTable status  is......."+status);
            long result = db.insert("Logging_Agent", null, contentValues);
            System.out.println("Result in insertInLoggingAgentTable  is "+result);
            if (result == -1) {
                return false;
            } else {
                System.out.println("true in logging agent");
            }


        }
        catch (Exception e) {
            System.out.println("db error in inserting " + e.toString());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<HashMap<String, String>> selectfromloggingagent() {
        System.out.println(Config.getDateTime() + "  : via selectfromloggingagent Entered selectfromloggingagent method...");
        ArrayList<HashMap<String,String>> detailsList=new ArrayList<>();
        System.out.println("Detail list is "+detailsList);
        try{
            open();
//            String sql = "SELECT * FROM Logging_Agent WHERE status='init'";
            String sql ="SELECT * FROM logging_agent WHERE status='init' ORDER BY date_time DESC LIMIT 1";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is "+rs);
            if (rs != null) {
                System.out.println("Cursor count is at 443 is at all " + rs.getCount() + " count");
            }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                String agent_name = rs.getString(rs.getColumnIndex("agent_name"));
                System.out.println("agent name is "+agent_name);
                String evt_type = rs.getString(rs.getColumnIndex("evt_type"));
                System.out.println("event type name is "+evt_type);
                String agent_output = rs.getString(rs.getColumnIndex("agent_output"));
                System.out.println("agent output  is "+agent_output);
                String date_time = rs.getString(rs.getColumnIndex("date_time"));
                System.out.println("date time is "+date_time);
                String status = rs.getString(rs.getColumnIndex("status"));
                System.out.println("status is from  "+status);

                hashMap.put("evt_type",evt_type);
                hashMap.put("agent_name",agent_name);
                hashMap.put("date_time",date_time);
                hashMap.put("status",status);
                hashMap.put("agent_output",agent_output);
                detailsList.add(hashMap);
            }
        }
        catch (Exception e) {
            System.out.println(": via selectfromloggingagent"+e.getMessage());
            e.printStackTrace();

        }
        return detailsList;
    }

    public void send_evt_to_server() {
        Utils.appendLog("ELOG_SEND_EVT_SERVER: Called");

        try{
            open();

//            String sql = "SELECT * FROM Logging_Agent WHERE status='init'";
            String sql ="SELECT rowid, * FROM logging_agent WHERE status='init' ORDER BY date_time ASC LIMIT 5";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is "+rs);
            if (rs != null) {
                System.out.println("Cursor count is at 443 is at all " + rs.getCount() + " count");
            }
            while (rs.moveToNext()) {
                String rowid = String.valueOf(rs.getLong(rs.getColumnIndexOrThrow("rowid")));
                String agent_name = rs.getString(rs.getColumnIndex("agent_name"));
                System.out.println("agent name is "+agent_name);
                String evt_type = rs.getString(rs.getColumnIndex("evt_type"));
                System.out.println("event type name is "+evt_type);
                String agent_output = rs.getString(rs.getColumnIndex("agent_output"));
                System.out.println("agent output  is "+agent_output);
                String date_time = rs.getString(rs.getColumnIndex("date_time"));
                System.out.println("date time is "+date_time);
                String status = rs.getString(rs.getColumnIndex("status"));
                System.out.println("status is from  "+status);

                if (agent_output!=null&& Utils.isNetworkAvailable(MviewApplication.ctx))
                {

//                    updateStatusOfAgentsEvents("processing",evt_type,rowid);

                    try {
                        JSONArray object = new JSONArray(agent_output);
                        Utils.appendLog("ELOG_SEND_EVT_SERVER: Agent name is: "+agent_name +" EVT_TYPE is: "+evt_type+"  ROWID is: "+rowid + " Last RowId is: "+getLastInsertedRowId());

                        Log.d("LogFapps","SEnd sdk_new evt to server"+object);
                        RequestResponse.sendEvtToServer(object,  AllInOneAsyncTaskForEVT.AsyncTaskPurpose.SDK,evt_type,rowid);


                    } catch (JSONException e) {
                        Utils.appendLog("ELOG_SEND_EVT_SERVER_EXCEPTION: Exception while sending EVT request is "+e.getMessage());
                        e.toString();

                    }

                }

            }
        }
        catch (Exception e) {
            Utils.appendLog("ELOG_SEND_EVT_SERVER_EXCEPTION: Exception while selectfromloggingagent EVT request is "+e.getMessage());

            System.out.println(": via selectfromloggingagent"+e.getMessage());
            e.printStackTrace();

        }
    }

    public int getLastInsertedRowId() {
        int lastInsertedRowId = -1;
        try {
            open();
            String lastInsertRowIdQuery = "SELECT rowid FROM logging_agent ORDER BY rowid DESC LIMIT 1";
            Cursor lastInsertCursor = db.rawQuery(lastInsertRowIdQuery, null);
            if (lastInsertCursor.moveToFirst()) {
                lastInsertedRowId = lastInsertCursor.getInt(0);
            }
            lastInsertCursor.close();
        } catch (Exception e) {
            Utils.appendLog("ELOG_GET_LAST_INSERTED_ROWID_EXCEPTION: Exception while fetching last inserted rowid is " + e.getMessage());
            e.printStackTrace();
        } finally {
            close();
        }
        return lastInsertedRowId;
    }

    public boolean insertUrlDetailsInDb(String id, String url, String network, String browser, String dns_ip, int i) {
        try {
            System.out.println(" insertUrlDetailsInDb  ");
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("id", id);
            contentValues.put("url", url);
            contentValues.put("network", network);
            contentValues.put("browser", browser);
            contentValues.put("dns_ip", dns_ip);
            //contentValues.put("scan_id", scan_id);
            contentValues.put("i", i);
            long result1 = db.insert("manual_url_list", null, contentValues);
            // long result1 = db.insertWithOnConflict("Scheduler_Agents", null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
            // if (result == -1) {
            System.out.println(" the result is in insertInSchedulerAgentTable " + result1);
            if (result1 == -1) {
                return false;
            } else {
                System.out.println("true in logging agent");
                return true;
            }
        }
        catch (Exception e) {
            System.out.println("db error in inserting " + e.toString());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return false;

    }

    public String current_status(String jar_name) {
        String status = null;
        System.out.println("thread name is" + jar_name);
        String sql = "SELECT * FROM Scheduler_Agents WHERE file_name='" + jar_name + "'";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {

            status=c.getString(c.getColumnIndex("upload_status"));
            System.out.println("upload status  is " + status);
        }


        return status;

    }


    public String end_date(String jar_name) {
        String end_date = null;
        System.out.println("thread name is" + jar_name);
        String sql = "SELECT * FROM Scheduler_Agents WHERE file_name='" + jar_name + "'";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {

            end_date=c.getString(c.getColumnIndex("sedt"));
            System.out.println("End date is " + end_date);
        }


        return end_date;

    }

    public void updateEndDate(String jar_name, String new_end_date) {
        String sql = "UPDATE Scheduler_Agents SET sedt=? WHERE file_name=?";
        Utils.appendLog("ELOG_UPDATE_ENDTIME: Updating end date to start date for Agent: " + jar_name + " to " + new_end_date);
        db.execSQL(sql, new String[]{new_end_date, jar_name});
        System.out.println("End date updated successfully.");
    }
    public String start_date(String jar_name) {
        String start_date = null;
        System.out.println("thread name is" + jar_name);
        String sql = "SELECT * FROM Scheduler_Agents WHERE file_name='" + jar_name + "'";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext()) {

//                period = c.getString(c.getColumnIndex("period"));
            start_date=c.getString(c.getColumnIndex("ssdt"));
            System.out.println("Start date is " + start_date);
        }

        return start_date;

    }

    public Map<String, Integer> getThreadPriorities() {
        Map<String, Integer> threadPriorities = new HashMap<>();

        //  'Scheduler_Agents' table contains columns 'file_name' and 'priority'
        String sql = "SELECT file_name, priority FROM Scheduler_Agents";
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            String fileName = c.getString(c.getColumnIndex("file_name"));
            String priorityJsonString = c.getString(c.getColumnIndex("priority"));
            Log.d(TAG, "getFileName: "+ fileName);
            Log.d(TAG, "getPriorities: "+priorityJsonString);

            // Parse the JSON array containing thread priorities
            try {
                JSONArray priorityArray = new JSONArray(priorityJsonString);
                Log.d(TAG, "getPriorityArray: "+priorityArray);
                // Each thread has only one priority in the JSON array
                if (priorityArray.length() > 0) {
                    int priority = priorityArray.getInt(0); // Assuming priority is stored at index 0
                    threadPriorities.put(fileName, priority);
                }
            } catch (JSONException e) {
                try {
                    int priority = Integer.parseInt(priorityJsonString);
                    threadPriorities.put(fileName, priority);
                } catch (NumberFormatException ex) {
                    // Handle the case where the priority is not a valid integer
                    e.printStackTrace();
                    Utils.appendLog("Invalid priority value for thread: " + fileName);
                }
            }
        }

        c.close();
        Log.d(TAG, "return thread priority: "+threadPriorities);
        return threadPriorities;
    }

    public String currentperiodOfSensor(String threadname) {
        String period = null;
        System.out.println("thread name is" + threadname);
        String sql = "SELECT * FROM Scheduler_Agents WHERE file_name='" + threadname + "'";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext())
        {

            period=c.getString(c.getColumnIndex("period"));
            System.out.println("upload status  is " + period);
        }

        return period;
    }

    public ArrayList<HashMap<String,String>>getAgentData() {


        ArrayList<HashMap<String,String>>list=new ArrayList<>();
        String agent_name = null;
        String sql = "SELECT * FROM Scheduler_Agents";
        Cursor c = db.rawQuery(sql, null);
        while (c.moveToNext())
        {

            HashMap<String,String>hashMap=new HashMap<>();
            agent_name=c.getString(c.getColumnIndexOrThrow("file_name"));
            /*
            period", period);
        contentValues.put("num_of_times"
             */
            String periodvalue= c.getString(c.getColumnIndexOrThrow("period"));

            String no_iterations= c.getString(c.getColumnIndexOrThrow("num_of_times"));
            hashMap.put("file_name",agent_name);
            hashMap.put("period",periodvalue);
            hashMap.put("no_iterations",no_iterations);

            list.add(hashMap);
        }
        Log.d(Mview.TAG,"Size of list from getAgentData is "+list);

        return list;
    }
    /*public void readTestResultsAt(float x, float y, String mapId) {
        String  sqlQuery="SELECT  * FROM " + "TEST_RESULTS "
                + " WHERE map_id = "+mapId +" AND ";
        Cursor rs = db.rawQuery(sqlQuery, null);
        System.out.println("Cursor rs is " + rs);
        if (rs != null) { }
        while (rs.moveToNext()) {
            TestResults testResults=new TestResults();
            testResults.setColor(rs.getInt(rs.getColumnIndex("marker_color")));
            testResults.setMapId(rs.getInt(rs.g));
        }
        //Cursor cursor=db.rawQuery("SELECT * FROM TEST_RESULTS ")
    }*/
    public ArrayList<HistoryModel> readHistory(String tag) {
      String query="SELECT * FROM HISTORY WHERE tag = '"+ tag+"' ORDER BY id DESC";
       // String query="SELECT * FROM HISTORY GROUP BY "+tag+" ORDER BY id DESC";
        Log.i(TAG,"History Query is "+query);

        Cursor rs = db.rawQuery(query, null);
        ArrayList<HistoryModel> historyList=new ArrayList<>();


        if (rs != null) {
            while (rs.moveToNext()) {
                HistoryModel historyModel = new HistoryModel();
                Log.i(TAG,"Tag is "+rs.getString(rs.getColumnIndex("tag")));
                historyModel.setTag(rs.getString(rs.getColumnIndex("tag")));
                historyModel.setValue(rs.getString(rs.getColumnIndex("value")));
              //  hi.setId(rs.getString(rs.getColumnIndex("id")));

                historyList.add(historyModel);
                Log.i(TAG,"Sub list "+historyList.size());

            }
        }
        //Cursor cursor=db.rawQuery("SELECT * FROM TEST_RESULTS ")
        return historyList;
    }

    public ArrayList<SubscriberModel> readSubscriberDetails() {
        String query="SELECT * FROM SUBSCRIBER_DETAILS";
        Cursor rs = db.rawQuery(query, null);
        ArrayList<SubscriberModel> subscribersList=new ArrayList<>();


        if (rs != null) {
            while (rs.moveToNext()) {
                SubscriberModel subscriberModel = new SubscriberModel();
                subscriberModel.setSubscriberName(rs.getString(rs.getColumnIndex("subscriber_name")));
                subscriberModel.setSubscriberId(rs.getString(rs.getColumnIndex("subscriber_id")));
                subscriberModel.setId(rs.getString(rs.getColumnIndex("id")));

                 subscribersList.add(subscriberModel);
                 Log.i(TAG,"Sub list "+subscribersList.size());

            }
        }
        //Cursor cursor=db.rawQuery("SELECT * FROM TEST_RESULTS ")
        return subscribersList;
    }
    public TestResults readTestResultsAtMarkerLocation(int id)
    {
        String  sqlQuery="SELECT * FROM " + "TEST_RESULTS "
                + " WHERE id = "+id;
        Cursor rs = db.rawQuery(sqlQuery, null);
        System.out.println("Cursor rs is " + rs);

        if (rs != null) {
            while (rs.moveToNext()) {
                TestResults testResults = new TestResults();
                testResults.setColor(Integer.parseInt(rs.getString(rs.getColumnIndex("marker_color"))));
                testResults.setMapId(rs.getInt(rs.getColumnIndex("map_id")) + "");
                testResults.setX(Float.parseFloat(rs.getString(rs.getColumnIndex("x_coord"))));
                testResults.setY(Float.parseFloat(rs.getString(rs.getColumnIndex("y_coord"))));
                testResults.setResult(rs.getString(rs.getColumnIndex("test_results")));
                testResults.setTestId(rs.getString(rs.getColumnIndex("test_id")));
                testResults.setId(rs.getString(rs.getColumnIndex("id")));
               return testResults;
            }
        }
        //Cursor cursor=db.rawQuery("SELECT * FROM TEST_RESULTS ")
        return null;
    }
    public List<TestResults> readTestResultsAt(int id) {
        String  sqlQuery="SELECT * FROM " + "TEST_RESULTS "
                + " WHERE map_id = "+id;
        Cursor rs = db.rawQuery(sqlQuery, null);
        System.out.println("Cursor rs is " + rs);
List<TestResults> testResultsArrayList=new ArrayList<>();
        if (rs != null) {
            while (rs.moveToNext()) {
                TestResults testResults = new TestResults();
                testResults.setColor(Integer.parseInt(rs.getString(rs.getColumnIndex("marker_color"))));
                testResults.setMapId(rs.getInt(rs.getColumnIndex("map_id")) + "");
                testResults.setX(Float.parseFloat(rs.getString(rs.getColumnIndex("x_coord"))));
                testResults.setY(Float.parseFloat(rs.getString(rs.getColumnIndex("y_coord"))));
                testResults.setResult(rs.getString(rs.getColumnIndex("test_results")));
                testResults.setTestId(rs.getString(rs.getColumnIndex("test_id")));
                testResults.setId(rs.getString(rs.getColumnIndex("id")));
testResultsArrayList.add(testResults);
            }
        }
        //Cursor cursor=db.rawQuery("SELECT * FROM TEST_RESULTS ")
        return testResultsArrayList;
    }
    public List<TestResults> readTestResults() {
        List<TestResults> list=new ArrayList<>();
        String  sqlQuery="SELECT  * FROM " + "TEST_RESULTS ";
        Cursor rs = db.rawQuery(sqlQuery, null);
        System.out.println("Cursor rs is " + rs);
        if (rs != null) {
            while (rs.moveToNext()) {
                TestResults testResults = new TestResults();
                testResults.setColor(Integer.parseInt(rs.getString(rs.getColumnIndex("marker_color"))));
                testResults.setMapId(rs.getInt(rs.getColumnIndex("map_id")) + "");
                testResults.setX(Float.parseFloat(rs.getString(rs.getColumnIndex("x_coord"))));
                testResults.setY(Float.parseFloat(rs.getString(rs.getColumnIndex("y_coord"))));
                testResults.setResult(rs.getString(rs.getColumnIndex("test_results")));
                testResults.setTestId(rs.getString(rs.getColumnIndex("test_id")));
                testResults.setId(rs.getString(rs.getColumnIndex("id")));

                list.add(testResults);
            }
        }
        return list;
    }
    public List<MapModel> readMapDataForMapId(String id)
    {

int _id= Integer.parseInt(id);
        List<MapModel> detailsList=new ArrayList<>();

        try {
         //   open();
            String sql = "SELECT  * FROM " + "Heat_map_table "
                    + " WHERE map_id = "+_id +" ORDER BY map_id DESC";
           /* String sql =  "SELECT scan_id , COUNT(*) as row_count FROM manual_url_list"
                    + " WHERE status = 'completed'"
                    + " GROUP BY scan_id"
                    + " ORDER BY COUNT(*) ASC";*/
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            if (rs != null) { }
            while (rs.moveToNext()) {
                MapModel mapModel=new MapModel();
                mapModel.setMapId(rs.getInt(rs.getColumnIndex("map_id"))+"");
                mapModel.setFloorPlan(rs.getString(rs.getColumnIndex("floor_plan_name")));
                mapModel.setSsidName(rs.getString(rs.getColumnIndex("ssid_name")));
                mapModel.setTechnology(rs.getString(rs.getColumnIndex("technology")));
                mapModel.setFlatType(rs.getString(rs.getColumnIndex("flat_type")));
                mapModel.setOpening(rs.getString(rs.getColumnIndex("opening")));
                mapModel.setOpeningType(rs.getString(rs.getColumnIndex("opening_type")));
                mapModel.setComponent(rs.getString(rs.getColumnIndex("component")));
                mapModel.setSurveyFor(rs.getString(rs.getColumnIndex("survey")));
                mapModel.setLocationType(rs.getString(rs.getColumnIndex("location_type")));
                mapModel.setAddress(rs.getString(rs.getColumnIndex("address")));
                mapModel.setLatitude(rs.getString(rs.getColumnIndex("latitude")));
                mapModel.setLongitude(rs.getString(rs.getColumnIndex("longitude")));
                mapModel.setDeviceId(rs.getString(rs.getColumnIndex("device_id")));
                mapModel.setWorkOrderId(rs.getString(rs.getColumnIndex("work_order_id")));
                mapModel.setMapPath(rs.getString(rs.getColumnIndex("map_path")));
                mapModel.setDateTime(rs.getString(rs.getColumnIndex("date_time")));
                mapModel.setFloorPlanPath(rs.getString(rs.getColumnIndex("floor_plan_path")));
                mapModel.setFinalMapPath(rs.getString(rs.getColumnIndex("final_map_path")));
                mapModel.setMsisdn(rs.getString(rs.getColumnIndex("msisdn")));
                mapModel.setFinalMapName(rs.getString(rs.getColumnIndex("final_map_name")));
                mapModel.setWalkMapName(rs.getString(rs.getColumnIndex("map_name")));
                mapModel.setWifiX(rs.getString(rs.getColumnIndex("wifi_x")));
                mapModel.setWifiY(rs.getString(rs.getColumnIndex("wifi_y")));
                mapModel.setExcellentCoveragePercentage(rs.getString(rs.getColumnIndex("excel_percent")));
                mapModel.setGoodCoveragePercentage(rs.getString(rs.getColumnIndex("good_percent")));
                mapModel.setFairCoveragePercentage(rs.getString(rs.getColumnIndex("fair_percent")));
                mapModel.setPoorCoveragePercentage(rs.getString(rs.getColumnIndex("poor_percent")));

                mapModel.setLsExcellentCoveragePercentage(rs.getString(rs.getColumnIndex("ls_excel_percent")));
                mapModel.setLsGoodCoveragePercentage(rs.getString(rs.getColumnIndex("ls_good_percent")));
                mapModel.setLsFairCoveragePercentage(rs.getString(rs.getColumnIndex("ls_fair_percent")));
                mapModel.setLsPoorCoveragePercentage(rs.getString(rs.getColumnIndex("ls_poor_percent")));

                mapModel.setLsWalkMap(rs.getString(rs.getColumnIndex("ls_walkmap")));
                mapModel.setLsWalkMapPath(rs.getString(rs.getColumnIndex("ls_walkmap_path")));
                mapModel.setLsHeatMap(rs.getString(rs.getColumnIndex("ls_heatmap")));
                mapModel.setLsHeatMapPath(rs.getString(rs.getColumnIndex("ls_heatmap_path")));
                mapModel.setSubscriberName(rs.getString(rs.getColumnIndex("subscriber_name")));
                mapModel.setSubscriberId(rs.getString(rs.getColumnIndex("subscriber_id")));
                mapModel.setWalkMapWarningIgnored(rs.getInt(rs.getColumnIndex("walkmap_warning")));

                detailsList.add(mapModel);
            }
        } catch (Exception e) {
            System.out.println(": via selectUrlName() is " + e.getMessage());
            e.printStackTrace();

        }

        return detailsList;



    }


    public List<MapModel> readMapData()
    {


            List<MapModel> detailsList=new ArrayList<>();

            try {
                open();
                String sql = "SELECT  * FROM " + "Heat_map_table ORDER BY map_id DESC";
                Cursor rs = db.rawQuery(sql, null);
                System.out.println("Cursor rs is " + rs);
                if (rs != null) { }
                while (rs.moveToNext()) {
                    MapModel mapModel=new MapModel();
                   mapModel.setMapId(rs.getInt(rs.getColumnIndex("map_id"))+"");
                    mapModel.setFloorPlan(rs.getString(rs.getColumnIndex("floor_plan_name")));
                   mapModel.setSsidName(rs.getString(rs.getColumnIndex("ssid_name")));
                   mapModel.setTechnology(rs.getString(rs.getColumnIndex("technology")));
                   mapModel.setFlatType(rs.getString(rs.getColumnIndex("flat_type")));
                   mapModel.setOpening(rs.getString(rs.getColumnIndex("opening")));
                   mapModel.setOpeningType(rs.getString(rs.getColumnIndex("opening_type")));
                   mapModel.setComponent(rs.getString(rs.getColumnIndex("component")));
                   mapModel.setSurveyFor(rs.getString(rs.getColumnIndex("survey")));
                   mapModel.setLocationType(rs.getString(rs.getColumnIndex("location_type")));
                   mapModel.setAddress(rs.getString(rs.getColumnIndex("address")));
                   mapModel.setLatitude(rs.getString(rs.getColumnIndex("latitude")));
                   mapModel.setLongitude(rs.getString(rs.getColumnIndex("longitude")));
                   mapModel.setDeviceId(rs.getString(rs.getColumnIndex("device_id")));
                   mapModel.setWorkOrderId(rs.getString(rs.getColumnIndex("work_order_id")));
                   mapModel.setMapPath(rs.getString(rs.getColumnIndex("map_path")));
                   mapModel.setDateTime(rs.getString(rs.getColumnIndex("date_time")));
                    mapModel.setFloorPlanPath(rs.getString(rs.getColumnIndex("floor_plan_path")));
                    mapModel.setFinalMapPath(rs.getString(rs.getColumnIndex("final_map_path")));
                    mapModel.setMsisdn(rs.getString(rs.getColumnIndex("msisdn")));
                    mapModel.setFinalMapName(rs.getString(rs.getColumnIndex("final_map_name")));
                    mapModel.setWifiX(rs.getString(rs.getColumnIndex("wifi_x")));
                    mapModel.setWifiY(rs.getString(rs.getColumnIndex("wifi_y")));
                    mapModel.setExcellentCoveragePercentage(rs.getString(rs.getColumnIndex("excel_percent")));
                    mapModel.setGoodCoveragePercentage(rs.getString(rs.getColumnIndex("good_percent")));
                    mapModel.setFairCoveragePercentage(rs.getString(rs.getColumnIndex("fair_percent")));
                    mapModel.setPoorCoveragePercentage(rs.getString(rs.getColumnIndex("poor_percent")));

                    mapModel.setLsExcellentCoveragePercentage(rs.getString(rs.getColumnIndex("ls_excel_percent")));
                    mapModel.setLsGoodCoveragePercentage(rs.getString(rs.getColumnIndex("ls_good_percent")));
                    mapModel.setLsFairCoveragePercentage(rs.getString(rs.getColumnIndex("ls_fair_percent")));
                    mapModel.setLsPoorCoveragePercentage(rs.getString(rs.getColumnIndex("ls_poor_percent")));

                    mapModel.setLsWalkMap(rs.getString(rs.getColumnIndex("ls_walkmap")));
                    mapModel.setLsWalkMapPath(rs.getString(rs.getColumnIndex("ls_walkmap_path")));
                    mapModel.setLsHeatMap(rs.getString(rs.getColumnIndex("ls_heatmap")));
                    mapModel.setLsHeatMapPath(rs.getString(rs.getColumnIndex("ls_heatmap_path")));
                    mapModel.setPoints(rs.getString(rs.getColumnIndex("points")));
                    mapModel.setSubscriberName(rs.getString(rs.getColumnIndex("subscriber_name")));
                    mapModel.setSubscriberId(rs.getString(rs.getColumnIndex("subscriber_id")));
                    mapModel.setLatitude(rs.getString(rs.getColumnIndex("latitude")));
                    mapModel.setLongitude(rs.getString(rs.getColumnIndex("longitude")));
                    mapModel.setWalkMapWarningIgnored(rs.getInt(rs.getColumnIndex("walkmap_warning")));
                   detailsList.add(mapModel);
                }
            } catch (Exception e) {
                System.out.println(": via selectUrlName() is " + e.getMessage());
                e.printStackTrace();

            }

            return detailsList;



    }
    public ArrayList<HashMap<String, String>> selectUrlName() {

        ArrayList<HashMap<String,String>> detailsList=new ArrayList<>();
        System.out.println("Detail list is "+detailsList);
        try {
            open();
            String sql = "SELECT  * FROM " + "manual_url_list";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            if (rs != null) { }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                String   url = rs.getString(rs.getColumnIndex("url"));
                System.out.println(" Url  is " + url);
                hashMap.put("url",url);
                detailsList.add(hashMap);
            }
        } catch (Exception e) {
            System.out.println(": via selectUrlName() is " + e.getMessage());
            e.printStackTrace();

        }

        return detailsList;


    }
    public int countInitStatusRows() {
        int countInitRows = 0;
        String sqlCount = "SELECT COUNT(*) FROM logging_agent WHERE status='init'";

        try {
            Cursor cursor = db.rawQuery(sqlCount, null);
            if (cursor.moveToFirst()) {
                countInitRows = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(Mview.TAG, "In Db class countInitStatusRows " + e.toString());
            e.printStackTrace();
        }

        Utils.appendLog("Total rows with 'init' status: " + countInitRows);
        return countInitRows;
    }
    public void updateStatusOfAgentsEvents(String status) {

        Utils.appendLog("Status marked as Completed for all 'init' rows except the last 10 Manually");

        // SQL to update the status of all rows with 'init' status except for the last 10 rows
        String sqlUpdate = "UPDATE logging_agent SET status=? WHERE status='init' AND rowid NOT IN (" +
                "SELECT rowid FROM logging_agent ORDER BY date_time DESC LIMIT 10)";

        try {
            db.execSQL(sqlUpdate, new Object[]{status});
        } catch (Exception e) {
            Log.d(Mview.TAG, "In Db class updateStatusOfAgentsEvents " + e.toString());
            e.printStackTrace();
        }
    }


    public  void updateStatusOfAgentsEvents(String status, String agent_name ,String rowid) {
        Log.d(Mview.TAG,"In Db class updatestatusofagentdata called");

        Utils.appendLog("Agent Name is: "+ agent_name+" RowId status marked as Completed: "+rowid);


//            String sql = "UPDATE logging_agent SET status='" + status + "' WHERE agent_name='" + enc_agent_name + "' AND date_time='" + dateandtime + "'";
            String sql = "UPDATE logging_agent SET status='" + status + "' WHERE evt_type='" + agent_name + "' AND rowid='" + rowid + "'";

            try {
                db.execSQL(sql);
            } catch (Exception e) {
                Log.d(Mview.TAG, "In Db class updatestatusofagentdata " + e.toString());

                // TODO: handle exception
                e.printStackTrace();
            }


    }
//    public void updateStatusOfAgentsEvents(String status, String evt_type) {
////
////        open();
//        //Log.i(TAG,"Update query called for event type "+evt_type + " status is "+status);
////        String sql = "UPDATE  Logging_Agent  SET status='"+status+"' WHERE evt_type='"+evt_type+"'";
//        ContentValues values = new ContentValues();
//        values.put("status", status);
//
//        String selection = "evt_type = ?";
//        String[] selectionArgs = { evt_type };
//
//        db.update("Logging_Agent", values, selection, selectionArgs);
//
////        try {
////
////            db.execSQL(sql);
////           // Log.i(TAG," UpdateStatusOfAgentsEvents  query is executed ");
////
////
////        } catch (Exception e) {;
////          //  Log.e(TAG,"Exception in updateStatusOfAgentsEvents is "+e.getMessage());
////            // TODO: handle exception
////            e.printStackTrace();
////        }
//
////        finally {
////
////            close();
////        }
//
//
//
//
//    }


// FOR SCREEN SHOT UPLOAD 10/14/2022
    public ArrayList<HashMap<String, String>> getScCountToUpload()
    {


        ArrayList<HashMap<String,String>> urlsDetailsList=null;
//helper.Utils.setMutexForManualScanTable(helper.Config.manualScanUploadValue);
//logger.debug(helper.Config.getDateTime() +" : via readManualScanUrlsWithCompletedStatus Manual Scan Update Value obtained "+
//helper.Utils.manualScanMutex +" current thread value is "+helper.Config.manualScanUploadValue);
//if(helper.Utils.manualScanMutex==helper.Config.manualScanUploadValue)
//{

//String sql="SELECT COUNT(*) as row_count FROM manual_url_list WHERE status='completed' LIMIT 20";
        try {
            open();
            String sql =  "SELECT scan_id , COUNT(*) as row_count FROM manual_url_list"
                    + " WHERE status = 'completed'"
                    + " GROUP BY scan_id"
                    + " ORDER BY COUNT(*) ASC";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            {
                urlsDetailsList = new ArrayList<>();


//rs    = stmt.executeQuery(sql);
                if (rs != null) {
//if(helper.Utils.manualScanMutex==helper.Config.manualScanUploadValue)
                    {

                        while (rs.moveToNext()) {

                            HashMap<String, String> detailsHp = new HashMap<>();


                            int row_count = rs.getInt(rs.getColumnIndex("row_count"));
                            String scan_id = rs.getString(rs.getColumnIndex("scan_id"));
                            detailsHp.put("scan_id", scan_id);
                            detailsHp.put("count", row_count + "");
                            //logger.debug(helper.Config.getDateTime() +"" + " : via getScCountToUpload Count of list for removing urls "+row_count +" scanid "+scan_id);
                            urlsDetailsList.add(detailsHp);


                        }
                    }
                   close();


                }
            }

        }
        catch(Exception e)
        {
            System.out.println(" exception in getScCountToUpload"+e.getMessage());
            e.printStackTrace();
            //sendExceptionEvent(threadValue,e,"getScCountToUpload");
            //logger.error("Exception",e);
//logger.debug(" : via hitReadQueryAndAddInList Exception occured in upload class in 3... "+e.getMessage());

        }
        finally
        {
        Constants.resetMutexForManualScanTable();
        }


        return urlsDetailsList;

    }

    public ArrayList<HashMap<String,String>> readManualScanUrlsWithCompletedStatus()
    {
        ArrayList<HashMap<String,String>> urlsDetailsList=null;
        Constants.setMutexForManualScanTable(Constants.manualScanUploadValue);
       // logger.debug(helper.Config.getDateTime() +" : via readManualScanUrlsWithCompletedStatus Manual Scan Update Value obtained "+ helper.Utils.manualScanMutex +" current thread value is "+helper.Config.manualScanUploadValue);
        if(Constants.manualScanMutex==Constants.manualScanUploadValue)
        {


            try {
                open();
                String sql = "SELECT * FROM manual_url_list WHERE status='completed' LIMIT 20";
                Cursor rs = db.rawQuery(sql, null);
                System.out.println("Cursor rs is " + rs);
                {
                    urlsDetailsList = new ArrayList<>();


//rs    = stmt.executeQuery(sql);
                    if (rs != null) {
                        if (Constants.manualScanMutex == Constants.manualScanUploadValue) {

                            while (rs.moveToNext()) {

                                HashMap<String, String> detailsHp = new HashMap<>();


                                String status = rs.getString(rs.getColumnIndex("status"));
                                String scan_details = rs.getString(rs.getColumnIndex("scan_details"));

                                String scan_id = rs.getString(rs.getColumnIndex("scan_id"));
                                String upload_id = rs.getString(rs.getColumnIndex("upload_id"));
                                String url_id = rs.getString(rs.getColumnIndex("id"));
                                detailsHp.put("status", status);
                                detailsHp.put("scan_details", scan_details);
                                detailsHp.put("scan_id", scan_id);
                                detailsHp.put("upload_id", upload_id);
                                detailsHp.put("url_id", url_id);

                                if (upload_id != null) {
                                    if (!(upload_id.equals(" ") || upload_id.equals(""))) {
                                        urlsDetailsList.add(detailsHp);
                                    }
                                }


                            }
                        }
                        close();

                    }
                }
            }

            catch(Exception e)
            {
                System.out.println(" exception in readManualScanUrlsWithCompletedStatus"+e.getMessage());
                e.printStackTrace();
               // logger.error("Exception",e);
               // sendExceptionEvent(helper.Config.manualScanUploadValue,e,"readManualScanUrlsWithCompletedStatus");
//logger.debug(" : via hitReadQueryAndAddInList Exception occured in upload class in 3... "+e.getMessage());

            }
            finally
            {
                Constants.resetMutexForManualScanTable();
            }

        }
        return urlsDetailsList;


    }


    public void removeuploadedcontentupload(String url_id) {
        Log.i(TAG," entering removeuploadedcontentupload ");
        db.execSQL("DELETE FROM Screenshot_For_HeatMap WHERE url_id='" + url_id + "'");
    }

    public void deletePlan(String mapId)
    {
        try {

            db.execSQL("DELETE FROM Heat_Map_Table WHERE map_id='" + mapId + "'");
            Log.i(TAG,"Map id "+mapId+" deleted");
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception while deleting Map id "+mapId+" deleted");
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            db.close();
        } catch (Exception e) {
            System.out.println("db error in closing dbhandler:-" + e.toString());
        }
    }
    public void updateMapData(String ssidName, String floorPlanName,
                              String tech,String flatType,String opening,
                              String openingType,String component,String surveyFor,
                              String locationType,String address,
                              String latitude,String longitude,String deviceId,
                              String workOrderId,String mapPath,String finalMapNmae,
                              String final_map_path,String msisdn,String floorPlanPath,String mapId) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ssid_name", ssidName);
            contentValues.put("floor_plan_name",floorPlanName);
            contentValues.put("final_map_name",finalMapNmae);
            contentValues.put("technology", tech);
            contentValues.put("flat_type", flatType);
            contentValues.put("opening", opening);
            contentValues.put("opening_type", openingType);
            contentValues.put("component", component);
            contentValues.put("survey", surveyFor);
            contentValues.put("location_type", locationType);
            contentValues.put("address", address);
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);
            contentValues.put("device_id", deviceId);
            contentValues.put("work_order_id", workOrderId);
            contentValues.put("map_path",mapPath);
            contentValues.put("floor_plan_path",floorPlanPath);
            contentValues.put("final_map_path",final_map_path);
            contentValues.put("msisdn",msisdn);
            contentValues.put("date_time", Utils.getDateTime());

            //     db.insert("Heat_Map_Table", null, contentValues);
            db.update("Heat_Map_Table", contentValues, "map_id" + mapId, null);
            Log.i(TAG,"HeatMap entry updated for id "+mapId);
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in HeatMap entry updation "+e.toString());
            e.printStackTrace();
        }
    }


    public void updateWalkMapPath(String walkMapPath, String mapId, String name, String pointsString, boolean walkMapWarning)
    {


        try {
            Log.i(TAG,"Points String is "+pointsString);
            ContentValues contentValues = new ContentValues();
            contentValues.put("map_path", walkMapPath);
            contentValues.put("map_name",name);
            contentValues.put("points",pointsString);
            if(walkMapWarning)
            contentValues.put("walkmap_warning",1);
            else
                contentValues.put("walkmap_warning",0);

            db.update("Heat_Map_Table", contentValues, "map_id=" + mapId, null);
            Log.i(TAG,"HeatMap entry updated for id "+mapId +" path "+walkMapPath +" walkMapWarning "+walkMapWarning);
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in HeatMap entry updation "+e.toString());
            e.printStackTrace();
        }
    }

    public void updateFinalMapDetails(String ssidName, String path, String name, String mapId, String walkMapPath, double d_percentage, double lg_percentage, double y_percentage, double r_percentage) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ssid_name", ssidName);
            contentValues.put("final_map_name",name);
            contentValues.put("final_map_path",path);
            contentValues.put("date_time", Utils.getDateTime());
            contentValues.put("excel_percent",d_percentage+"");
            contentValues.put("good_percent",lg_percentage+"");
            contentValues.put("fair_percent",y_percentage+"");
            contentValues.put("poor_percent",r_percentage+"");

           // contentValues.put("map_path",walkMapPath);
            //     db.insert("Heat_Map_Table", null, contentValues);
          //  db.update("GRAPHS_TABLE", initialValues, "dbId=" + dbid, null);
            db.update("Heat_Map_Table", contentValues, "map_id=" + mapId, null);
            Log.i(TAG,"HeatMap entry updated for id "+mapId +" path "+path +" dark green "+d_percentage +" yellow "+y_percentage +" light greeb "+lg_percentage+" red "+r_percentage );
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in HeatMap entry updation "+e.toString());
            e.printStackTrace();
        }
    }

    public void updateLsFinalMapPercentDetails( String mapId,double d_percentage, double lg_percentage, double y_percentage, double r_percentage) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ls_excel_percent",d_percentage+"");
            contentValues.put("ls_good_percent",lg_percentage+"");
            contentValues.put("ls_fair_percent",y_percentage+"");
            contentValues.put("ls_poor_percent",r_percentage+"");

            // contentValues.put("map_path",walkMapPath);
            //     db.insert("Heat_Map_Table", null, contentValues);
            //  db.update("GRAPHS_TABLE", initialValues, "dbId=" + dbid, null);
            db.update("Heat_Map_Table", contentValues, "map_id=" + mapId, null);
           // Utils.appendCrashLog("link speed HeatMap percent entry updated for id "+mapId  +" dark green "+d_percentage +" yellow "+y_percentage +" light greeb "+lg_percentage+" red "+r_percentage );

            Log.i(TAG,"link speed HeatMap percent entry updated for id "+mapId  +" dark green "+d_percentage +" yellow "+y_percentage +" light greeb "+lg_percentage+" red "+r_percentage );
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in link speed HeatMap entry updation "+e.toString());
            e.printStackTrace();
        }
    }
    public void updateLsFinalMapDetails(String path, String name, String mapId) {
        try {
            ContentValues contentValues = new ContentValues();
            //contentValues.put("ssid_name", ssidName);
            contentValues.put("ls_heatmap",name);
            contentValues.put("ls_heatmap_path",path);
            db.update("Heat_Map_Table", contentValues, "map_id=" + mapId, null);
          //  Log.i(TAG,"link speed HeatMap entry updated for id "+mapId +" path "+path +" dark green "+d_percentage +" yellow "+y_percentage +" light greeb "+lg_percentage+" red "+r_percentage );
        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in HeatMap entry updation "+e.toString());
            e.printStackTrace();
        }
    }
    public void updateExistingTestResult(String mapId, String id, String res, String testId) {
        try {
            String whereClause="map_id="+mapId+" AND "+"test_id='"+testId+"'"+" AND id="+id;
            ContentValues contentValues = new ContentValues();
            contentValues.put("test_results",res);
            db.update("TEST_RESULTS", contentValues, whereClause, null);

        }
        catch (Exception e)
        {
            Log.i(TAG,"Exception in TEST_RESULTS entry updation "+e.toString());
            e.printStackTrace();
        }
    }

    // by swapnil BANSAL 09/01/2023
    public ArrayList<HashMap<String, String>> selectInLoggingTable() {
        System.out.println(Config.getDateTime() + "  : via selectfromloggingagent Entered selectfromloggingagent method...");
        ArrayList<HashMap<String,String>> detailsList=new ArrayList<>();
        System.out.println("Detail list is selectInLoggingTable "+detailsList);
        try{
            open();
            String sql = "SELECT * FROM Logging WHERE status='init' LIMIT 10";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is "+rs);
            if (rs != null) {
                System.out.println("Cursor count is at 443 is at all " + rs.getCount() + " count");
            }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                String evt_type = rs.getString(rs.getColumnIndex("evt_type"));
                System.out.println("event type name is "+evt_type);
                String agent_output = rs.getString(rs.getColumnIndex("agent_output"));
                System.out.println("agent output  is "+agent_output);
                String date_time = rs.getString(rs.getColumnIndex("date_time"));
                System.out.println("date time is "+date_time);
                String status = rs.getString(rs.getColumnIndex("status"));
                System.out.println("status is from  "+status);
                hashMap.put("evt_type",evt_type);
                hashMap.put("date_time",date_time);
                hashMap.put("status",status);
                hashMap.put("agent_output",agent_output);
                detailsList.add(hashMap);
            }
        }
        catch (Exception e) {
            System.out.println(": via selectfromloggingagent"+e.getMessage());
            e.printStackTrace();

        }
        return detailsList;
    }
    // by swapnil BANSAL 09/01/2023
    public ArrayList<String> selectInLoggingTableNewJson() {
        System.out.println(Config.getDateTime() + "  : via selectfromloggingagent Entered selectfromloggingagent method...");
        ArrayList<String> detailsList=new ArrayList<>();
        System.out.println("Detail list is selectInLoggingTable "+detailsList);
        try{
            open();
            String sql = "SELECT * FROM Logging WHERE status='init' LIMIT 10";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is "+rs);
            if (rs != null) {
                System.out.println("Cursor count is at 443 is at all " + rs.getCount() + " count");
            }
            while (rs.moveToNext()) {
                HashMap hashMap=new HashMap<String,String>();
                //String agent_output = rs.getString(rs.getColumnIndex("agent_output"));
                //hashMap.put("agent_output",agent_output);
                detailsList.add(rs.getString(rs.getColumnIndex("agent_output")));
            }
        }
        catch (Exception e) {
            System.out.println(": via selectfromloggingagent"+e.getMessage());
            e.printStackTrace();

        }
        return detailsList;
    }
    

    public void updateLoggingData(String status) {

        String sql = "UPDATE Logging  SET status='"+status+"'";
        System.out.println("updated value is " + sql);
        try {
            db.execSQL(sql);
        } catch (Exception e) {;
            e.printStackTrace();
        }
    }
    public ArrayList<String> selectIPdetails1() {

        ArrayList<String> detailsList=new ArrayList<>();
        System.out.println("Detail list is "+detailsList);
        try {
            open();
            String sql = "SELECT  * FROM  ip_list";
            Cursor rs = db.rawQuery(sql, null);
            System.out.println("Cursor rs is " + rs);
            if (rs != null) { }
            while (rs.moveToNext()) {
                // HashMap hashMap=new HashMap<String,String>();
                String   ipDetails = rs.getString(rs.getColumnIndex("ip"));
                System.out.println(" ipDetails  is " +ipDetails);
                // hashMap.put("ip", ipDetails);
                detailsList.add(ipDetails);
            }
        } catch (Exception e) {
            System.out.println(": via selectUrlName() is " + e.getMessage());
            e.printStackTrace();

        }

        return detailsList;


    }

    public boolean insertInLoggingTable(String agent_name, String evt_type, String agent_output, String date_time, String status) {
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put("evt_type", evt_type);
            System.out.println(" event type  is......."+evt_type);
            contentValues.put("agent_output", agent_output);
            System.out.println("agent  output is......."+agent_output);
            contentValues.put("date_time", date_time);
            System.out.println("date time  is......."+date_time);
            contentValues.put("status", status);
            System.out.println("insertInLoggingTable status  is......."+status);
            long result = db.insert("Logging", null, contentValues);
            System.out.println("Result In insertInLoggingTable  is "+result);
            if (result == -1) {
                return false;
            } else {

            }


        }
        catch (Exception e) {
            System.out.println("db error in inserting " + e.toString());
            e.printStackTrace();
        }
        finally {
            close();
        }
        return false;
    }

}
