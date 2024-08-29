package com.geekofia.chokidar.ui.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.geekofia.chokidar.ui.auth.AuthActivity;
import com.geekofia.chokidar.databinding.ActivityMainBinding;
import com.geekofia.chokidar.utils.SecurePrefs;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SecurePrefs securePrefs = new SecurePrefs();

        if (securePrefs.isFirstLaunch(this)) {
            // If first launch, show AuthOptionsFragment in AuthActivity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            // If not first launch, show LoginFragment in AuthActivity
            // Check which auth option is selected
            String authMethod = securePrefs.getAuthMethod(this);

            if ("PIN".equals(authMethod)) {
                Intent intent = new Intent(this, AuthActivity.class);
                intent.putExtra("FRAGMENT_NAME", "PIN_LOGIN");
                startActivity(intent);
                finish();
            } else if ("BIOMETRIC".equals(authMethod)) {
                Intent intent = new Intent(this, AuthActivity.class);
                intent.putExtra("FRAGMENT_NAME", "BIOMETRIC_LOGIN");
                startActivity(intent);
                finish();
            }


        }
    }
}
