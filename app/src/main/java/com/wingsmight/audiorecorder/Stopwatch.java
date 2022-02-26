package com.wingsmight.audiorecorder;

import android.os.Handler;
import java.util.Locale;

public class Stopwatch {
    private String time = "0:00";
    private int seconds = 0;
    private boolean running;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            setValues();
            seconds++;

            handler.postDelayed(this, 1000);
        }
    };

    public IStopWatch callback;


    public Stopwatch(IStopWatch callback) {
        this.callback = callback;

        resetValues();
    }


    public void start() {
        handler.post(runnable);
    }
    public void stop() {
        handler.removeCallbacks(runnable);
        resetValues();
    }

    private void setValues() {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        time = String.format(Locale.getDefault(),
                        "%d:%02d:%02d",
                            hours, minutes, secs);

        callback.onSetTime(time);
    }
    private void resetValues() {
        seconds = 0;

        setValues();
    }


    public interface IStopWatch {
        void onSetTime(String time);
    }
}


