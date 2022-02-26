package com.wingsmight.audiorecorder.audioHandlers;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayer implements PlayerContract.Player, MediaPlayer.OnPreparedListener {
    public final static int PLAYBACK_VISUALIZATION_INTERVAL = 26;
    private final List<PlayerContract.PlayerCallback> actionsListeners = new ArrayList<>();

    private MediaPlayer mediaPlayer;
    private boolean isPrepared = false;
    private boolean isPause = false;
    private long seekPos = 0;
    private long pausePos = 0;
    private String dataSource = null;
    private final Handler handler = new Handler();


    private static class SingletonHolder {
        private static final AudioPlayer singleton = new AudioPlayer();

        public static AudioPlayer getSingleton() {
            return SingletonHolder.singleton;
        }
    }

    public static AudioPlayer getInstance() {
        return SingletonHolder.getSingleton();
    }

    private AudioPlayer() {}

    @Override
    public void addPlayerCallback(PlayerContract.PlayerCallback callback) {
        if (callback != null) {
            actionsListeners.add(callback);
        }
    }

    @Override
    public boolean removePlayerCallback(PlayerContract.PlayerCallback callback) {
        if (callback != null) {
            return actionsListeners.remove(callback);
        }
        return false;
    }

    @Override
    public void setData(String data) {
        if (mediaPlayer != null && dataSource != null && dataSource.equals(data)) {
            //Do nothing
        } else {
            dataSource = data;
            restartPlayer();
        }
    }

    private void restartPlayer() {
        if (dataSource != null) {
            try {
                isPrepared = false;
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(dataSource);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } catch (IOException | IllegalArgumentException | IllegalStateException | SecurityException e) {
                onError(e);

                Log.e("AudioPlayer", e.getMessage());
            }
        }
    }

    @Override
    public void playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    isPause = false;
                    if (!isPrepared) {
                        try {
                            mediaPlayer.setOnPreparedListener(this);
                            mediaPlayer.prepareAsync();
                        } catch (IllegalStateException ex) {
                            //Log.e("AudioPlayer", ex.getMessage());
                            restartPlayer();
                            mediaPlayer.setOnPreparedListener(this);
                            try {
                                mediaPlayer.prepareAsync();
                            } catch (IllegalStateException e) {
                                restartPlayer();

                                Log.w("AudioPlayer", e.getMessage());
                            }
                        }
                    } else {
                        mediaPlayer.start();
                        mediaPlayer.seekTo((int) pausePos);
                        onStartPlay();
                        mediaPlayer.setOnCompletionListener(mp -> {
                            stop();
                            onStopPlay();
                        });

                        schedulePlaybackTimeUpdate();
                    }
                    pausePos = 0;
                }
            }
        } catch(IllegalStateException e){
            Log.e("AudioPlayer", e.getMessage());
        }
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        if (mediaPlayer != mp) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = mp;
        }
        onPreparePlay();
        isPrepared = true;
        mediaPlayer.start();
        mediaPlayer.seekTo((int) seekPos);
        onStartPlay();
        mediaPlayer.setOnCompletionListener(mp1 -> {
            stop();
            onStopPlay();
        });

        schedulePlaybackTimeUpdate();
    }

    @Override
    public void seek(long mills) {
        seekPos = mills;
        if (isPause) {
            pausePos = mills;
        }
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo((int) seekPos);
                onSeek((int) seekPos);
            }
        } catch(IllegalStateException e){
            Log.e("AudioPlayer", e.getMessage());
        }
    }

    @Override
    public void pause() {
        stopPlaybackTimeUpdate();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                onPausePlay();
                seekPos = mediaPlayer.getCurrentPosition();
                isPause = true;
                pausePos = seekPos;
            }
        }
    }

    @Override
    public void stop() {
        stopPlaybackTimeUpdate();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.setOnCompletionListener(null);
            isPrepared = false;
            onStopPlay();
            mediaPlayer.getCurrentPosition();
            seekPos = 0;
        }
        isPause = false;
        pausePos = 0;
    }

    @Override
    public boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch(IllegalStateException e){
            Log.e("AudioPlayer", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean isPause() {
        return isPause;
    }

    @Override
    public long getPauseTime() {
        return seekPos;
    }

    @Override
    public void release() {
        stop();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPrepared = false;
        isPause = false;
        dataSource = null;
        actionsListeners.clear();
    }

    private void schedulePlaybackTimeUpdate() {
        handler.postDelayed(() -> {
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int curPos = mediaPlayer.getCurrentPosition();
                    Log.i("curPos", String.valueOf(curPos));
                    onPlayProgress(curPos);
                }
                schedulePlaybackTimeUpdate();
            } catch(IllegalStateException e){
                onError(e);

                Log.e("AudioPlayer", e.getMessage());
            }
        }, PLAYBACK_VISUALIZATION_INTERVAL);
    }

    private void stopPlaybackTimeUpdate() {
        handler.removeCallbacksAndMessages(null);
    }

    private void onPreparePlay() {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onPreparePlay();
            }
        }
    }

    private  void onStartPlay() {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onStartPlay();
            }
        }
    }

    private void onPlayProgress(long mills) {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onPlayProgress(mills);
            }
        }
    }

    private void onStopPlay() {
        if (!actionsListeners.isEmpty()) {
            for (int i = actionsListeners.size()-1; i >= 0; i--) {
                actionsListeners.get(i).onStopPlay();
            }
        }
    }

    private void onPausePlay() {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onPausePlay();
            }
        }
    }

    private void onSeek(long mills) {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onSeek(mills);
            }
        }
    }

    private void onError(Exception exception) {
        if (!actionsListeners.isEmpty()) {
            for (int i = 0; i < actionsListeners.size(); i++) {
                actionsListeners.get(i).onError(exception);
            }
        }
    }
}
