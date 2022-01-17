package com.wingsmight.audiorecorder.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wingsmight.audiorecorder.MainActivity;
import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private FragmentSettingsBinding binding;
    private SettingsPreferencesFragment preferencesFragment;

    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        preferencesFragment = new SettingsPreferencesFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, preferencesFragment)
                .commit();

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActivity().getSupportFragmentManager().beginTransaction()
                .remove(preferencesFragment)
                .commit();

        binding = null;
    }
}