package com.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class GraphData {

    @SerializedName("graphId")
    private String graphId;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    @SerializedName("graphName")
    private String graphName;

    @Override
    public String toString() {
        // Return a meaningful representation of the DashboardEntity object
        return "GraphData{" +
                "graphId='" + graphId + '\'' +
                ", graphName='" + graphName +
                '}';
    }
}
