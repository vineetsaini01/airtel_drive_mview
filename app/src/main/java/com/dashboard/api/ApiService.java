package com.dashboard.api;

import com.dashboard.model.DashboardApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("RequestHandler")
    Call<DashboardApiResponse> getDashboardData(
            @Field("code_request") String codeRequest,
            @Field("userId") String userId
    );
}
