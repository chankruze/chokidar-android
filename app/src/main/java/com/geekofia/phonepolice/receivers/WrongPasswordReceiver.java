package com.geekofia.phonepolice.receivers;

import static com.geekofia.phonepolice.utils.Constants.WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS;
import static com.geekofia.phonepolice.utils.Constants.WRONG_PASSWORD_ALERT_MAX_ATTEMPTS;
import static com.geekofia.phonepolice.utils.Constants.WRONG_PASSWORD_ALERT_RECEIVER_TAG;
import static com.geekofia.phonepolice.utils.Utils.showToast;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.services.CameraService;
import com.geekofia.phonepolice.services.WrongPasswordAlertService;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;

public class WrongPasswordReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
        showToast(context, "Device admin enabled");
        Log.d(WRONG_PASSWORD_ALERT_RECEIVER_TAG, "Device admin enabled");
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
        showToast(context, "Device admin disabled");
        Log.d(WRONG_PASSWORD_ALERT_RECEIVER_TAG, "Device admin disabled");
    }

    @Override
    public void onPasswordFailed(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        // Retrieve the current count of wrong password attempts
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int failedAttempts = preferences.getInt(PreferenceKeyManager.getPreferenceKeyItem(WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS).getKey(), 0);
        // Get the state of the switch
        boolean isWrongPasswordAlertEnabled = preferences.getBoolean(PreferenceKeyManager.getPreferenceKeyItem(Constants.WRONG_PASSWORD_ALERT_SWITCH).getKey(), false);
        // Get the selected tone
        int maxWrongPasswordAttempts = Integer.parseInt(preferences.getString(PreferenceKeyManager.getPreferenceKeyItem(WRONG_PASSWORD_ALERT_MAX_ATTEMPTS).getKey(), "1"));

        // Increment the count
        failedAttempts++;

        // Save the updated count back to shared preferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKeyManager.getPreferenceKeyItem(WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS).getKey(), failedAttempts);
        editor.apply();
        
        String message = "Wrong attempts: " + failedAttempts + " | Max attempts: " + maxWrongPasswordAttempts;
        Log.d(WRONG_PASSWORD_ALERT_RECEIVER_TAG, message);

        if (isWrongPasswordAlertEnabled && (failedAttempts >= maxWrongPasswordAttempts)) {
            // Device admin enabled
            // Start the battery service with start intent
            Intent startIntent = new Intent(context, WrongPasswordAlertService.class);
            ContextCompat.startForegroundService(context, startIntent);

            Intent i = new Intent(context, CameraService.class);
            // TODO: read this from preference
            i.putExtra("useFrontCamera", true);
            context.startService(i);
        }
    }

    @Override
    public void onPasswordSucceeded(@NonNull Context context, @NonNull Intent intent, @NonNull UserHandle user) {
        // Reset the attempt count on successful password entry
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PreferenceKeyManager.getPreferenceKeyItem(WRONG_PASSWORD_ALERT_FAILED_ATTEMPTS).getKey(), 0);
        editor.apply();

        Log.d(WRONG_PASSWORD_ALERT_RECEIVER_TAG, "Password succeeded. Attempt count reset.");

        // Device admin not enabled
        // Stop foreground service with stop intent
        // https://developer.android.com/develop/background-work/services#Stopping
        Intent stopIntent = new Intent(context, WrongPasswordAlertService.class);
        context.stopService(stopIntent);
    }
}
