package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;

import static com.geekofia.phonepolice.utils.Constants.FULL_BATTERY_ALERT_NOTIFICATION_ID;
import static com.geekofia.phonepolice.utils.Constants.FULL_BATTERY_ALERT_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Utils.createNotificationChannel;
import static com.geekofia.phonepolice.utils.Utils.dismissNotification;
import static com.geekofia.phonepolice.utils.Utils.getSelectedTone;
import static com.geekofia.phonepolice.utils.Utils.setupMediaPlayer;
import static com.geekofia.phonepolice.utils.Utils.showNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.FullBatteryAlertActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;


public class FullBatteryAlertService extends Service {
    private MediaPlayer mediaPlayer;
    private String currentAlertTone;
    private BroadcastReceiver batteryBroadcastReceiver;

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
                FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Full Battery Alert",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for full battery alert");

        // Check if the full battery alert switch is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFullBatteryAlertEnabled = prefs.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.FULL_BATTERY_ALERT_SWITCH).getKey(), false);

        if (isFullBatteryAlertEnabled) {
            // register battery broadcast receiver
            registerBatteryReceiver();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a pending intent to open when clicked on the notification
        Intent notificationIntent = new Intent(this, FullBatteryAlertActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification required for the service to start
        Notification notification = new NotificationCompat.Builder(this, FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Full Battery Alert")
                .setContentText("Full battery alert feature is now active")
                .setSmallIcon(R.drawable.outline_battery_charging_full_24)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        // Start foreground service
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(FULL_BATTERY_ALERT_SERVICE_ID, notification);
        } else {
            startForeground(FULL_BATTERY_ALERT_SERVICE_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }
        return START_STICKY;
    }

    private void registerBatteryReceiver() {
        batteryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;
                int level = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                float batteryPct = level / (float) scale * 100;

                Log.d(this.getClass().getName(), "Battery level: " + batteryPct);

                if (isCharging && batteryPct >= 100) {
                    // Get selected tone
                    String selectedTone = getSelectedTone(context, PreferenceKeyManager.getPreferenceKeyItem(Constants.FULL_BATTERY_ALERT_TONE_PICKER).getKey());

                    // Check if currently selected tone is changed
                    if (!selectedTone.equals(currentAlertTone) || mediaPlayer == null) {
                        // Release current media player if tone has changed
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        currentAlertTone = selectedTone;
                        mediaPlayer = setupMediaPlayer(context, currentAlertTone);
                    }
                    // If media player is already playing, start playing
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        // Create a new notification and swap it with existing one
                        Notification notification = new NotificationCompat.Builder(context, FULL_BATTERY_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                                .setContentTitle("Full Battery Alert")
                                .setContentText("Your device is fully charged! Please remove the charger.")
                                .setSmallIcon(R.drawable.outline_battery_charging_full_24)
                                .setOngoing(true)
                                .build();
                        // Show the notification
                        showNotification(context, FULL_BATTERY_ALERT_NOTIFICATION_ID, notification);
                    }
                } else {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        // Pause the media player
                        mediaPlayer.pause();
                        // Dismiss the notification
                        dismissNotification(context, FULL_BATTERY_ALERT_NOTIFICATION_ID);
                    }
                }
            }
        };

        // Register the BroadcastReceiver for battery changes
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Unregister the battery broadcast receiver if exists
        if (batteryBroadcastReceiver != null) {
            unregisterReceiver(batteryBroadcastReceiver);
        }
        // Dismiss the service with notification
        stopForeground(true);
    }
}
