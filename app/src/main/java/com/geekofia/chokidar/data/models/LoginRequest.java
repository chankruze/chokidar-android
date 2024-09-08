package com.geekofia.chokidar.data.models;

public class LoginRequest {
    private String deviceId;
    private String devicePin;

    public LoginRequest(String deviceId, String devicePin) {
        this.deviceId = deviceId;
        this.devicePin = devicePin;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDevicePin() {
        return devicePin;
    }

    public void setDevicePin(String devicePin) {
        this.devicePin = devicePin;
    }
}
