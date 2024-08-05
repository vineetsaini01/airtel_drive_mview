package com.newmview.wifi.bean;

import java.io.Serializable;

public class Pinger implements Serializable {
    String latency;
    String packetLoss;
    String host;
    String rrtMax;

    public String getRttDev() {
        return rttDev;
    }

    public void setRttDev(String rttDev) {
        this.rttDev = rttDev;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    String rttMin;
    String rttDev;
    String timeUnit;

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    public String getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(String packetLoss) {
        this.packetLoss = packetLoss;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRrtMax() {
        return rrtMax;
    }

    public void setRrtMax(String rrtMax) {
        this.rrtMax = rrtMax;
    }

    public String getRttMin() {
        return rttMin;
    }

    public void setRttMin(String rttMin) {
        this.rttMin = rttMin;
    }

    public String getRttAvg() {
        return rttAvg;
    }

    public void setRttAvg(String rttAvg) {
        this.rttAvg = rttAvg;
    }

    String rttAvg;

}
