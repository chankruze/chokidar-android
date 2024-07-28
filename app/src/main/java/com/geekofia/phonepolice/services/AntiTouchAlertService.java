package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_NOTIFICATION_ID;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_TAG;
import static com.geekofia.phonepolice.utils.Constants.FULL_BATTERY_ALERT_NOTIFICATION_ID;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.AntiTouchAlertActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;
import com.geekofia.phonepolice.utils.Utils;

public class AntiTouchAlertService extends Service implements SensorEventListener {
    private float acceleration;
    private float accelerationCurrent;
    private float accelerationLast;
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
                ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Anti Touch Alert",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for anti touch alert");

        // Check if the full battery alert switch is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAntiTouchAlertEnabled = prefs.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.ANTI_TOUCH_ALERT_SWITCH).getKey(), false);

        if (isAntiTouchAlertEnabled) {
            // TODO: register sensor listener
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor defaultSensor = sensorManager.getDefaultSensor(1);
            acceleration = 0.0f;
            accelerationCurrent = 9.80665f;
            accelerationLast = 9.80665f;
            sensorManager.registerListener(this, defaultSensor, 0);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(ANTI_TOUCH_ALERT_SERVICE_TAG, "onStartCommand executed");

        // Create a pending intent to open when clicked on the notification
        Intent notificationIntent = new Intent(this, AntiTouchAlertActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification required for the service to start
        Notification notification = new NotificationCompat.Builder(this, ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Anti Touch Alert")
                .setContentText("Anti touch alert feature is now active")
                .setSmallIcon(R.drawable.ic_anti_touch)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        // Start foreground service
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(ANTI_TOUCH_ALERT_SERVICE_ID, notification);
        } else {
            startForeground(ANTI_TOUCH_ALERT_SERVICE_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }

        return Service.START_STICKY;
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
        // TODO: disable flash
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // Dismiss the service with notification
        stopForeground(true);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            float[] fArr = (float[]) sensorEvent.values.clone();
            float f1 = fArr[0];
            float f2 = fArr[1];
            float f3 = fArr[2];

            accelerationLast = accelerationCurrent;

            float sqrt = (float) Math.sqrt(Math.pow(f1, 2) + Math.pow(f2, 2) + Math.pow(f3, 2));
            accelerationCurrent = sqrt;

            float f4 = (acceleration * 0.5f) + (sqrt - accelerationLast);
            acceleration = f4;

            if (f4 > 1.0f) {
                // TODO: play alert
                // TODO: set flash
                // TODO: set vibrate
                // Get selected tone
                String selectedTone = getSelectedTone(this, PreferenceKeyManager.getPreferenceKeyItem(Constants.ANTI_TOUCH_ALERT_TONE_PICKER).getKey());

                // Check if currently selected tone is changed
                if (!selectedTone.equals(currentAlertTone) || mediaPlayer == null) {
                    // Release current media player if tone has changed
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    currentAlertTone = selectedTone;
                    mediaPlayer = setupMediaPlayer(this, currentAlertTone, false);
                }
                // If media player is already playing, start playing
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    // Create a new notification and swap it with existing one
                    Notification notification = new NotificationCompat.Builder(this, ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                            .setContentTitle("Anti Touch Alert")
                            .setContentText("Some one is touching your device!")
                            .setSmallIcon(R.drawable.ic_anti_touch)
                            .setOngoing(true)
                            .build();
                    // Show the notification
                    showNotification(this, ANTI_TOUCH_ALERT_NOTIFICATION_ID, notification);
                }
            }
            // TODO: properly exit by removing notification once not moving
            //            else {
            //                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //                    // Pause the media player
            //                    mediaPlayer.pause();
            //                    // Dismiss the notification
            //                    dismissNotification(this, ANTI_TOUCH_ALERT_NOTIFICATION_ID);
            //                }
            //            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
