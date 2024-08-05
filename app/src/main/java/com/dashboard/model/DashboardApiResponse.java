package com.dashboard.model;

import com.dashboard.roomdb.DashboardEntity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardApiResponse {


        @SerializedName("response")
        private ResponseData responseStatus;

        @SerializedName("dashboardData")
        private List<DashboardEntity> dashboardData;

        public ResponseData getResponseStatus() {
            return responseStatus;
        }

        public List<DashboardEntity> getDashboardData() {
            return dashboardData;
        }


}
