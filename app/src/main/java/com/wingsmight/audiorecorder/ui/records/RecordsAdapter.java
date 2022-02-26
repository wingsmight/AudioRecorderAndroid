package com.wingsmight.audiorecorder.ui.records;

import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.text.format.DateFormat;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.wingsmight.audiorecorder.R;
import com.wingsmight.audiorecorder.audioHandlers.AudioPlayer;
import com.wingsmight.audiorecorder.audioHandlers.PlayerContract;
import com.wingsmight.audiorecorder.data.Record;
import com.wingsmight.audiorecorder.extensions.StringExt;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {
    private static final int DEFAULT_EXPANDED_POSITION = -1;


    private Context context;
    private ArrayList<Record> records;
    private RecyclerView recyclerView;

    private int expandedPosition = DEFAULT_EXPANDED_POSITION;


    public RecordsAdapter(Context context, ArrayList<Record> records, RecyclerView recyclerView) {
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
        holder.show(records.get(position));

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
        if (isExpanded)
        {
            holder.expand();
        } else {
            holder.collapse();
        }
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {
        private View detailsView;
        private TextView recordTimeTextView;
        private TextView recordDateTextView;
        private SeekBar timeSlider;
        private TextView currentPlaybackTime;
        private TextView remainingPlaybackTime;
        private ImageButton backward10Button;
        private ImageButton forward10Button;
        private ImageButton playRecordButton;

        private Record record;
        private AudioPlayer player;

//        Handler updateTimeBarHandler = new Handler();
//        final Runnable updateTimeBar = new Runnable() {
//            @Override
//            public void run() {
//                long duration = record.getDuration();
//                long currentTime = player.getPauseTime();
//
//                timeSlider.setProgress((int)(currentTime / duration));
//
//                updateTimeBarHandler.postDelayed(updateTimeBar, 100);
//            }
//        };


        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            detailsView = itemView.findViewById(R.id.recordDetails);
            recordTimeTextView = itemView.findViewById(R.id.recordTimeText);
            recordDateTextView = itemView.findViewById(R.id.recordDateText);
            timeSlider = itemView.findViewById(R.id.playerTimeSlider);
            currentPlaybackTime = itemView.findViewById(R.id.currentPlaybackTime);
            remainingPlaybackTime = itemView.findViewById(R.id.remainingPlaybackTime);
            backward10Button = itemView.findViewById(R.id.backward10Button);
            forward10Button = itemView.findViewById(R.id.forward10Button);
            playRecordButton = itemView.findViewById(R.id.playRecordButton);

            player = AudioPlayer.getInstance();
            player.addPlayerCallback(new PlayerContract.PlayerCallback() {
                @Override
                public void onPreparePlay() {
                    playRecordButton.setImageResource(R.drawable.ic_play_arrow_24);
                }

                @Override
                public void onStartPlay() {
                    playRecordButton.setImageResource(R.drawable.ic_pause_24);
                }

                @Override
                public void onPlayProgress(long mills) {
                    long duration = record.getDuration();

                    new Handler().post(new Runnable() {
                        public void run() {
                            timeSlider.setProgress((int)(mills * 100 / duration));

                            currentPlaybackTime.setText(StringExt.getTime(mills));
                        }
                    });
                }

                @Override
                public void onStopPlay() {
                    playRecordButton.setImageResource(R.drawable.ic_play_arrow_24);

                    new Handler().post(new Runnable() {
                        public void run() {
                            timeSlider.setProgress(0);

                            currentPlaybackTime.setText("0:00");
                        }
                    });
                }

                @Override
                public void onPausePlay() {
                    playRecordButton.setImageResource(R.drawable.ic_play_arrow_24);
                }

                @Override
                public void onSeek(long mills) {

                }

                @Override
                public void onError(Exception exception) {

                }
            });

            timeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                long seekTime = 0;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        long duration = record.getDuration();

                        seekTime = duration / 100 * progress;
                    }
                }

                @Override public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override public void onStopTrackingTouch(SeekBar seekBar) {
                    player.seek(seekTime);
                }
            });

            playRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    player.playOrPause();
                }
            });

            forward10Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long currentTime = player.getPauseTime();
                    player.seek(currentTime + (10 * 1000));
                }
            });
            backward10Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    long currentTime = player.getPauseTime();
                    player.seek(currentTime - (10 * 1000));
                }
            });

            currentPlaybackTime.setText("0:00");
        }


        public void show(Record record) {
            this.record = record;

            recordTimeTextView.setText(DateFormat.format("hh:mm", record.getCreatingDate()));
            recordDateTextView.setText(DateFormat.format("dd.MM.yyyy", record.getCreatingDate()));

            remainingPlaybackTime.setText(StringExt.getTime(record.getDuration()));
        }
        public void setDetailsVisibility(int visibility) {
            detailsView.setVisibility(visibility);
        }
        public void expand() {
            player.setData(record.fileName);
        }
        public void collapse() {
            player.stop();
        }
    }
}
