package com.geekofia.phonepolice.utils;


import com.geekofia.phonepolice.models.PreferenceKeyItem;

import java.util.HashMap;
import java.util.Map;

public class PreferenceKeyManager {
    private static final Map<String, PreferenceKeyItem> preferenceKeyItemHashMap = new HashMap<>();

    static {
        // FullBatteryAlert Preferences
        preferenceKeyItemHashMap.put(Constants.FULL_BATTERY_ALERT_SWITCH, new PreferenceKeyItem("full_battery_alert_switch", "Full Battery Alert"));
        preferenceKeyItemHashMap.put(Constants.FULL_BATTERY_ALERT_TONE_PICKER, new PreferenceKeyItem("full_battery_alert_tone_picker", "Full Battery Alert Tone"));

        // PocketAlarm Preferences
        preferenceKeyItemHashMap.put(Constants.POCKET_ALARM_SWITCH, new PreferenceKeyItem("pocket_alarm_switch", "Pocket Alarm"));
        preferenceKeyItemHashMap.put(Constants.POCKET_ALARM_TONE_PICKER, new PreferenceKeyItem("pocket_alarm_tone_picker", "Pocket Alarm Tone"));

        // AntiTouchAlert Preferences
        preferenceKeyItemHashMap.put(Constants.ANTI_TOUCH_ALERT_SWITCH, new PreferenceKeyItem("anti_touch_alert_switch", "Anti Touch Alert"));
        preferenceKeyItemHashMap.put(Constants.ANTI_TOUCH_ALERT_TONE_PICKER, new PreferenceKeyItem("anti_touch_alert_tone_picker", "Anti Touch Alert Tone"));
        preferenceKeyItemHashMap.put(Constants.ANTI_TOUCH_ALERT_SENSITIVITY, new PreferenceKeyItem("anti_touch_alert_sensitivity", "Anti Touch Alert Sensitivity"));

        // AntiTouchAlert Preferences
        preferenceKeyItemHashMap.put(Constants.CHARGER_REMOVAL_ALERT_SWITCH, new PreferenceKeyItem("charger_removal_alert_switch", "Charger Removal Alert"));
        preferenceKeyItemHashMap.put(Constants.CHARGER_REMOVAL_ALERT_TONE_PICKER, new PreferenceKeyItem("charger_removal_alert_tone_picker", "Charger Removal Alert Tone"));

        // WrongPasswordAlert Preferences
        preferenceKeyItemHashMap.put(Constants.WRONG_PASSWORD_ALERT_SWITCH, new PreferenceKeyItem("wrong_password_alert_switch", "Wrong Password Alert"));
        preferenceKeyItemHashMap.put(Constants.WRONG_PASSWORD_ALERT_TONE_PICKER, new PreferenceKeyItem("wrong_password_alert_tone_picker", "Wrong Password Alert Tone"));        preferenceKeyItemHashMap.put(Constants.WRONG_PASSWORD_ALERT_TRIGGER_PICKER, new PreferenceKeyItem("wrong_password_alert_trigger_picker", "Wrong Password Alert Tone"));preferenceKeyItemHashMap.put(Constants.WRONG_PASSWORD_ALERT_MAX_ATTEMPTS, new PreferenceKeyItem("wrong_password_alert_max_attempts", "Wrong Password Alert Max Attempts"));preferenceKeyItemHashMap.put(Constants.WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS, new PreferenceKeyItem("wrong_password_alert_failed_attempts", "Wrong Password Alert Failed Attempts"));
    }

    public static PreferenceKeyItem getPreferenceKeyItem(String key) {
        return preferenceKeyItemHashMap.get(key);
    }
}
