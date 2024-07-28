package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.phonepolice.utils.Constants.CHARGER_REMOVAL_ALERT_NOTIFICATION_ID;
import static com.geekofia.phonepolice.utils.Constants.CHARGER_REMOVAL_ALERT_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
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
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.ChargerRemovalAlertActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class ChargerRemovalAlertService extends Service {
    private MediaPlayer mediaPlayer;
    private String currentAlertTone;
    private BroadcastReceiver chargerBroadcastReceiver;

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
            // register charger broadcast receiver
            registerBatteryReceiver();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a pending intent to open when clicked on the notification
        Intent notificationIntent = new Intent(this, ChargerRemovalAlertActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification required for the service to start
        Notification notification = new NotificationCompat.Builder(this, CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.title_charger_removal_alert))
                .setContentText(getString(R.string.desc_charger_removal_alert))
                .setSmallIcon(R.drawable.ic_charger_removal)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        // Start foreground service
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(CHARGER_REMOVAL_ALERT_SERVICE_ID, notification);
        } else {
            startForeground(CHARGER_REMOVAL_ALERT_SERVICE_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: disable flash
        // Cleanup the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Unregister the battery broadcast receiver if exists
        if (chargerBroadcastReceiver != null) {
            unregisterReceiver(chargerBroadcastReceiver);
        }
        // Dismiss the service with notification
        stopForeground(true);
    }

    private void registerBatteryReceiver() {
        chargerBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
                    // Get selected tone
                    String selectedTone = getSelectedTone(context, PreferenceKeyManager.getPreferenceKeyItem(Constants.CHARGER_REMOVAL_ALERT_TONE_PICKER).getKey());

                    // Check if currently selected tone is changed
                    if (!selectedTone.equals(currentAlertTone) || mediaPlayer == null) {
                        // Release current media player if tone has changed
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }
                        currentAlertTone = selectedTone;
                        mediaPlayer = setupMediaPlayer(context, currentAlertTone, true);
                    }
                    // If media player is already playing, start playing
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        // Create a new notification and swap it with existing one
                        Notification notification = new NotificationCompat.Builder(context, CHARGER_REMOVAL_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(getString(R.string.title_charger_removal_alert))
                                .setContentText("Charger is unplugged!")
                                .setSmallIcon(R.drawable.ic_charger_removal)
                                .setOngoing(true)
                                .build();
                        // Show the notification
                        showNotification(context, CHARGER_REMOVAL_ALERT_NOTIFICATION_ID, notification);
                    }
                } else {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        // Pause the media player
                        mediaPlayer.pause();
                        // Dismiss the notification
                        dismissNotification(context, CHARGER_REMOVAL_ALERT_NOTIFICATION_ID);
                    }
                }
            }
        };

        // Register the BroadcastReceiver for power action changes
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(chargerBroadcastReceiver, filter);
    }
}
