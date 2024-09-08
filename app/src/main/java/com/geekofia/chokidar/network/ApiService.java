package com.geekofia.chokidar.network;

import com.geekofia.chokidar.data.models.LoginRequest;
import com.geekofia.chokidar.data.models.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/devices/auth")
    Call<ApiResponse> authenticateDevice(@Body LoginRequest loginRequest);
}
