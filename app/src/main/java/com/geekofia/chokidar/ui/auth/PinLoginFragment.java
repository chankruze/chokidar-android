package com.geekofia.chokidar.ui.auth;

import static com.geekofia.chokidar.utils.Utils.showToast;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geekofia.chokidar.databinding.FragmentPinLoginBinding;
import com.geekofia.chokidar.ui.home.HomeActivity;
import com.geekofia.chokidar.utils.SecurePrefs;

public class PinLoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPinLoginBinding binding = FragmentPinLoginBinding.inflate(inflater, container, false);
        SecurePrefs securePrefs = new SecurePrefs();

        binding.btnLogin.setOnClickListener(v -> {
            String enteredPin = String.valueOf(binding.pinEditText.getText());
            String savedPin = securePrefs.getPin(getActivity());

            if (enteredPin.equals(savedPin)) {
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).navigateToHome();
                }
            } else {
                showToast(requireContext(), "Invalid PIN");
            }
        });

        return binding.getRoot();
    }
}
