package com.geekofia.phonepolice.services;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_ID;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.phonepolice.utils.Constants.POCKET_ALARM_SERVICE_TAG;
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
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.activities.AntiTouchAlertActivity;
import com.geekofia.phonepolice.activities.PocketAlarmActivity;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class PocketAlarmService extends Service implements SensorEventListener {
    private MediaPlayer mediaPlayer;
    private Sensor accelerometer;
    private SensorManager sensorManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.values[0] < this.accelerometer.getMaximumRange()) {
            // pocket status: true
            // TODO: stop media player
            // TODO: implement flash light disable
        }
        // TODO: if pocket alarm enabled
        // else if (Constants.Pocket_Status.booleanValue()) {
        // TODO: implement flash light enable
        // TODO: implement vibration
        // TODO: start media player
        // }
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
                POCKET_ALARM_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Pocket Alarm",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for pocket alarm");

        // Check if the full battery alert switch is enabled
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPocketAlarmEnabled = prefs.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.POCKET_ALARM_SWITCH).getKey(), false);

        if (isPocketAlarmEnabled) {
            // TODO: register sensor listener
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(8);
        sensorManager.registerListener(this, accelerometer, 3);

        Log.d(POCKET_ALARM_SERVICE_TAG, "onStartCommand executed");

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

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: pause media player
        // TODO: disable flash
        // TODO: set pocket status false
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}
