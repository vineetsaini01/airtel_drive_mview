package com.newmview.wifi.bean;

public class WifiModel {
    String ssidName="Unknown";
    int linkSpeed;
    String wifiDetails;

    public boolean isSupporting5GHzBand() {
        return support5GHzBand;
    }

    public void setSupport5GHzBand(boolean support5GHzBand) {
        this.support5GHzBand = support5GHzBand;
    }

    boolean support5GHzBand;

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    String channelNo;

    public double getFrequencyBandwidth() {
        return frequencyBandwidth;
    }

    public void setFrequencyBandwidth(double frequencyBandwidth) {
        this.frequencyBandwidth = frequencyBandwidth;
    }

    double frequencyBandwidth;
    boolean isWifiOn;

    public boolean isWifiOn() {
        return isWifiOn;
    }

    public void setWifiOn(boolean wifiOn) {
        isWifiOn = wifiOn;
    }

    public String getSsidName() {
        return ssidName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    public int getLinkSpeed() {
        return linkSpeed;
    }

    public void setLinkSpeed(int linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
    public void setDetailsForWifi(String details)
    {
        wifiDetails=details;


    }

    public String getWifiDetails() {
        return wifiDetails;
    }

    int signalStrength;
    boolean isConnected;
    String BSSID;

    public void setIsConnected(boolean connected) {
        isConnected=connected;

    }
    public boolean isConnected()
    {
        return isConnected;
    }

    public void setBSSID(String BSSID) {
        this.BSSID=BSSID;
    }
    public String getBSSID()
    {
        return BSSID;
    }
}
