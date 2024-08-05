package com.newmview.wifi.bean;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.mview.airtel.R;
import com.newmview.wifi.customview.TouchImageView;
import com.newmview.wifi.other.Utils;

import java.io.Serializable;

public class MapModel implements Serializable {
    private static final String TAG ="MapModel" ;
    String mapId;
    String floorPlan;
    String ssidName;
    String technology;
    String flatType;
    String finalMapPath;
    String lsWalkMap;



    String lsWalkMapPath;
    int walkMapWarningIgnored;

    public String getPoints() {
        return points;
    }

    public int getWalkMapWarningIgnored() {
        return walkMapWarningIgnored;
    }

    public void setWalkMapWarningIgnored(int walkMapWarningIgnored) {
        this.walkMapWarningIgnored = walkMapWarningIgnored;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    String lsHeatMap;
    String lsHeatMapPath;
    String compareImgPath;
    String subscriberName;
    String subscriberId;
    String points;


    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }



    public String getCompareImgPath() {
        return compareImgPath;
    }

    public void setCompareImgPath(String compareImgPath) {
        this.compareImgPath = compareImgPath;
    }

    public String getLsWalkMap() {
        return lsWalkMap;
    }

    public void setLsWalkMap(String lsWalkMap) {
        this.lsWalkMap = lsWalkMap;
    }

    public String getLsWalkMapPath() {
        return lsWalkMapPath;
    }

    public void setLsWalkMapPath(String lsWalkMapPath) {
        this.lsWalkMapPath = lsWalkMapPath;
    }

    public String getLsHeatMap() {
        return lsHeatMap;
    }

    public void setLsHeatMap(String lsHeatMap) {
        this.lsHeatMap = lsHeatMap;
    }

    public String getLsHeatMapPath() {
        return lsHeatMapPath;
    }

    public void setLsHeatMapPath(String lsHeatMapPath) {
        this.lsHeatMapPath = lsHeatMapPath;
    }

    public String getLsExcellentCoveragePercentage() {
        return lsExcellentCoveragePercentage;
    }

    public void setLsExcellentCoveragePercentage(String lsExcellentCoveragePercentage) {
        this.lsExcellentCoveragePercentage = lsExcellentCoveragePercentage;
    }

    public String getLsGoodCoveragePercentage() {
        return lsGoodCoveragePercentage;
    }

    public void setLsGoodCoveragePercentage(String lsGoodCoveragePercentage) {
        this.lsGoodCoveragePercentage = lsGoodCoveragePercentage;
    }

    public String getLsFairCoveragePercentage() {
        return lsFairCoveragePercentage;
    }

    public void setLsFairCoveragePercentage(String lsFairCoveragePercentage) {
        this.lsFairCoveragePercentage = lsFairCoveragePercentage;
    }

    public String getLsPoorCoveragePercentage() {
        return lsPoorCoveragePercentage;
    }

    public void setLsPoorCoveragePercentage(String lsPoorCoveragePercentage) {
        this.lsPoorCoveragePercentage = lsPoorCoveragePercentage;
    }

    String lsExcellentCoveragePercentage;
    String lsGoodCoveragePercentage;
    String lsFairCoveragePercentage;
    String lsPoorCoveragePercentage;


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String floorPlanPath;
    String excellentCoveragePercentage;
    String state;

    public String getExcellentCoveragePercentage() {
        return excellentCoveragePercentage;
    }

    public void setExcellentCoveragePercentage(String excellentCoveragePercentage) {
        this.excellentCoveragePercentage = excellentCoveragePercentage;
    }

    public String getGoodCoveragePercentage() {
        return goodCoveragePercentage;
    }

    public void setGoodCoveragePercentage(String goodCoveragePercentage) {
        this.goodCoveragePercentage = goodCoveragePercentage;
    }

    public String getFairCoveragePercentage() {
        return fairCoveragePercentage;
    }

    public void setFairCoveragePercentage(String fairCoveragePercentage) {
        this.fairCoveragePercentage = fairCoveragePercentage;
    }

    public String getPoorCoveragePercentage() {
        return poorCoveragePercentage;
    }

    public void setPoorCoveragePercentage(String poorCoveragePercentage) {
        this.poorCoveragePercentage = poorCoveragePercentage;
    }

    String goodCoveragePercentage;
    String fairCoveragePercentage;
    String poorCoveragePercentage;
    public String getWifiX() {
        return wifiX;
    }

    public void setWifiX(String wifiX) {
        this.wifiX = wifiX;
    }

    public String getWifiY() {
        return wifiY;
    }

    public void setWifiY(String wifiY) {
        this.wifiY = wifiY;
    }

    String finalMapName;
    String wifiX;
    String wifiY;

    public String getWalkMapName() {
        return walkMapName;
    }

    public void setWalkMapName(String walkMapName) {
        this.walkMapName = walkMapName;
    }

    String walkMapName;

    public String getFinalMapPath() {
        return finalMapPath;
    }

    public void setFinalMapPath(String finalMapPath) {
        this.finalMapPath = finalMapPath;
    }

    public String getFloorPlanPath() {
        return floorPlanPath;
    }

    public void setFloorPlanPath(String floorPlanPath) {
        this.floorPlanPath = floorPlanPath;
    }

    public String getFinalMapName() {
        return finalMapName;
    }

    public void setFinalMapName(String finalMapName) {
        this.finalMapName = finalMapName;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    String msisdn;


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    String opening;
    String dateTime;

    public String getMapPath() {
        return mapPath;
    }

    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    String openingType;
    String mapPath;

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getOpeningType() {
        return openingType;
    }

    public void setOpeningType(String openingType) {
        this.openingType = openingType;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    String component;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    String surveyFor;
    String locationType;
    String address;
    String latitude;

    public String getFloorPlan() {
        return floorPlan;
    }

    public void setFloorPlan(String floorPlan) {
        this.floorPlan = floorPlan;
    }

    public String getSsidName() {
        return ssidName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }

    public String getSurveyFor() {
        return surveyFor;
    }

    public void setSurveyFor(String surveyFor) {
        this.surveyFor = surveyFor;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }

    String longitude;
    String deviceId;
    String workOrderId;

    @BindingAdapter("mapImage")
    public static void loadImage(TouchImageView view, String imageUrl) {
        Context context=view.getContext();
        Log.i(TAG,"url is "+imageUrl);
        if(Utils.checkifavailable(imageUrl)) {
            Utils.loadImage(context, view, imageUrl, null, R.drawable.placeholder_image, false);
        }
        else
        {

            view.setImageDrawable(context.getResources().getDrawable(R.drawable.placeholder_image));
        }
       view.setZoom(1);

}

    @BindingAdapter("textValue")
public static void setTextValue(TextView textView, String text)
{
    textView.setText("Survey Id : "+text);

}
    @BindingAdapter("warning")
public static void setWarningText(TextView textView,int walkMapWarningIgnored)
{
    if(walkMapWarningIgnored==1)
    {
        textView.setText("No");
    }
    else
    {
        textView.setText("Yes");
    }
}
}
