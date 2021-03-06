package com.wingsmight.audiorecorder.ui.records;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.wingsmight.audiorecorder.audioHandlers.VoiceRecorder;
import com.wingsmight.audiorecorder.data.Record;
import com.wingsmight.audiorecorder.extensions.StringExt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.FileHandler;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {
    private static final int DEFAULT_EXPANDED_POSITION = -1;


    private Context context;
    private RecyclerView recyclerView;

    private int expandedPosition = DEFAULT_EXPANDED_POSITION;


    public RecordsAdapter(Context context, ArrayList<Record> records, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.record, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record, parent, false);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.show(VoiceRecorder.records.get(position));

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
        return VoiceRecorder.records.size();
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
        private ImageButton shareRecordButton;

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
            shareRecordButton = itemView.findViewById(R.id.shareRecordButton);

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

            shareRecordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(record.fileName)));
                    shareIntent.setType("audio/3gp");
                    itemView.getContext().startActivity(Intent.createChooser(shareIntent, null));
                }
            });
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
