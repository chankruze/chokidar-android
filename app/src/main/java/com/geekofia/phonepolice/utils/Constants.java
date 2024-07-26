package com.geekofia.phonepolice.utils;

public class Constants {
    // Keys for SharedPreferences
    public static final String PREF_ENABLE_DISABLE_SWITCH = "enable_disable_switch";

    // Other constants
    public static final String API_BASE_URL = "https://api.example.com/";
    public static final int REQUEST_TIMEOUT = 5000;

    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
