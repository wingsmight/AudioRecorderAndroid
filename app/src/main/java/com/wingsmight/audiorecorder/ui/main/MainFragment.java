package com.wingsmight.audiorecorder.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wingsmight.audiorecorder.MainActivity;
import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;

    private ImageButton switchRecordButton;
    private View[] volumeRounds = new View[2];
    private TextView timerTextView;

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

        timerTextView = binding.timer;

        volumeRounds[0] = binding.volumeRound0;
        volumeRounds[1] = binding.volumeRound1;

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void startRecording() {
        switchRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop_square));
        timerTextView.setVisibility(View.VISIBLE);
        setVolumeRoundsVisibility(View.VISIBLE);
    }
    private void stopRecording() {
        switchRecordButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24));
        timerTextView.setVisibility(View.INVISIBLE);
        setVolumeRoundsVisibility(View.INVISIBLE);
    }
    private void setVolumeRoundsVisibility(int visibility) {
        for (View volumeRound : volumeRounds) {
            volumeRound.setVisibility(visibility);
        }
    }
}