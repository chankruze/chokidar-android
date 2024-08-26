package com.geekofia.chokidar.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.geekofia.chokidar.SecurePrefs;
import com.geekofia.chokidar.databinding.ActivityPinLoginBinding;
import com.geekofia.chokidar.utils.Utils;

public class PinLoginActivity extends AppCompatActivity {
    ActivityPinLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> {
            String pin = String.valueOf(binding.pinEditText.getText());

            SecurePrefs securePrefs = new SecurePrefs();
            String storedPin = securePrefs.getPin(this);

            if (pin.equals(storedPin)) {
                navigateToHome();
            } else {
                Utils.showToast(this, "Incorrect PIN");
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
