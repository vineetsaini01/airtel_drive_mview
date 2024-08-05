package com.dashboard.roomdb.gagdagent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class LoggingAgent {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String agentName;
    private String evtType;
    private String agentOutput;
    private String dateTime;
    private String status;

    public LoggingAgent(String agentName, String evtType, String agentOutput, String dateTime, String status) {
        this.agentName = agentName;
        this.evtType = evtType;
        this.agentOutput = agentOutput;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getEvtType() {
        return evtType;
    }

    public void setEvtType(String evtType) {
        this.evtType = evtType;
    }

    public String getAgentOutput() {
        return agentOutput;
    }

    public void setAgentOutput(String agentOutput) {
        this.agentOutput = agentOutput;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}