package com.geekofia.phonepolice.utils;

public class Constants {
    // Keys for SharedPreferences
    public static final String FULL_BATTERY_ALERT_SWITCH = "FULL_BATTERY_ALERT_SWITCH";
    public static final String FULL_BATTERY_ALERT_TONE_PICKER = "FULL_BATTERY_ALERT_TONE_PICKER";
    public static final String ANTI_TOUCH_ALERT_SWITCH = "ANTI_TOUCH_ALERT_SWITCH";
    public static final String ANTI_TOUCH_ALERT_TONE_PICKER = "ANTI_TOUCH_ALERT_TONE_PICKER";
    public static final String ANTI_TOUCH_ALERT_SENSITIVITY = "ANTI_TOUCH_ALERT_SENSITIVITY";
    public static final String POCKET_ALARM_SWITCH = "POCKET_ALARM_SWITCH";
    public static final String POCKET_ALARM_TONE_PICKER = "POCKET_ALARM_TONE_PICKER";
    public static final String CHARGER_REMOVAL_ALERT_SWITCH = "CHARGER_REMOVAL_ALERT_SWITCH";
    public static final String CHARGER_REMOVAL_ALERT_TONE_PICKER = "CHARGER_REMOVAL_ALERT_TONE_PICKER";
    public static final String WRONG_PASSWORD_ALERT_SWITCH = "WRONG_PASSWORD_ALERT_SWITCH";
    public static final String WRONG_PASSWORD_ALERT_TONE_PICKER = "WRONG_PASSWORD_ALERT_TONE_PICKER";
    public static final String WRONG_PASSWORD_ALERT_TRIGGER_PICKER = "WRONG_PASSWORD_ALERT_TRIGGER_PICKER";
    public static final String WRONG_PASSWORD_ALERT_MAX_ATTEMPTS="WRONG_PASSWORD_ALERT_MAX_ATTEMPTS";
    public static final String WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS="WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS";

    // Service Ids
    public static final int FULL_BATTERY_ALERT_SERVICE_ID = 6767;
    public static final int ANTI_TOUCH_ALERT_SERVICE_ID = 6768;
    public static final int POCKET_ALARM_SERVICE_ID = 6769;
    public static final int CHARGER_REMOVAL_ALERT_SERVICE_ID = 6770;
    public static final int WRONG_PASSWORD_ALERT_SERVICE_ID = 6771;

    // Notification Ids
    public static final int FULL_BATTERY_ALERT_NOTIFICATION_ID = 67671;
    public static final int ANTI_TOUCH_ALERT_NOTIFICATION_ID = 67681;
    public static final int POCKET_ALARM_NOTIFICATION_ID = 67691;
    public static final int CHARGER_REMOVAL_ALERT_NOTIFICATION_ID = 67701;
    public static final int WRONG_PASSWORD_ALERT_NOTIFICATION_ID = 67711;

    // Notification channels
    public static final String FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL";
    public static final String ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL";
    public static final String POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID = "POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL";
    public static final String CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL";
    public static final String WRONG_PASSWORD_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID = "WRONG_PASSWORD_ALERT_SERVICE_NOTIFICATION_CHANNEL";

    // Logging TAGs
    public static final String FULL_BATTERY_ALERT_SERVICE_TAG = "FullBatteryAlertService";
    public static final String POCKET_ALARM_SERVICE_TAG = "PocketAlarmService";
    public static final String ANTI_TOUCH_ALERT_SERVICE_TAG = "AntiTouchAlertService";
    public static final String CHARGER_REMOVAL_ALERT_SERVICE_TAG = "ChargerRemovalAlertService";
    public static final String WRONG_PASSWORD_ALERT_RECEIVER_TAG = "WrongPasswordAlertReceiver";
    public static final String WRONG_PASSWORD_ALERT_SERVICE_TAG = "WrongPasswordAlertService";

    // Prevent instantiation
    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
