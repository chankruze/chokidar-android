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
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.geekofia.phonepolice.MainActivity;
import com.geekofia.phonepolice.adapters.SafetyFeatureCardAdapter;
import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.databinding.ActivityHomeBinding;
import com.geekofia.phonepolice.models.SafetyFeatureCardItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private static final String TAG = "HomeActivity";
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> requestManageExternalStorageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Update toolbar
        Toolbar toolbar = binding.toolbar;
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

        // Initialize the permission launcher
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            boolean allPermissionsGranted = true;
            for (boolean isGranted : permissions.values()) {
                if (!isGranted) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Log.i(TAG, "All permissions granted");
            } else {
                Log.i(TAG, "Some permissions denied");
                showToast(this, "Some permissions denied");
            }
        });

        requestManageExternalStorageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (isManageExternalStoragePermissionGranted()) {
                Log.i(TAG, "Manage External Storage permission granted");
            } else {
                Log.i(TAG, "Manage External Storage permission denied");
                showToast(this, "Manage External Storage permission denied");
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !isManageExternalStoragePermissionGranted()) {
            requestManageExternalStoragePermission();
        }

    }

    private boolean isManageExternalStoragePermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager();
    }

    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This app requires access to manage all files. Please grant this permission in Settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                            requestManageExternalStorageLauncher.launch(intent);
                        })
                        .create()
                        .show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (itemId == R.id.nav_anti_touch_alert) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, AntiTouchAlertActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_wrong_password_alert) {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(this, WrongPasswordAlertActivity.class);
            startActivity(intent);
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
        } else if (itemId == R.id.action_open_gallery) {
            Intent intent = new Intent(this, IntruderGalleryActivity.class);
            startActivity(intent);
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
