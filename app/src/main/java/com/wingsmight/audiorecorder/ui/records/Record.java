package com.wingsmight.audiorecorder.ui.records;

import java.util.Date;

public class Record {
    private Date creatingDate;


    public Record(Date creatingDate) {
        this.creatingDate = creatingDate;
    }


    public Date getCreatingDate() {
        return creatingDate;
    }
    public void setCreatingDate(Date creatingDate) {
        this.creatingDate = creatingDate;
    }
}
