package com.wingsmight.audiorecorder.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.databinding.ActivityDoNotDisturbIntervalBinding;

public class DoNotDisturbIntervalActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityDoNotDisturbIntervalBinding binding;

    private int fromHour = 22;
    private int fromMinute = 0;
    private int toHour = 8;
    private int toMinute = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_do_not_disturb_interval);

        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        fromHour = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_from_hour), fromHour);
        fromMinute = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_from_minute), fromMinute);
        toHour = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_to_hour), toHour);
        toMinute = sharedPreferences.getInt(getString(R.string.preference_do_not_disturb_to_minute), toMinute);

        TextView fromTimeText = findViewById(R.id.fromTime);
        fromTimeText.setText(fromHour + ":" + String.format("%02d", fromMinute));
        fromTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        fromTimeText.setText(hourOfDay + ":" + String.format("%02d", minute));
                        fromHour = hourOfDay;
                        fromMinute = minute;
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(DoNotDisturbIntervalActivity.this, timeSetListener, fromHour, fromMinute, true);
                timePickerDialog.show();
            }
        });

        TextView toTimeText = findViewById(R.id.toTime);
        toTimeText.setText(toHour + ":" + String.format("%02d", toMinute));
        toTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        toTimeText.setText(hourOfDay + ":" + String.format("%02d", minute));
                        toHour = hourOfDay;
                        toMinute = minute;
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(DoNotDisturbIntervalActivity.this, timeSetListener, toHour, toMinute, true);
                timePickerDialog.show();
            }
        });

        Button cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

                preferenceEditor.putInt(getString(R.string.preference_do_not_disturb_from_hour), fromHour);
                preferenceEditor.putInt(getString(R.string.preference_do_not_disturb_from_minute), fromMinute);
                preferenceEditor.putInt(getString(R.string.preference_do_not_disturb_to_hour), toHour);
                preferenceEditor.putInt(getString(R.string.preference_do_not_disturb_to_minute), toMinute);

                preferenceEditor.commit();

                finish();
            }
        });
    }
}