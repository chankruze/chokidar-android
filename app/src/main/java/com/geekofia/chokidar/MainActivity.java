package com.geekofia.chokidar;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.geekofia.chokidar.activities.PinLoginActivity;
import com.geekofia.chokidar.activities.PinSetupActivity;
import com.geekofia.chokidar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SecurePrefs securePrefs = new SecurePrefs();

        if (securePrefs.isFirstLaunch(this)) {
            // Redirect to PIN setup screen
            Intent intent = new Intent(this, PinSetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Redirect to PIN login screen
            Intent intent = new Intent(this, PinLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
