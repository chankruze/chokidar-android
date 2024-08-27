package com.geekofia.chokidar.ui.wrongpasswordalert;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;
import static com.geekofia.chokidar.utils.Constants.WRONG_PASSWORD_ALERT_NOTIFICATION_ID;
import static com.geekofia.chokidar.utils.Constants.WRONG_PASSWORD_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID;
import static com.geekofia.chokidar.utils.Utils.createNotificationChannel;
import static com.geekofia.chokidar.utils.Utils.getSelectedTone;
import static com.geekofia.chokidar.utils.Utils.setupMediaPlayer;
import static com.geekofia.chokidar.utils.Utils.showNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.utils.Constants;
import com.geekofia.chokidar.utils.PreferenceKeyManager;

public class WrongPasswordAlertService extends Service {
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
                WRONG_PASSWORD_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID,
                "Wrong Password Alert",
                NotificationManager.IMPORTANCE_DEFAULT,
                "Notifications for wrong password alert");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get the selected tone
        String selectedTone = getSelectedTone(this, PreferenceKeyManager.getPreferenceKeyItem(Constants.WRONG_PASSWORD_ALERT_TONE_PICKER).getKey());

        // Build the notification
        // Create a pending intent to open when clicked on the notification
        Intent notificationIntent = new Intent(this, WrongPasswordAlertService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create the notification required for the service to start
        Notification notification = new NotificationCompat.Builder(this, WRONG_PASSWORD_ALERT_SERVICE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.title_wrong_password_alert))
                .setContentText(getString(R.string.desc_wrong_password_alert))
                .setSmallIcon(R.drawable.ic_wrong_password)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        // Check if currently selected tone is changed
        if (!selectedTone.equals(currentAlertTone) || mediaPlayer == null) {
            // Release current media player if tone has changed
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            currentAlertTone = selectedTone;
            mediaPlayer = setupMediaPlayer(this, currentAlertTone, true);
        }
        // If media player is already playing, start playing
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            // Show the notification
            showNotification(this, WRONG_PASSWORD_ALERT_NOTIFICATION_ID, notification);
        }

        // Start foreground service
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(WRONG_PASSWORD_ALERT_NOTIFICATION_ID, notification);
        } else {
            startForeground(WRONG_PASSWORD_ALERT_NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
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
        // Dismiss the service with notification
        stopForeground(true);
    }
}
