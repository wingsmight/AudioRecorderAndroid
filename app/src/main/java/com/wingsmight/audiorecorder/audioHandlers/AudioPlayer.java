package com.wingsmight.audiorecorder.audioHandlers;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer player = null;


    public void start(String fileName) {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("AudioPlayer", "prepare() failed");
        }
    }
    public void stop() {
        player.release();
        player = null;
    }
}
