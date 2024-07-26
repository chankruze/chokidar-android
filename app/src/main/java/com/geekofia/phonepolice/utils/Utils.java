package com.geekofia.phonepolice.utils;

import android.content.Context;
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

    // Prevent instantiation
    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }
}
