package com.geekofia.chokidar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePrefs {
    public static final String TAG = "SecurePrefs";
    private static final String KEY_PIN = "UserPIN";
    private static final String KEY_FIRST_LAUNCH = "FirstLaunch";
    private static final String KEY_AUTH_METHOD = "auth_method";


    private SharedPreferences getEncryptedPrefs(Context context) throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                "encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public void setPin(Context context, String pin) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().putString(KEY_PIN, pin).apply();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public String getPin(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            return prefs.getString(KEY_PIN, null);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
            return null;
        }
    }

    public void setFirstLaunch(Context context, boolean isFirst) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public boolean isFirstLaunch(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
            return true;
        }
    }

    public void setAuthMethod(Context context, String authMethod) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().putString(KEY_AUTH_METHOD, authMethod).apply();
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    public String getAuthMethod(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            return prefs.getString(KEY_AUTH_METHOD, "PIN"); // Default to PIN if not set
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
            return "PIN";
        }
    }
}
