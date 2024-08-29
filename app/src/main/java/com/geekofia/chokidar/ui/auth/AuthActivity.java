package com.geekofia.chokidar.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.databinding.ActivityAuthBinding;
import com.geekofia.chokidar.ui.common.MainActivity;
import com.geekofia.chokidar.ui.home.HomeActivity;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if we need to show the LoginFragment directly
        String fragmentName = getIntent().getStringExtra("FRAGMENT_NAME");

        if ("PIN_LOGIN".equals(fragmentName)) {
            // Load LoginFragment directly if not the first launch
            loadFragment(new PinLoginFragment());
        } else if ("BIOMETRIC_LOGIN".equals(fragmentName)) {
            loadFragment(new BiometricLoginFragment());
        } else {
            // Show the initial AuthOptionsFragment
            loadFragment(new AuthOptionsFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
