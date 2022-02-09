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
}
