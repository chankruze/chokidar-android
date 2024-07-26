package com.geekofia.phonepolice.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.geekofia.phonepolice.MainActivity;
import com.geekofia.phonepolice.SecurePrefs;
import com.geekofia.phonepolice.databinding.ActivityPinSetupBinding;
import com.geekofia.phonepolice.utils.Utils;

public class PinSetupActivity extends AppCompatActivity {
    ActivityPinSetupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinSetupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.savePin.setOnClickListener(v -> {
            String pin = String.valueOf(binding.pinEditText.getText());
            String confirmPin = String.valueOf(binding.confirmPinEditText.getText());

            if (pin.isEmpty() || confirmPin.isEmpty()) {
                Utils.showToast(this, "Please enter and confirm the PIN");
            } else if (!pin.equals(confirmPin)) {
                Utils.showToast(this, "PINs do not match");
            } else {
                SecurePrefs securePrefs = new SecurePrefs();
                securePrefs.setPin(this, pin);
                securePrefs.setFirstLaunch(this, false);
                navigateToMain();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
