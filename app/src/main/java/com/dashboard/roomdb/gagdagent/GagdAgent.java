package com.dashboard.roomdb.gagdagent;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


public class GagdAgent {



    public GagdAgent(String id, String url, String agentName, String eventType, String agentStatus, String packetSize, String totalPackets,String user_type,String rule,String mobile,String third_party) {
        this.id = id;
        this.url = url;
        this.agentName = agentName;
        this.eventType = eventType;
        this.status = agentStatus;
        this.packetSize =packetSize;
        this.totalPackets = totalPackets;
        this.user_type = user_type;
        this.rule = rule;
        this.mobile = mobile;
        this.third_party = third_party;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @NonNull
    @PrimaryKey
    private String id;



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    private String url;
    private String agentName;
    private String eventType;

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    private String user_type;
    private String status;

    public String getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(String packetSize) {
        this.packetSize = packetSize;
    }

    public String getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(String totalPackets) {
        this.totalPackets = totalPackets;
    }

    private String packetSize;
    private String totalPackets;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String mobile;

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    private String rule;

    public String getThird_party() {
        return third_party;
    }

    public void setThird_party(String third_party) {
        this.third_party = third_party;
    }

    private String third_party;


    // Getters and Setters
}

