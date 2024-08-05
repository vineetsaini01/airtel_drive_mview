package com.dashboard.roomdb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.dashboard.model.GraphData;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "dashboard")
public class DashboardEntity {
    @NonNull
    public String getDbId() {
        return dbId;
    }

    public void setDbId(@NonNull String dbId) {
        this.dbId = dbId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<GraphData> getGraphData() {
        return graphData;
    }

    public void setGraphData(List<GraphData> graphData) {
        this.graphData = graphData;
    }

    @PrimaryKey
    @NonNull
    @SerializedName("dbId")
    private String dbId;

    @ColumnInfo(name = "dbName")
    @SerializedName("dbName")
    private String dbName;


    @SerializedName("graphData")
    private List<GraphData> graphData;
    @Override
    public String toString() {
        // Return a meaningful representation of the DashboardEntity object
        return "DashboardEntity{" +
                "dbId='" + dbId + '\'' +
                ", dbName='" + dbName + '\'' +
                ", graphData=" + graphData +
                '}';
    }
    // Constructor, getters, setters
}

