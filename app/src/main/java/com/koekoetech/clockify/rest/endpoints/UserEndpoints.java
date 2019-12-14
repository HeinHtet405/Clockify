package com.koekoetech.clockify.rest.endpoints;

import com.koekoetech.clockify.models.UserInfo;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserEndpoints {

    @GET("user")
    Call<UserInfo> getUserInfo();

}
