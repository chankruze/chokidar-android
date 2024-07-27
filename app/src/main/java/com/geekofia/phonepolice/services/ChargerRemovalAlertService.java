package com.geekofia.phonepolice.services;

import static com.geekofia.phonepolice.utils.Constants.CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Utils.createNotificationChannel;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class ChargerRemovalAlertService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // set up the notification channel early
        createNotificationChannel(
                this,
                CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Charger Removal Alert",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for charger removal alert");

        // Check if the full battery alert switch is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isChargerRemovalAlertEnabled = prefs.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.CHARGER_REMOVAL_ALERT_SWITCH).getKey(), false);

        if (isChargerRemovalAlertEnabled) {
            // TODO: register sensor listener
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: pause media player
        // TODO: disable flash
        // Dismiss the service with notification
        stopForeground(true);
    }
}
