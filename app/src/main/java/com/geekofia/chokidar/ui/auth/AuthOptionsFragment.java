package com.geekofia.chokidar.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geekofia.chokidar.R;
import com.geekofia.chokidar.databinding.FragmentAuthOptionsBinding;

public class AuthOptionsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAuthOptionsBinding binding = FragmentAuthOptionsBinding.inflate(inflater, container, false);

        // Disable the "Next" button until a selection is made
        binding.next.setEnabled(false);

        // Listen for changes in the RadioGroup
        binding.authOptions.setOnCheckedChangeListener((group, checkedId) -> {
            // Enable the "Next" button once a selection is made
            binding.next.setEnabled(true);
        });

        binding.next.setOnClickListener(v -> {
            int selectedId = binding.authOptions.getCheckedRadioButtonId();

            if (selectedId == R.id.pin) {
                // Load the PIN setup fragment
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).loadFragment(new PinSetupFragment());
                }
            } else if (selectedId == R.id.biometric) {
                // Load the biometric setup fragment
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).loadFragment(new BiometricSetupFragment());
                }
            }
        });


        return binding.getRoot();
    }
}
