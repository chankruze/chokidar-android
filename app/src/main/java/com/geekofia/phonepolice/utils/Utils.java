package com.geekofia.phonepolice.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;

import java.util.Calendar;

public class Utils {

    // Function to show a toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static int getToneResource(String tone) {
        switch (tone) {
            case "tone1":
                return R.raw.tone1;
            case "tone2":
                return R.raw.tone2;
            case "tone3":
                return R.raw.tone3;
            case "tone4":
                return R.raw.tone4;
            case "tone5":
                return R.raw.tone5;
            case "tone6":
                return R.raw.tone6;
            default:
                return R.raw.tone1;
        }
    }

    public static String getSelectedTone(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, "tone1");
    }

    public static MediaPlayer setupMediaPlayer(Context context, String tone) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, getToneResource(tone));
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(100.0f, 100.0f);
            return mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to create MediaPlayer with the selected tone");
        }
    }

    public static MediaPlayer setupMediaPlayer(Context context, String tone, boolean setLooping) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, getToneResource(tone));
            mediaPlayer.setLooping(setLooping);
            mediaPlayer.setVolume(100.0f, 100.0f);
            return mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to create MediaPlayer with the selected tone");
        }
    }

    public static void createNotificationChannel(Context context, String channelId, String channelName, int importance, String channelDescription) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    importance
            );
            channel.setDescription(channelDescription);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, int notificationId, Notification notification) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notification);
    }

    public static void dismissNotification(Context context, int notificationId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(notificationId);
    }

    public static double calculateDeltaAcceleration(float[] linearAcceleration) {
        return Math.sqrt(Math.pow(linearAcceleration[0], 2) +
                Math.pow(linearAcceleration[1], 2) +
                Math.pow(linearAcceleration[2], 2));
    }

    public static void rateApp(Context context) {
        // Rate intent
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start activity with rate intent
        context.startActivity(intent);
    }

    public static void shareApp(Context context) {
        // Share Intent
        Intent intent = new Intent("android.intent.action.SEND")
                .setType("text/plain")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("android.intent.extra.TEXT", context.getString(R.string.text_share_app) + "\n\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());
        // Start activity with share intent
        context.startActivity(intent);
    }

    public static void showPrivacyPolicy(Context context) {
        // Create show intent
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://redapps.in/apps_legal/phone_police_privacy.html"))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start activity with show intent
        context.startActivity(intent);
    }

    public static String getGreetingMessage() {
        // Get the current hour
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Determine the greeting based on the hour
        if (hour >= 5 && hour < 12) {
            return "Good Morning!";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon!";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening!";
        } else {
            return "Good Night!";
        }
    }

    // Prevent instantiation
    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }
}
