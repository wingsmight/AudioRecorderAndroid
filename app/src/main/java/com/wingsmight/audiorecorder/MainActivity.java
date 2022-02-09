package com.wingsmight.audiorecorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.auth.FirebaseAuth;
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognize;
import com.wingsmight.audiorecorder.databinding.ActivityMainBinding;
import com.wingsmight.audiorecorder.ui.login.SignUpActivity;
import com.wingsmight.audiorecorder.ui.main.MainFragment;
import com.wingsmight.audiorecorder.ui.settings.AppInfoActivity;
import com.wingsmight.audiorecorder.ui.settings.DoNotDisturbIntervalActivity;

import org.vosk.LibVosk;
import org.vosk.LogLevel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private ActivityMainBinding binding;
    private MainFragment mainFragment;
    private SpeechRecognize speechRecognize;

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
        NavigationUI.setupWithNavController(binding.navView, navController);

        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragmentList = fragmentManager.getFragments();
        FragmentManager childFragmentManager = fragmentList.get(0).getChildFragmentManager();
        mainFragment = (MainFragment)childFragmentManager.getFragments().get(0);

        // vosk-api
        LibVosk.setLogLevel(LogLevel.DEBUG);

        // Check if user has given permission to record audio, init the model after permission is granted
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            speechRecognize = new SpeechRecognize(getApplicationContext(), new Runnable() {
                @Override
                public void run() {
                    mainFragment.startService(speechRecognize);
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                speechRecognize = new SpeechRecognize(getApplicationContext(), new Runnable() {
                    @Override
                    public void run() {
                        mainFragment.startService(speechRecognize);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Приложение не может работать без доступа к микрофону", Toast.LENGTH_LONG).show();
            }
        }
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
}