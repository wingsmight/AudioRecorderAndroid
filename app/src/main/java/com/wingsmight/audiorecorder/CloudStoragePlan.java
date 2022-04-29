package com.wingsmight.audiorecorder;

public class CloudStoragePlan {
    public static long getSize(Plan plan) {
        switch (plan){
            case free200MB:
                return 200L * 1024L * 1024L;
            case paid2GB:
                return 2L * 1024L * 1024L * 1024L;
        }

        return 200L * 1024L * 1024L;
    }

    public enum Plan {
        free200MB,
        paid2GB
    }
}
