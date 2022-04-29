package com.wingsmight.audiorecorder.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Consumer;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wingsmight.audiorecorder.CloudDatabase;
import com.wingsmight.audiorecorder.MainActivity;
import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.extensions.StringExt;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;

public class SettingsPreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Preference preference = findPreference("doNotDisturbInterval");

        int fromHour = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_from_hour), 22);
        int fromMinute = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_from_minute), 0);
        int toHour = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_to_hour), 8);
        int toMinute = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_to_minute), 0);

        preference.setSummary(fromHour + ":" + String.format("%02d", fromMinute) + " - " + toHour + ":" + String.format("%02d", toMinute));

        Preference fullNamePref = findPreference("userFullName");
        fullNamePref.setTitle(sharedPreferences.getString("userFullName", ""));

        Preference emailPref = findPreference("email");
        emailPref.setSummary(sharedPreferences.getString("email", "01.01.2001"));

        CloudDatabase.getStorageSize(getActivity(), new Consumer<Long>() {
            @Override
            public void accept(Long storageSize) {
                Preference cloudSizePref = findPreference("cloudSize");

                CloudDatabase.getUsedStorageSize(new Consumer<Long>() {
                    @Override
                    public void accept(Long storedSize) {
                        cloudSizePref.setTitle(StringExt.getSize(storedSize) + " / " + StringExt.getSize(storageSize));
                    }
                });
            }
        });

        Preference themeList = findPreference("themeMode");
        String currentNightModeValue = sharedPreferences.getString("themeMode", getResources().getString(R.string.pf_dark_auto));

        themeList.setSummary(currentNightModeValue);
        themeList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String currentValue = newValue.toString();
                themeList.setSummary(currentValue);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("themeMode", currentValue);
                editor.apply();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setNightMode(currentValue);
                    }
                }, 100);

                return true;
            }
        });
    }


    private void restartApp() {
        Context context = getContext();
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }
    private String convertNightModeToString() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            return getResources().getString(R.string.pf_light_on);
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            return getResources().getString(R.string.pf_light_off);
        } else {
            return getResources().getString(R.string.pf_dark_auto);
        }
    }
    private void setNightMode(String currentValue) {
        if (currentValue.equals(getResources().getString(R.string.pf_light_on))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentValue.equals(getResources().getString(R.string.pf_light_off))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            }
        }
    }
}