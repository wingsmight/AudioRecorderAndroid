package com.wingsmight.audiorecorder.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognitionListener;
import com.wingsmight.audiorecorder.audioHandlers.SpeechRecognize;
import com.wingsmight.audiorecorder.databinding.FragmentMainBinding;

import org.vosk.LibVosk;
import org.vosk.LogLevel;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private SpeechRecognize speechRecognize;
    private SpeechRecognitionListener speechListener = new SpeechRecognitionListener();

    private ImageButton switchRecordButton;
    private View[] volumeRounds = new View[2];
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

        volumeRounds[0] = binding.volumeRound0;
        volumeRounds[1] = binding.volumeRound1;

        modelLoadingBar = binding.modelLoadingBar;
        modelLoadingBar.show();

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
        timerTextView.setVisibility(View.VISIBLE);
        setVolumeRoundsVisibility(View.VISIBLE);

        speechRecognize.recognizeMicrophone(speechListener);
    }
    private void stopRecording() {
        switchRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24));
        timerTextView.setVisibility(View.INVISIBLE);
        setVolumeRoundsVisibility(View.INVISIBLE);

        speechRecognize.stop();
    }
    private void setVolumeRoundsVisibility(int visibility) {
        for (View volumeRound : volumeRounds) {
            volumeRound.setVisibility(visibility);
        }
    }
}