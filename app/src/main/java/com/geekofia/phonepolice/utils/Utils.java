package com.geekofia.phonepolice.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.geekofia.phonepolice.R;

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

    // Prevent instantiation
    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }
}
