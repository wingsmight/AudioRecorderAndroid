package com.wingsmight.audiorecorder.data;

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
}
