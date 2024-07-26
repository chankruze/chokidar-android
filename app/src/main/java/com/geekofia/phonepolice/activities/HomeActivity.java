package com.geekofia.phonepolice.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import com.geekofia.phonepolice.adapters.SafetyFeatureCardAdapter;
import com.geekofia.phonepolice.R;
import com.geekofia.phonepolice.databinding.ActivityHomeBinding;
import com.geekofia.phonepolice.models.SafetyFeatureCardItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityHomeBinding binding;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbarHome;
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        List<SafetyFeatureCardItem> safetyFeatureCardItemList = new ArrayList<>();
        // Add card items to the list
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Intruder Alert", R.drawable.ic_intruder, null));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Anti Touch Detection", R.drawable.ic_anti_touch, null));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Wrong Password Alert", R.drawable.ic_wrong_password, null));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Charging Removal Alert", R.drawable.ic_charging_removal, null));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Full Battery Alert", R.drawable.ic_full_battery, FullChargeActivity.class));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("Pocket Alarm", R.drawable.ic_pocket_alarm, null));
        safetyFeatureCardItemList.add(new SafetyFeatureCardItem("USB Detection", R.drawable.ic_baseline_usb_24, null));

        SafetyFeatureCardAdapter safetyFeatureCardAdapter = new SafetyFeatureCardAdapter(this, safetyFeatureCardItemList);
        binding.recyclerView.setAdapter(safetyFeatureCardAdapter);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {// Handle home action
        } else if (itemId == R.id.nav_account) {// Handle profile action
        } else if (itemId == R.id.nav_logout) {// Handle settings action
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}