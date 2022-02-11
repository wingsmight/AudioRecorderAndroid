package com.wingsmight.audiorecorder.audioHandlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wingsmight.audiorecorder.CloudDatabase;
import com.wingsmight.audiorecorder.data.Record;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class VoiceRecorder {
    private static String RECORDS_SAVE_KEY = "RECORDS";


    private static long MAX_RECORD_COUNT = 50;
    private static long MAX_SILENCE_DURATION_MILLISECONDS = 10 * 1000; // 10 sec
    private static long LIMIT_RECORD_DURATION_MILLISECONDS = 60 * 3 * 1000;// 3 mins

    private Context context;
    private MediaRecorder recorder = null;
    private Handler stopAtLimitHandler;
    private Handler autoStopHandler;
    private Runnable stopAtLimit;
    private Runnable autoStop;
    private float volume;
    private String lastRecordFileName;
    public static ArrayList<Record> records = new ArrayList<>();


    public VoiceRecorder(Context context) {
        this.context = context;

        loadRecords();
    }


    public void start() {
        lastRecordFileName = generateNewFileName();

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(lastRecordFileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        Handler showAmplitudeHandler = new Handler(Looper.getMainLooper());
        showAmplitudeHandler.post(new Runnable() {
            @Override
            public void run() {
                if (recorder == null)
                    return;

                volume = (float) Math.log10(Math.max(1, recorder.getMaxAmplitude() - 500)) * ScreenUtils.dp2px(context, 20);

                showAmplitudeHandler.postDelayed(this, 50);
            }
        });

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("VoiceRecorder", "prepare() failed cause of " + e.getMessage());
        }

        recorder.start();

        stopAtLimitHandler = new Handler(Looper.getMainLooper());
        stopAtLimit = () -> {
            Log.i("VoiceRecorder", "Audio Recorder was auto stopped");

            stop();
        };
        stopAtLimitHandler.postDelayed(stopAtLimit, LIMIT_RECORD_DURATION_MILLISECONDS);

        autoStopHandler = new Handler(Looper.getMainLooper());
        autoStop = () -> {
            Log.i("VoiceRecorder", "Audio Recorder was auto stopped");

            stop();
        };
        autoStopHandler.postDelayed(autoStop, MAX_SILENCE_DURATION_MILLISECONDS);

        resetAutoStop();
    }
    public void stop() {
        if (!isRecording())
            return;

        stopAtLimitHandler.removeCallbacks(stopAtLimit);
        autoStopHandler.removeCallbacks(autoStop);

        recorder.stop();
        recorder.release();
        recorder = null;

        saveRecord(lastRecordFileName);

        CloudDatabase.uploadRecord(lastRecordFileName);

        checkForRecordLimit();
    }
    public boolean isRecording() {
        return recorder != null;
    }
    public void resetAutoStop() {
        autoStopHandler.removeCallbacks(autoStop);
        autoStopHandler.postDelayed(autoStop, MAX_SILENCE_DURATION_MILLISECONDS);
    }
    public float getVolume()
    {
        return volume;
    }


    private String generateNewFileName()
    {
        String fileName = context.getExternalCacheDir().getAbsolutePath();
        fileName += Calendar.getInstance().getTime();
        fileName += ".3gp";

        return fileName;
    }
    private void saveRecord(String fileName) {
        Record newRecord = new Record(fileName, Calendar.getInstance().getTime());

        records.add(newRecord);

        saveRecords();
    }
    private void saveRecordsOld() {
        try {
            FileOutputStream fos = context.openFileOutput(RECORDS_SAVE_KEY, Context.MODE_PRIVATE);

            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(records.get(0));
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveRecords() {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        Gson gson = new Gson();
        ed.putString(RECORDS_SAVE_KEY, gson.toJson(records));
        ed.commit();
    }
    private void loadRecords() {
        SharedPreferences mPrefs = context.getSharedPreferences(context.getApplicationInfo().name, Context.MODE_PRIVATE);
        String recordsGson = mPrefs.getString(RECORDS_SAVE_KEY, "");
        if (!recordsGson.isEmpty()) {
            Gson gson = new Gson();
            try {
                records = gson.fromJson(recordsGson, new TypeToken<ArrayList<Record>>(){}.getType());
            } catch (Exception exception) {
                Log.i("VoiceRecorder", "records cannot be loaded");
            }
        }
    }
    private void checkForRecordLimit() {
        while (records.size() > MAX_RECORD_COUNT) {
            Record lastRecord = records.remove(records.size() - 1);

            // delete from storage
            File deletedFile = new File(lastRecord.fileName);
            if (deletedFile.exists()) {
                if (deletedFile.delete()) {
                    Log.i("VoiceRecorder", "file Deleted :" + lastRecord.fileName);
                } else {
                    Log.i("VoiceRecorder", "file not Deleted :" + lastRecord.fileName);
                }
            }
        }
    }
}
