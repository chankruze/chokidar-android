package com.geekofia.phonepolice.activities;

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
import com.geekofia.phonepolice.PreferenceKeyManager;
import com.geekofia.phonepolice.Utils;
import com.geekofia.phonepolice.databinding.ActivityFullChargeBinding;
import com.geekofia.phonepolice.services.BatteryService;

public class FullChargeActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ActivityFullChargeBinding binding;
    private static final String SWITCH_KEY = "full_battery_alert";
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullChargeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbarFullCharge;
        toolbar.setTitle("Battery Full Alert");
        setSupportActionBar(toolbar);

        // Enable the Up button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Load the preference fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.preference_container, new ActivityFullChargeSettingsFragment())
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
        ActivityFullChargeSettingsFragment fragment = (ActivityFullChargeSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.preference_container);
        if (fragment != null) {
            if (PreferenceKeyManager.getPreferenceKeyItem("FULL_BATTERY_ALERT_SWITCH").getKey().equals(key)) {
                boolean isEnabled = sharedPreferences.getBoolean(key, false);
                String message = PreferenceKeyManager.getPreferenceKeyItem("FULL_BATTERY_ALERT_SWITCH").getFeatureName() + (isEnabled ? " Enabled" : " Disabled");
                Utils.showToast(this, message);

                if (!isBatteryServiceRunning()) {
                    // Start the battery service
                    Intent intent = new Intent(this, BatteryService.class);
                    ContextCompat.startForegroundService(this, intent);
                }
            } else if (PreferenceKeyManager.getPreferenceKeyItem("FULL_BATTERY_ALERT_TONE_PICKER").getKey().equals(key)) {
                // Play the newly selected tone
                playSelectedTone(sharedPreferences.getString(key, "tone1"));
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
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private int getToneResource(String tone) {
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

    private void playSelectedTone(String tone) {
        int toneResource = getToneResource(tone);

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Create a new MediaPlayer for the selected tone
        mediaPlayer = MediaPlayer.create(this, toneResource);
        mediaPlayer.start();
    }

    public boolean isBatteryServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (BatteryService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class ActivityFullChargeSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences_activity_full_charge, rootKey);
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