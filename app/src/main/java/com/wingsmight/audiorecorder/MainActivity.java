package com.wingsmight.audiorecorder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wingsmight.audiorecorder.databinding.ActivityMainBinding;
import com.wingsmight.audiorecorder.ui.settings.AppInfoActivity;
import com.wingsmight.audiorecorder.ui.settings.DoNotDisturbIntervalActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_main, R.id.navigation_settings, R.id.navigation_records)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        hideToolBar();
    }

    @SuppressLint("RestrictedApi")
    private void hideToolBar() {
        getSupportActionBar().setShowHideAnimationEnabled(false);
        getSupportActionBar().hide();
    }

    public void logOut(View view) {

    }
    public void setDoNotDisturbInterval(View view) {
        Intent myIntent = new Intent(MainActivity.this, DoNotDisturbIntervalActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
    public void showAppInfoPanel(View view) {
        Intent myIntent = new Intent(MainActivity.this, AppInfoActivity.class);
        MainActivity.this.startActivity(myIntent);
    }
}