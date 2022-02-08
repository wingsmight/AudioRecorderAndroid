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

import com.wingsmight.audiorecorder.databinding.FragmentRecordsBinding;

import java.util.Date;

public class RecordsFragment extends Fragment {
    private FragmentRecordsBinding binding;
    private RecyclerView recyclerView;

    private Record[] testRecords = {
            new Record(new Date(102)),
            new Record(new Date(10400505))
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecordsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recordsList;
        recyclerView.setAdapter(new RecordsAdapter(getContext(), testRecords, recyclerView));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}