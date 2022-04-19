package com.wingsmight.audiorecorder.ui.records;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.audioHandlers.VoiceRecorder;
import com.wingsmight.audiorecorder.data.Record;
import com.wingsmight.audiorecorder.databinding.FragmentRecordsBinding;

import java.util.Date;

public class RecordsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecordsAdapter recordsAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_records, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView)view.findViewById(R.id.recordsList);
        recordsAdapter = new RecordsAdapter(getContext(), VoiceRecorder.records, recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recordsAdapter);
    }


    public RecordsAdapter getRecordsAdapter() {
        return  recordsAdapter;
    }
}