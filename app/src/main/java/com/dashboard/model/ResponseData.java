package com.dashboard.model;

import com.google.gson.annotations.SerializedName;

public class ResponseData {

    @SerializedName("status")
    private String status;

    public String getStatus() {
        return status;
    }

}
