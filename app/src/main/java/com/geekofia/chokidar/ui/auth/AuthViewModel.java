package com.geekofia.chokidar.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.geekofia.chokidar.data.models.Device;
import com.geekofia.chokidar.data.models.LoginRequest;
import com.geekofia.chokidar.data.models.ApiResponse;
import com.geekofia.chokidar.data.repositories.DeviceRepository;
import com.geekofia.chokidar.network.ApiResponseCallback;

public class AuthViewModel extends ViewModel {

    private final DeviceRepository deviceRepository;
    private final MutableLiveData<Device> deviceLiveData = new MutableLiveData<>();
    private final MutableLiveData<ApiResponse> loginLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AuthViewModel() {
        deviceRepository = new DeviceRepository();
    }

    public LiveData<Device> getDeviceLiveData() {
        return deviceLiveData;
    }

    public LiveData<ApiResponse> getLoginLiveData() {
        return loginLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void authenticateDevice(LoginRequest loginRequest) {
        deviceRepository.authenticateDevice(loginRequest, new ApiResponseCallback<ApiResponse>() {
            @Override
            public void onSuccess(ApiResponse result) {
                loginLiveData.postValue(result);
            }

            @Override
            public void onError(Throwable error) {
                errorLiveData.postValue(error.getMessage());
            }
        });
    }
}
