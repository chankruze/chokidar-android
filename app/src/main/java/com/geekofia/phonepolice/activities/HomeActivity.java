package com.geekofia.phonepolice.activities;

import static com.geekofia.phonepolice.utils.Utils.getGreetingMessage;
import static com.geekofia.phonepolice.utils.Utils.rateApp;
import static com.geekofia.phonepolice.utils.Utils.shareApp;
import static com.geekofia.phonepolice.utils.Utils.showPrivacyPolicy;
import static com.geekofia.phonepolice.utils.Utils.showToast;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.geekofia.phonepolice.MainActivity;
import com.geekofia.phonepolice.adapters.SafetyFeatureCardAdapter;
import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.databinding.ActivityHomeBinding;
import com.geekofia.phonepolice.models.SafetyFeatureCardItem;
import com.geekofia.phonepolice.utils.Utils;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Update toolbar
        Toolbar toolbar = binding.toolbarHome;
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        // Setup actionbar drawer toggle
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update greeting message
        binding.greetingTextView.setText(String.format("Hey, %s", getGreetingMessage()));

        // Render safety feature cards
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        List<SafetyFeatureCardItem> safetyFeatureCardItemList = new ArrayList<>();
        // Add card items to the list
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_anti_touch_detection), getString(R.string.desc_anti_touch_alert), R.drawable.ic_anti_touch, AntiTouchAlertActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_wrong_password_alert), R.drawable.ic_wrong_password, WrongPasswordAlertActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_charger_removal_alert), getString(R.string.desc_charger_removal_alert), R.drawable.ic_charger_removal, ChargerRemovalAlertActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_full_battery_alert), getString(R.string.desc_full_battery_alert), R.drawable.ic_full_battery, FullBatteryAlertActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_pocket_alarm), getString(R.string.desc_pocket_alarm), R.drawable.ic_pocket_alarm, PocketAlarmActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem(getString(R.string.title_usb_detection), R.drawable.outline_usb_24, null));

        SafetyFeatureCardAdapter safetyFeatureCardAdapter = new SafetyFeatureCardAdapter(this, safetyFeatureCardItemList);
        binding.recyclerView.setAdapter(safetyFeatureCardAdapter);

        // Request notification permission
        requestNotificationPermission();

        // Handle back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    showExitDialog();
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_intruder_alert) {
            // TODO
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_anti_touch_alert) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, AntiTouchAlertActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_wrong_password_alert) {
            // TODO
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_charger_removal_alert) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, ChargerRemovalAlertActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_full_battery_alert) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, FullBatteryAlertActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_pocket_alarm) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, PocketAlarmActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_usb_detection) {
            // TODO
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_settings) {
            // TODO
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_share_app) {
            shareApp(this);
        } else if (itemId == R.id.nav_rate_app) {
            rateApp(this);
        } else if (itemId == R.id.nav_privacy_policy) {
            showPrivacyPolicy(this);
        } else if (itemId == R.id.nav_account) {
            showToast(this, "Not implemented yet");
        } else if (itemId == R.id.nav_logout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            // TODO: open settings
            return true;
        } else if (itemId == R.id.action_cloud_sync) {
            // TODO: sync to cloud
            return true;
        } else if (itemId == R.id.action_logout) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now show notifications
                Utils.showToast(this, "Notification permission granted.");
            } else {
                // Permission denied, handle appropriately (e.g., show a message)
                showPermissionDeniedDialog();
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request the POST_NOTIFICATIONS permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showPermissionDeniedDialog() {
        // Alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification Permission Required");
        builder.setMessage("This app requires notification permission to alert you about important updates. Please enable it in the app settings.");
        builder.setPositiveButton("OK", (dialog, which) -> openAppSettings());
        // Create alert dialog
        AlertDialog dialog = builder.create();
        // Show the dialog
        dialog.show();
    }

    private void openAppSettings() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", getPackageName(), null));
        }
        startActivity(intent);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Finish the activity
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Dismiss the dialog
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}
