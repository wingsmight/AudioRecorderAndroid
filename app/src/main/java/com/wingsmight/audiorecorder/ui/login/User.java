package com.wingsmight.audiorecorder.ui.login;

import com.wingsmight.audiorecorder.CloudStoragePlan;

import java.util.Date;

/**
 * Created by shiva on 31-01-2018.
 */

public class User {
    String Name;
    String Surname;
    String Email;
    Date BirthDate;
    long createdAt;
    int storageSize;

    public User(String Name, String Surname, String email, Date BirthDate, long createdAt, int storageSize) {
        this.Name=Name;
        this.Surname=Surname;
        this.Email=email;
        this.BirthDate = BirthDate;
        this.createdAt=createdAt;
        this.storageSize = storageSize;
    }


    public String getName() {
        return Name;
    }
    public String getSurname() {
        return Surname;
    }
    public String getEmail() {
        return Email;
    }
    public Date getBirthDate() {
        return BirthDate;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public String getPhotoUrl() {
        return "";
    }
    public String getFaceboolProfileUrl() {
        return "";
    }

    public String getPhoneNumber() {
        return "";
    }

    public int getCloudStorageSize() {
        return storageSize;
    }
}
