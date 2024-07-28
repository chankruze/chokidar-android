package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_NOTIFICATION_ID;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_TAG;
import static com.geekofia.phonepolice.utils.Utils.createNotificationChannel;
import static com.geekofia.phonepolice.utils.Utils.dismissNotification;
import static com.geekofia.phonepolice.utils.Utils.getSelectedTone;
import static com.geekofia.phonepolice.utils.Utils.setupMediaPlayer;
import static com.geekofia.phonepolice.utils.Utils.showNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.PocketAlarmActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class PocketAlarmService extends Service implements SensorEventListener {
    private Sensor proximitySensor;
    private SensorManager sensorManager;
    private MediaPlayer mediaPlayer;
    private String currentAlertTone;

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
                POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Pocket Alarm",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for pocket alarm");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(POCKET_ALARM_SERVICE_TAG, "onStartCommand executed");
        // Check if the pocket alarm switch is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPocketAlarmEnabled = prefs.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.POCKET_ALARM_SWITCH).getKey(), false);

        if (isPocketAlarmEnabled) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

            if (proximitySensor != null) {
                sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e(POCKET_ALARM_SERVICE_TAG, "Proximity sensor not available.");
                stopSelf();
            }

            // Create a pending intent to open when clicked on the notification
            Intent notificationIntent = new Intent(this, PocketAlarmActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            // Create the notification required for the service to start
            Notification notification = new NotificationCompat.Builder(this, POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID)
                    .setContentTitle("Pocket Alarm")
                    .setContentText("Pocket alarm feature is now active")
                    .setSmallIcon(R.drawable.ic_pocket_alarm)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();

            // Start foreground service
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(POCKET_ALARM_SERVICE_ID, notification);
            } else {
                startForeground(POCKET_ALARM_SERVICE_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: disable flash
        // TODO: set pocket status false
        super.onDestroy();
        // Cleanup the media player
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // Dismiss the service with notification
        stopForeground(true);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float distance = event.values[0];
            System.out.println("Distance: " + distance + " getMaximumRange: " + proximitySensor.getMaximumRange());

            if (distance < proximitySensor.getMaximumRange()) {
                // The sensor is covered (e.g., phone in pocket)
                Log.i(POCKET_ALARM_SERVICE_TAG, "Phone is in pocket.");

                // TODO: implement flash light disable

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // Pause the media player
                    mediaPlayer.pause();
                    // Dismiss the notification
                    dismissNotification(this, POCKET_ALARM_NOTIFICATION_ID);
                }
            } else {
                // The sensor is uncovered (e.g., phone is not in pocket)
                Log.i(POCKET_ALARM_SERVICE_TAG, "Phone is not in pocket.");

                // TODO: implement flash light enable
                // TODO: implement vibration

                // Get the selected tone
                String selectedTone = getSelectedTone(this, PreferenceKeyManager.getPreferenceKeyItem(Constants.POCKET_ALARM_TONE_PICKER).getKey());

                // Check if currently selected tone is changed
                if (!selectedTone.equals(currentAlertTone) || mediaPlayer == null) {
                    // Release current media player if tone has changed
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    currentAlertTone = selectedTone;
                    mediaPlayer = setupMediaPlayer(this, currentAlertTone);
                }
                // If media player is already playing, start playing
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    // Create a new notification and swap it with existing one
                    Notification notification = new NotificationCompat.Builder(this, POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Pocket Alarm")
                            .setContentText("Your device is not in the pocket.")
                            .setSmallIcon(R.drawable.ic_pocket_alarm)
                            .setOngoing(true)
                            .build();
                    // Show the notification
                    showNotification(this, POCKET_ALARM_NOTIFICATION_ID, notification);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
