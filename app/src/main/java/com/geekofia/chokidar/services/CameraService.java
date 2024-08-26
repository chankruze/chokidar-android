package com.geekofia.chokidar.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.helpers.CameraHelper;

public class CameraService extends LifecycleService {
    private static final String TAG = "CameraService";
    private CameraHelper cameraHelper;
    private static final String CHANNEL_ID = "camera_service_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        cameraHelper = new CameraHelper(this, this);
        cameraHelper.setOnPictureTakenListener(filePath -> {
            Log.d(TAG, "Picture taken: " + filePath);
            stopSelf(); // Stop service after the picture is taken
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Camera Service")
                .setContentText("Camera service is running")
                .setSmallIcon(R.drawable.ic_alert)
                .build();

        startForeground(1, notification);

        new Thread(() -> {
            cameraHelper.captureFrontCamera();
        }).start();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return super.onBind(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Camera Service Channel";
            String description = "Channel for camera service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
