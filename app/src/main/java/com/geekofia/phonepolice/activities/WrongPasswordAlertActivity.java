package com.geekofia.phonepolice.activities;

import static com.geekofia.phonepolice.utils.Utils.setupMediaPlayer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.databinding.ActivityWrongPasswordAlertBinding;
import com.geekofia.phonepolice.services.WrongPasswordAlertService;
import com.geekofia.phonepolice.services.WrongPasswordAlertService;
import com.geekofia.phonepolice.utils.Constants;
import com.geekofia.phonepolice.utils.PreferenceKeyManager;
import com.geekofia.phonepolice.utils.Utils;

public class WrongPasswordAlertActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWrongPasswordAlertBinding binding = ActivityWrongPasswordAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup toolbar
        Toolbar toolbar = binding.toolbarWrongPasswordAlert;
        toolbar.setTitle("Wrong Password Alert");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Load the preference fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_container, new WrongPasswordAlertSettingsFragment())
                .commit();

        // Get the default shared preferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register the preference change listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar back button click
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to previous
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        WrongPasswordAlertSettingsFragment fragment = (WrongPasswordAlertSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.preference_container);

        if (fragment != null) {
            if (PreferenceKeyManager.getPreferenceKeyItem(Constants.WRONG_PASSWORD_ALERT_SWITCH).getKey().equals(key)) {
                boolean isAlertEnabled = sharedPreferences.getBoolean(key, false);
                String message = PreferenceKeyManager.getPreferenceKeyItem(Constants.WRONG_PASSWORD_ALERT_SWITCH).getFeatureName() + (isAlertEnabled ? " Enabled" : " Disabled");
                Utils.showToast(this, message);

                if (isAlertEnabled && !isWrongPasswordAlertServiceRunning()) {
                    // Start the battery service with start intent
                    Intent startIntent = new Intent(this, WrongPasswordAlertService.class);
                    ContextCompat.startForegroundService(this, startIntent);
                } else if (!isAlertEnabled && isWrongPasswordAlertServiceRunning()) {
                    // Stop foreground service with stop intent
                    // https://developer.android.com/develop/background-work/services#Stopping
                    Intent stopIntent = new Intent(this, WrongPasswordAlertService.class);
                    this.stopService(stopIntent);
                }
            } else if (PreferenceKeyManager.getPreferenceKeyItem(Constants.WRONG_PASSWORD_ALERT_TONE_PICKER).getKey().equals(key)) {
                // Play the newly selected tone
                if (mediaPlayer == null) {
                    mediaPlayer = setupMediaPlayer(this, sharedPreferences.getString(key, "tone1"));
                } else {
                    mediaPlayer.release();
                    mediaPlayer = setupMediaPlayer(this, sharedPreferences.getString(key, "tone1"));
                    mediaPlayer.start();
                }
                // Update the summary
                fragment.updatePreferenceSummary(key);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the preference change listener to avoid memory leaks
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        // Release the MediaPlayer when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public boolean isWrongPasswordAlertServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (WrongPasswordAlertService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class WrongPasswordAlertSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_activity_wrong_password_alert, rootKey);
        }

        public void updatePreferenceSummary(String key) {
            ListPreference listPreference = findPreference(key);
            if (listPreference != null) {
                int index = listPreference.findIndexOfValue(listPreference.getValue());
                if (index >= 0) {
                    listPreference.setSummary(listPreference.getEntries()[index]);
                }
            }
        }
    }
}
