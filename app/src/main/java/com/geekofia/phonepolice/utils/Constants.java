package com.geekofia.phonepolice.utils;

public class Constants {
    // Keys for SharedPreferences
    public static final String FULL_BATTERY_ALERT_SWITCH = "FULL_BATTERY_ALERT_SWITCH";
    public static final String FULL_BATTERY_ALERT_TONE_PICKER = "FULL_BATTERY_ALERT_TONE_PICKER";

    // Service Ids
    public static final int BATTERY_SERVICE_ID = 6767;

    // Notification Ids
    public static final int FULL_BATTERY_NOTIFICATION_ID = 67671;
    public static final String BATTERY_SERVICE_NOTIFICATION_CHANNEL_ID = "BATTERY_SERVICE_NOTIFICATION_CHANNEL";

    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
