package com.wingsmight.audiorecorder.audioHandlers;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;

public class VoiceRecorder {
    private static long MAX_SILENCE_DURATION_MILLISECONDS = 10 * 1000; // 10 sec
    private static long LIMIT_RECORD_DURATION_MILLISECONDS = 60 * 3 * 1000;// 3 mins
    private static String fileName = "test";

    private Context context;
    private MediaRecorder recorder = null;
    private Handler stopAtLimitHandler;
    private Handler autoStopHandler;
    private Runnable stopAtLimit;
    private Runnable autoStop;


    public VoiceRecorder(Context context) {
        this.context = context;

        // TEST
        fileName = context.getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        // TEST
    }


    public void start() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

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
        stopAtLimitHandler.removeCallbacks(stopAtLimit);
        autoStopHandler.removeCallbacks(autoStop);

        recorder.stop();
        recorder.release();
        recorder = null;
    }
    public boolean isRecording() {
        return recorder != null;
    }
    public void resetAutoStop() {
        autoStopHandler.removeCallbacks(autoStop);
        autoStopHandler.postDelayed(autoStop, MAX_SILENCE_DURATION_MILLISECONDS);
    }
}
