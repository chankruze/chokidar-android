package com.geekofia.phonepolice.utils;

public class Constants {
    // Keys for SharedPreferences
    public static final String FULL_BATTERY_ALERT_SWITCH = "FULL_BATTERY_ALERT_SWITCH";
    public static final String FULL_BATTERY_ALERT_TONE_PICKER = "FULL_BATTERY_ALERT_TONE_PICKER";


    // Other constants
    public static final String API_BASE_URL = "https://api.example.com/";
    public static final int REQUEST_TIMEOUT = 5000;

    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
