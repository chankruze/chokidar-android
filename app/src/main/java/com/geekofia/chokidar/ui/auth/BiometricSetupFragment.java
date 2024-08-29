package com.geekofia.chokidar.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geekofia.chokidar.databinding.FragmentBiometricSetupBinding;
import com.geekofia.chokidar.utils.SecurePrefs;

public class BiometricSetupFragment extends Fragment {
    private FragmentBiometricSetupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBiometricSetupBinding.inflate(inflater, container, false);

        binding.setupBiometric.setOnClickListener(v -> {
            SecurePrefs securePrefs = new SecurePrefs();
            securePrefs.setAuthMethod(getActivity(), "BIOMETRIC");
            securePrefs.setFirstLaunch(getActivity(), false);
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).navigateToMain();
            }
        });

        return binding.getRoot();
    }
}
