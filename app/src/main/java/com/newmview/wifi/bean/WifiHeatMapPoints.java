package com.newmview.wifi.bean;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WifiHeatMapPoints implements Parcelable {
    private static final String TAG = "WifiHeatMapPoints";
    List<Point> points;
    List<FloatPoint> floatPoints;
    int color;
    int signalStrength;
    int linkSpeed;
    String gridId;
    String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSignalStrengthColor() {
        return signalStrengthColor;
    }

    public void setSignalStrengthColor(int signalStrengthColor) {
        this.signalStrengthColor = signalStrengthColor;
    }

    int signalStrengthColor;

    public int getLinkSpeedColor() {
        return linkSpeedColor;
    }

    public void setLinkSpeedColor(int linkSpeedColor) {
        this.linkSpeedColor = linkSpeedColor;
    }

    int linkSpeedColor;

    public String getQuadId() {
        return quadId;
    }

    public void setQuadId(String quadId) {
        Log.i(TAG,"Quad id being set is "+quadId);
        this.quadId = quadId;
    }

    String quadId;

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public WifiHeatMapPoints(Parcel in)
    {
        this.linkSpeed=in.readInt();
        this.color=in.readInt();
        this.signalStrength=in.readInt();
        this.quadId=in.readString();
        this.label=in.readString();
        /*floatPoints = new ArrayList<>();
        in.readList(floatPoints, WifiHeatMapPoints.class.getClassLoader());*/
        points=new ArrayList<>();
        in.readList(points,WifiHeatMapPoints.class.getClassLoader());
        //this.floatPoints=in.readList();
    }
    public WifiHeatMapPoints()
    {
    }




    public List<FloatPoint> getFloatPoints() {
        return floatPoints;
    }

    public void setFloatPoints(List<FloatPoint> floatPoints) {
        this.floatPoints = floatPoints;
    }

    String ssidName;

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public String getSsidName() {
        return ssidName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.linkSpeed);
        dest.writeInt(this.color);
        dest.writeInt(this.signalStrength);
        dest.writeString(this.quadId);
        dest.writeString(this.label);
        dest.writeList(this.points);
      //  dest.writeList(this.floatPoints);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WifiHeatMapPoints createFromParcel(Parcel in) {
            return new WifiHeatMapPoints(in);
        }

        public WifiHeatMapPoints[] newArray(int size) {
            return new WifiHeatMapPoints[size];
        }
    };
}

/*
    public class MyCreator implements Parcelable.Creator<WifiHeatMapPoints> {
        @Override
        public WifiHeatMapPoints createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public WifiHeatMapPoints[] newArray(int size) {
            return new WifiHeatMapPoints[0];
        }
*/
/*
    public static final Parcelable.Creator<WifiHeatMapPoints> CREATOR
            = new Parcelable.Creator<WifiHeatMapPoints>() {
        public WifiHeatMapPoints createFromParcel(Parcel in) {
            return new Track(in);
        }

        public Student[] newArray(int size) {
            return new Track[size];
        }
    };
*//*

}
*/
