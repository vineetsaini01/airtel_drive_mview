package com.newmview.wifi.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.newmview.wifi.bean.MapModel;
import com.newmview.wifi.repository.MapListRepository;

import java.util.List;

public class MapVM extends ViewModel {
    private final MutableLiveData<List<MapModel>> mapListObservable;

    public MapVM() {
        mapListObservable= MapListRepository.getInstance().getMapList();
    }
    public MutableLiveData<List<MapModel>> getMapListObservable()
    {
        return mapListObservable;
    }
 public boolean removePlan(String id)
    {
        boolean successfullResult=MapListRepository.getInstance().removePlan(id);
        if(successfullResult)
        refresh();
        return successfullResult;
    }
    public void refresh() {
        MapListRepository.getInstance().getMapList();
    }
    public long insertNewMap(MapModel mapModel) {
        return MapListRepository.getInstance().insertNewMapData(mapModel);
    }
    public MapModel readDetailsAtMapId(String mapId) {
       return MapListRepository.getInstance().getMapEntryAt(mapId);
    }

}
