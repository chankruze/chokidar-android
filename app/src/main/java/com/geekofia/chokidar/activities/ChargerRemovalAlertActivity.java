package com.geekofia.chokidar.activities;

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
import com.geekofia.chokidar.databinding.ActivityChargerRemovalAlertBinding;
import com.geekofia.chokidar.services.ChargerRemovalAlertService;
import com.geekofia.chokidar.utils.Constants;
import com.geekofia.chokidar.utils.PreferenceKeyManager;
import com.geekofia.chokidar.utils.Utils;

public class ChargerRemovalAlertActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityChargerRemovalAlertBinding binding = ActivityChargerRemovalAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbarChargerRemovalAlert;
        toolbar.setTitle("Charger Removal Alert");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Load the preference fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_container, new ChargerRemovalAlertSettingsFragment())
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
        ChargerRemovalAlertSettingsFragment fragment = (ChargerRemovalAlertSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.preference_container);

        if (fragment != null) {
            if (PreferenceKeyManager.getPreferenceKeyItem(Constants.CHARGER_REMOVAL_ALERT_SWITCH).getKey().equals(key)) {
                boolean isAlertEnabled = sharedPreferences.getBoolean(key, false);
                String message = PreferenceKeyManager.getPreferenceKeyItem(Constants.CHARGER_REMOVAL_ALERT_SWITCH).getFeatureName() + (isAlertEnabled ? " Enabled" : " Disabled");
                Utils.showToast(this, message);

                if (isAlertEnabled && !isChargerRemovalAlertServiceRunning()) {
                    // Start the battery service with start intent
                    Intent startIntent = new Intent(this, ChargerRemovalAlertService.class);
                    ContextCompat.startForegroundService(this, startIntent);
                } else if (!isAlertEnabled && isChargerRemovalAlertServiceRunning()) {
                    // Stop foreground service with stop intent
                    // https://developer.android.com/develop/background-work/services#Stopping
                    Intent stopIntent = new Intent(this, ChargerRemovalAlertService.class);
                    this.stopService(stopIntent);
                }
            } else if (PreferenceKeyManager.getPreferenceKeyItem(Constants.CHARGER_REMOVAL_ALERT_TONE_PICKER).getKey().equals(key)) {
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

    public boolean isChargerRemovalAlertServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (ChargerRemovalAlertService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class ChargerRemovalAlertSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_activity_charger_removal_alert, rootKey);
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
