package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Constants.ANTI_TOUCH_ALERT_SERVICE_TAG;
import static com.geekofia.phonepolice.utils.Utils.createNotificationChannel;

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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.AntiTouchAlertActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class AntiTouchAlertService extends Service implements SensorEventListener {
    private float acceleration;
    private float accelerationCurrent;
    private float accelerationLast;
    private SensorManager sensorManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor defaultSensor = sensorManager.getDefaultSensor(1);
        acceleration = 0.0f;
        accelerationCurrent = 9.80665f;
        accelerationLast = 9.80665f;
        sensorManager.registerListener(this, defaultSensor, 0);

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
        // TODO: pause media player
        // TODO: disable flash
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // Dismiss the service with notification
        stopForeground(true);
    }
}
