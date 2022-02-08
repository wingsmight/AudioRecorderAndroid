package com.wingsmight.audiorecorder.ui.records;

import android.content.Context;
import android.text.Layout;
import android.text.format.DateFormat;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wingsmight.audiorecorder.R;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {
    private static final int DEFAULT_EXPANDED_POSITION = -1;


    private Context context;
    private Record[] records;
    private RecyclerView recyclerView;

    private int expandedPosition = DEFAULT_EXPANDED_POSITION;


    public RecordsAdapter(Context context, Record[] records, RecyclerView recyclerView) {
        this.context = context;
        this.records = records;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.record, parent, false);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.show(records[position]);

        final boolean isExpanded = position == expandedPosition;
        holder.setDetailsVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedPosition = isExpanded ? DEFAULT_EXPANDED_POSITION : position;
                TransitionManager.beginDelayedTransition(recyclerView);

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.length;
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        private View detailsView;
        private TextView recordTimeTextView;
        private TextView recordDateTextView;


        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            detailsView = itemView.findViewById(R.id.recordDetails);
            recordTimeTextView = itemView.findViewById(R.id.recordTimeText);
            recordDateTextView = itemView.findViewById(R.id.recordDateText);
        }


        public void show(Record record) {
            Date creatingDate = record.getCreatingDate();

            recordTimeTextView.setText(DateFormat.format("hh:mm", creatingDate));
            recordDateTextView.setText(DateFormat.format("dd.MM.yyyy", creatingDate));
        }
        public void setDetailsVisibility(int visibility) {
            detailsView.setVisibility(visibility);
        }
    }
}
