package com.geekofia.phonepolice.utils;

public class Constants {
    // Keys for SharedPreferences
    public static final String FULL_BATTERY_ALERT_SWITCH = "FULL_BATTERY_ALERT_SWITCH";
    public static final String FULL_BATTERY_ALERT_TONE_PICKER = "FULL_BATTERY_ALERT_TONE_PICKER";
    public static final String ANTI_TOUCH_ALERT_SWITCH = "ANTI_TOUCH_ALERT_SWITCH";
    public static final String ANTI_TOUCH_ALERT_TONE_PICKER = "ANTI_TOUCH_ALERT_TONE_PICKER";
    public static final String POCKET_ALARM_SWITCH = "POCKET_ALARM_SWITCH";
    public static final String POCKET_ALARM_TONE_PICKER = "POCKET_ALARM_TONE_PICKER";

    // Service Ids
    public static final int FULL_BATTERY_ALERT_SERVICE_ID = 6767;
    public static final int ANTI_TOUCH_ALERT_SERVICE_ID = 6768;
    public static final int POCKET_ALARM_SERVICE_ID = 6769;

    // Notification Ids
    public static final int FULL_BATTERY_ALERT_NOTIFICATION_ID = 67671;
    public static final int ANTI_TOUCH_ALERT_NOTIFICATION_ID = 67681;
    public static final int POCKET_ALARM_NOTIFICATION_ID = 67691;
    public static final String FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL";
    public static final String ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL";
    public static final String POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID = "POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL";

    // Logging TAGs
    public static final String FULL_BATTERY_ALERT_SERVICE_TAG = "FullBatteryAlertService";
    public static final String POCKET_ALARM_SERVICE_TAG = "PocketAlarmService";
    public static final String ANTI_TOUCH_ALERT_SERVICE_TAG = "AntiTouchAlertService";

    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
