package com.geekofia.chokidar.data.models;

import androidx.annotation.NonNull;

import java.time.format.DateTimeFormatter;

public class Intruder {
    private final String imageFilePath;
    private final String alertType;

    public Intruder(String imageFilePath, String alertType) {
        this.imageFilePath = imageFilePath;
        this.alertType = alertType;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public String getAlertType() {
        return alertType;
    }

    @NonNull
    @Override
    public String toString() {
        return "Intruder{" +
                "alertType='" + alertType + '\'' +
                ", filePath='" + imageFilePath + '\'' +
                '}';
    }
}
