package com.wingsmight.audiorecorder.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.Stopwatch;
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognitionListener;
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognize;
import com.wingsmight.audiorecorder.audioHandlers.VoiceRecorder;
import com.wingsmight.audiorecorder.databinding.FragmentMainBinding;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.android.SpeechService;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private SpeechRecognize speechRecognize;
    private SpeechRecognitionListener speechListener;
    private VoiceRecorder voiceRecorder;
    private Stopwatch stopwatch;

    private ImageButton switchRecordButton;
    private View volumeRound;
    private TextView timerTextView;
    private ContentLoadingProgressBar modelLoadingBar;

    private boolean isRecording = false;


    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        switchRecordButton = binding.switchRecordButton;
        switchRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRecording = !isRecording;

                if (isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });
        switchRecordButton.setEnabled(false);

        timerTextView = binding.timer;

        volumeRound = binding.volumeRound0;
        volumeRound.setVisibility(View.INVISIBLE);

        modelLoadingBar = binding.modelLoadingBar;
        modelLoadingBar.show();

        voiceRecorder = new VoiceRecorder(getContext());
        speechListener = new SpeechRecognitionListener(voiceRecorder);

        ViewGroup.LayoutParams layoutParams = volumeRound.getLayoutParams();
        int initRadius =  layoutParams.width;

        Handler updateVolumeRoundRadiusHandler = new Handler(Looper.getMainLooper());
        updateVolumeRoundRadiusHandler.post(new Runnable() {
            @Override
            public void run() {
                float volume = voiceRecorder.getVolume();
                if (volume != 0)
                {
                    ViewGroup.LayoutParams layoutParams = volumeRound.getLayoutParams();

                    layoutParams.width = initRadius + (int) volume;
                    layoutParams.height = initRadius + (int) volume;
                    volumeRound.setLayoutParams(layoutParams);
                }

                updateVolumeRoundRadiusHandler.postDelayed(this, 20);
            }
        });

        stopwatch = new Stopwatch(new Stopwatch.IStopWatch() {
            @Override
            public void onSetTime(String time) {
                timerTextView.setText(time);
            }
        });

        voiceRecorder.callback = new VoiceRecorder.IRecordable() {
            @Override
            public void onStart() {
                volumeRound.setVisibility(View.VISIBLE);
                timerTextView.setVisibility(View.VISIBLE);
                stopwatch.start();
            }

            @Override
            public void onStop() {
                volumeRound.setVisibility(View.GONE);
                timerTextView.setVisibility(View.GONE);
                stopwatch.stop();
            }
        };

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
    public void startService(SpeechRecognize speechRecognize) {
        this.speechRecognize = speechRecognize;

        modelLoadingBar.hide();
        switchRecordButton.setEnabled(true);
    }

    private void startRecording() {
        switchRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_square));

        speechRecognize.recognizeMicrophone(speechListener);
    }
    private void stopRecording() {
        switchRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24));
        timerTextView.setVisibility(View.INVISIBLE);
        volumeRound.setVisibility(View.INVISIBLE);

        speechRecognize.stop();
        voiceRecorder.stop();
    }
}