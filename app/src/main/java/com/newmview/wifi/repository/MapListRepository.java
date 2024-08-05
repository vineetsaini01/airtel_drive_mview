package com.newmview.wifi.repository;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newmview.wifi.application.MviewApplication;
import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.bean.WifiHeatMapPoints;
import com.newmview.wifi.database.DB_handler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapListRepository {
    private static final String TAG = "MapListRepository";
    private static MapListRepository mapListRepo;
    private List<MapModel> mapList=new ArrayList<>();
    final MediatorLiveData<List<MapModel>> mData = new MediatorLiveData<>();
    private MapModel mapDetails;

    public static MapListRepository getInstance() {
        if (mapListRepo == null) {
            mapListRepo = new MapListRepository();
        }
        return mapListRepo;
    }

public boolean removePlan(String mapId)
{
    boolean successfull=false;
    DB_handler db_handler=new DB_handler(MviewApplication.ctx);
    try {
        db_handler.open();
        db_handler.deletePlan(mapId);
        db_handler.close();
        successfull=true;
    }
    catch (Exception e)
    {
        e.printStackTrace();

    }
    return successfull;
}
public long insertNewMapData(MapModel mapModel)
{
    long success=-1;
    DB_handler db_handler=new DB_handler(MviewApplication.ctx);
    try {
        db_handler.open();
         success = db_handler.insertMapData(mapModel);
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    finally {
        db_handler.close();
    }
    return success;
}
    public MutableLiveData<List<MapModel>> getMapList()
    {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        try {
            db_handler.open();
            mapList = db_handler.readMapData();
            for(int i=0;i<mapList.size();i++)
            {
                Log.i(TAG,"Final name"+mapList.get(i).getFinalMapName()
                        +" final path "+mapList.get(i).getFinalMapPath()
                        +" floor plan name "+mapList.get(i).getFloorPlan()
                        +" floor plan path "+mapList.get(i).getFloorPlanPath()
                +" map id "+mapList.get(i).getMapId() +" floor type "+mapList.get(i).getFlatType());
            }
            if (mapList.size() > 0) {
                Gson gson = new Gson();
                String latestPoints = mapList.get(0).getPoints();
                Type type = new TypeToken<List<WifiHeatMapPoints>>() {
                }.getType();

                List<WifiHeatMapPoints> finalOutputString = gson.fromJson(latestPoints, type);
                if(finalOutputString!=null)
                {
                    if(finalOutputString.size()>0)
                    {
                        for (int i = 0; i < finalOutputString.size(); i++) {
                            Log.i(TAG, "Values for index " + i + " ss " +
                                    finalOutputString.get(i).getSignalStrength() + " points " +
                                    finalOutputString.get(i).getFloatPoints());
                        }

                    }
                }
            }
            db_handler.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mData.setValue(mapList);
        return mData;
    }

    public MapModel getMapEntryAt(String mapId) {
        DB_handler db_handler=new DB_handler(MviewApplication.ctx);
        db_handler.open();
        List<MapModel> mapModelList=db_handler.readMapDataForMapId(mapId);
        if(mapModelList!=null)
        {
            if(mapModelList.size()>0)
            {
                mapDetails= mapModelList.get(0);
            }
        }

        db_handler.close();
        return mapDetails;

    }
}
