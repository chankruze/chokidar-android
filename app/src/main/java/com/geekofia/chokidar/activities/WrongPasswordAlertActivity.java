package com.geekofia.chokidar.activities;

import static com.geekofia.chokidar.utils.Utils.setupMediaPlayer;
import static com.geekofia.chokidar.utils.Utils.showToast;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.databinding.ActivityWrongPasswordAlertBinding;
import com.geekofia.chokidar.receivers.WrongPasswordReceiver;
import com.geekofia.chokidar.utils.Constants;
import com.geekofia.chokidar.utils.PreferenceKeyManager;
import com.geekofia.chokidar.utils.Utils;

public class WrongPasswordAlertActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private ActivityResultLauncher<Intent> enableAdminLauncher;

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

        // Initialize the launcher
        enableAdminLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                    } else {

                    }
                }
        );
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

                // Device policy manager
                DevicePolicyManager devicePolicyManager;
                devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                // Admin component
                ComponentName adminComponent;
                adminComponent = new ComponentName(this, WrongPasswordReceiver.class);

                if (isAlertEnabled) {
                    // Ask for device admin permission
                    if (!devicePolicyManager.isAdminActive(adminComponent)) {
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin to use this feature.");
                        // Use the launcher to start the activity
                        enableAdminLauncher.launch(intent);
                    }
                } else {
                    // Remove device admin permission
                    if (devicePolicyManager.isAdminActive(adminComponent)) {
                        devicePolicyManager.removeActiveAdmin(adminComponent);
                        showToast(this, "Device admin access removed");
                    }
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
