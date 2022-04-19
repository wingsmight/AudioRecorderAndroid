package com.wingsmight.audiorecorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognize;
import com.wingsmight.audiorecorder.databinding.ActivityMainBinding;
import com.wingsmight.audiorecorder.ui.login.SignUpActivity;
import com.wingsmight.audiorecorder.ui.main.MainFragment;
import com.wingsmight.audiorecorder.ui.records.RecordsFragment;
import com.wingsmight.audiorecorder.ui.settings.AppInfoActivity;
import com.wingsmight.audiorecorder.ui.settings.DoNotDisturbIntervalActivity;
import com.wingsmight.audiorecorder.ui.settings.SettingsFragment;

import org.vosk.LibVosk;
import org.vosk.LogLevel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


    BottomNavigationView bottomNavigationView;
    final MainFragment mainFragment = new MainFragment();
    final Fragment settingsFragment = new SettingsFragment();
    final Fragment recordsFragment = new RecordsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav);
        fragmentManager.beginTransaction().add(R.id.frame, recordsFragment, "recordsFragment").hide(recordsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame, settingsFragment, "settingsFragment").hide(settingsFragment).commit();
        fragmentManager.beginTransaction().add(R.id.frame, mainFragment, "mainFragment").commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(false);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // vosk-api
        LibVosk.setLogLevel(LogLevel.DEBUG);

        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            SpeechRecognize.Init(getApplicationContext(), new Runnable() {
                @Override
                public void run() {
                    mainFragment.startService();
                }
            });
        }

        setNightMode();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                SpeechRecognize.Init(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        mainFragment.startService();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Приложение не может работать без доступа к микрофону", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_main:
                fragmentManager.beginTransaction().hide(active).show(mainFragment).commit();
                active = mainFragment;
                return true;
            case R.id.navigation_settings:
                fragmentManager.beginTransaction().hide(active).show(settingsFragment).commit();
                active = settingsFragment;
                return true;
            case R.id.navigation_records:
                fragmentManager.beginTransaction().hide(active).show(recordsFragment).commit();
                active = recordsFragment;
                return true;
        }

        return false;
    }

    @SuppressLint("RestrictedApi")
    private void hideToolBar() {
        getSupportActionBar().setShowHideAnimationEnabled(false);
        getSupportActionBar().hide();
    }

    public void logOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
    public void setDoNotDisturbInterval(View view) {
        Intent myIntent = new Intent(MainActivity.this, DoNotDisturbIntervalActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
    public void showAppInfoPanel(View view) {
        Intent myIntent = new Intent(MainActivity.this, AppInfoActivity.class);
        MainActivity.this.startActivity(myIntent);
    }


    private void setNightMode() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String currentValue = sharedPreferences.getString("themeMode", getResources().getString(R.string.pf_dark_auto));

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