package com.geekofia.phonepolice.models;

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
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        }
        return "Intruder{" +
                "alertType='" + alertType + '\'' +
                ", filePath='" + imageFilePath + '\'' +
                '}';
    }
}
