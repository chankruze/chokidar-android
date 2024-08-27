package com.geekofia.chokidar.ui.fullbatteryalert;

import static com.geekofia.chokidar.utils.Utils.setupMediaPlayer;

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

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.databinding.ActivityFullBatteryAlertBinding;
import com.geekofia.chokidar.utils.Constants;
import com.geekofia.chokidar.utils.PreferenceKeyManager;
import com.geekofia.chokidar.utils.Utils;

public class FullBatteryAlertActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFullBatteryAlertBinding binding = ActivityFullBatteryAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbarFullBatteryAlert;
        toolbar.setTitle("Full Battery Alert");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Load the preference fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_container, new FullBatteryAlertSettingsFragment())
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
        FullBatteryAlertSettingsFragment fragment = (FullBatteryAlertSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.preference_container);

        if (fragment != null) {
            if (PreferenceKeyManager.getPreferenceKeyItem(Constants.FULL_BATTERY_ALERT_SWITCH).getKey().equals(key)) {
                boolean isAlertEnabled = sharedPreferences.getBoolean(key, false);
                String message = PreferenceKeyManager.getPreferenceKeyItem(Constants.FULL_BATTERY_ALERT_SWITCH).getFeatureName() + (isAlertEnabled ? " Enabled" : " Disabled");
                Utils.showToast(this, message);

                if (isAlertEnabled && !isFullBatteryAlertServiceRunning()) {
                    // Start the battery service with start intent
                    Intent startIntent = new Intent(this, FullBatteryAlertService.class);
                    ContextCompat.startForegroundService(this, startIntent);
                } else if (!isAlertEnabled && isFullBatteryAlertServiceRunning()) {
                    // Stop foreground service with stop intent
                    // https://developer.android.com/develop/background-work/services#Stopping
                    Intent stopIntent = new Intent(this, FullBatteryAlertService.class);
                    this.stopService(stopIntent);
                }
            } else if (PreferenceKeyManager.getPreferenceKeyItem(Constants.FULL_BATTERY_ALERT_TONE_PICKER).getKey().equals(key)) {
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

    public boolean isFullBatteryAlertServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (FullBatteryAlertService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class FullBatteryAlertSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_activity_full_battery_alert, rootKey);
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
