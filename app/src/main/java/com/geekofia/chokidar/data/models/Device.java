package com.geekofia.chokidar.data.models;

public class Device {
    private String id;
    private String deviceId;
    private String pin;

    // Constructors, getters, and setters
    public Device(String id, String deviceId, String pin) {
        this.id = id;
        this.deviceId = deviceId;
        this.pin = pin;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}
