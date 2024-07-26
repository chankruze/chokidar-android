package com.geekofia.phonepolice.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Utils {

    // Function to show a toast message
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Prevent instantiation
    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }
}
