package com.geekofia.phonepolice.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.geekofia.phonepolice.databinding.ActivityAntiTouchAlertBinding;

public class AntiTouchAlertActivity extends AppCompatActivity {
    ActivityAntiTouchAlertBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAntiTouchAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
