package com.newmview.wifi.bean;

import com.newmview.wifi.customview.CustomHeatMapView;

import java.util.List;

public class MarkerTestResults {
    String id;
    boolean touched;
    public  float x;
    public  float y;

    public boolean isTouched() {
        return touched;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CustomHeatMapView.Vector2> getMarkerResults() {
        return markerResults;
    }

    public void setMarkerResults(List<CustomHeatMapView.Vector2> markerResults) {
        this.markerResults = markerResults;
    }

    List<CustomHeatMapView.Vector2> markerResults;
}
