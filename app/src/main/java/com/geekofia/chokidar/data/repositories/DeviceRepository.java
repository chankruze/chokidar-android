package com.geekofia.chokidar.data.repositories;

import androidx.annotation.NonNull;

import com.geekofia.chokidar.data.models.LoginRequest;
import com.geekofia.chokidar.data.models.ApiResponse;
import com.geekofia.chokidar.network.ApiResponseCallback;
import com.geekofia.chokidar.network.ApiService;
import com.geekofia.chokidar.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceRepository {

    private final ApiService apiService;

    public DeviceRepository() {
        apiService = RetrofitInstance.getApiService();
    }

    public void authenticateDevice(LoginRequest loginRequest, final ApiResponseCallback<ApiResponse> callback) {
        Call<ApiResponse> call = apiService.authenticateDevice(loginRequest);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Exception("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                callback.onError(t);
            }
        });
    }
}
