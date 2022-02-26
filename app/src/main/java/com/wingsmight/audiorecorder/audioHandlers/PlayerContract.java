package com.wingsmight.audiorecorder.audioHandlers;

public interface PlayerContract {

    interface PlayerCallback {
        void onPreparePlay();
        void onStartPlay();
        void onPlayProgress(long mills);
        void onStopPlay();
        void onPausePlay();
        void onSeek(long mills);
        void onError(Exception exception);
    }

    interface Player {
        void addPlayerCallback(PlayerContract.PlayerCallback callback);
        boolean removePlayerCallback(PlayerContract.PlayerCallback callback);
        void setData(String data);
        void playOrPause();
        void seek(long mills);
        void pause();
        void stop();
        boolean isPlaying();
        boolean isPause();
        long getPauseTime();
        void release();
    }
}

