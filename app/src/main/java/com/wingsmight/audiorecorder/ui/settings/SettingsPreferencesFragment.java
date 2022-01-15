package com.wingsmight.audiorecorder.ui.settings;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wingsmight.audiorecorder.R;

public class SettingsPreferencesFragment extends PreferenceFragmentCompat {



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}