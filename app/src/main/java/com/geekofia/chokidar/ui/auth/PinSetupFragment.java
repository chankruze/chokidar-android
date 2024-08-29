package com.geekofia.chokidar.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geekofia.chokidar.databinding.FragmentPinSetupBinding;
import com.geekofia.chokidar.ui.common.MainActivity;
import com.geekofia.chokidar.utils.SecurePrefs;
import com.geekofia.chokidar.utils.Utils;

import java.util.Objects;

public class PinSetupFragment extends Fragment {
    private FragmentPinSetupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPinSetupBinding.inflate(inflater, container, false);

        binding.savePin.setOnClickListener(v -> {
            String pin = String.valueOf(binding.pinEditText.getText());
            String confirmPin = String.valueOf(binding.confirmPinEditText.getText());

            if (pin.isEmpty() || confirmPin.isEmpty()) {
                Utils.showToast(requireContext(), "Please enter and confirm the PIN");
            } else if (!pin.equals(confirmPin)) {
                Utils.showToast(requireContext(), "PINs do not match");
            } else {
                SecurePrefs securePrefs = new SecurePrefs();
                securePrefs.setPin(requireContext(), pin);
                securePrefs.setFirstLaunch(requireContext(), false);
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).navigateToMain();
                }
            }
        });

        return binding.getRoot();
    }
}
