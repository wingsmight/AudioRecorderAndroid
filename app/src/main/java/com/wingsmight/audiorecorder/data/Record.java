package com.wingsmight.audiorecorder.data;

import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Record {
    public String fileName;
    private Date creatingDate;


    public Record(String fileName, Date creatingDate) {
        this.fileName = fileName;
        this.creatingDate = creatingDate;
    }


    @Override
    public String toString() {
        return fileName;
    }
    public Date getCreatingDate() {
        return creatingDate;
    }
    public void setCreatingDate(Date creatingDate) {
        this.creatingDate = creatingDate;
    }
    public long getDuration() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(fileName);
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Long.parseLong(durationStr);
    }
}
