package com.wingsmight.audiorecorder.extensions;

public class StringExt {
    public static String slice(String rawResult, String startText, String finishText) {
        try {
            String result = rawResult.substring(rawResult.indexOf(startText) + startText.length());
            result = result.trim();
            result = result.substring(0, result.indexOf(finishText));
            result = result.trim();

            return result;
        } catch(Exception exception) {
            return "";
        }
    }
    public static String getTime(long durationInMillis) {
        long millis = durationInMillis % 1000;
        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;

        return String.format("%d:%02d", minute, second);
    }
}
