// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.wingsmight.audiorecorder.audioHandlers;

import android.util.Log;

import com.wingsmight.audiorecorder.extensions.StringExt;

import org.vosk.android.RecognitionListener;


public class SpeechRecognitionListener implements
        RecognitionListener {
    private VoiceRecorder voiceRecorder;


    public SpeechRecognitionListener(VoiceRecorder voiceRecorder) {
        this.voiceRecorder = voiceRecorder;
    }


    @Override
    public void onResult(String hypothesis) {
        handleResult(removeTextTag(hypothesis));
    }
    @Override
    public void onFinalResult(String hypothesis) {
        handleResult(removeTextTag(hypothesis));
    }
    @Override
    public void onPartialResult(String hypothesis) {
        handleResult(removePartialTag(hypothesis));
    }
    @Override
    public void onError(Exception e) {
        Log.e("SpeechRecognition", e.getMessage());
    }
    @Override
    public void onTimeout() {
        Log.e("SpeechRecognition", "Timeout!");
    }


    private void handleResult(String result) {
        if (!result.isEmpty()) {
            Log.i("SpeechRecognition", "Speech: " + result);


            if (!voiceRecorder.isRecording()) {
                voiceRecorder.start();
            } else {
                voiceRecorder.resetAutoStop();
            }
        }
    }
    private String removePartialTag(String rawResult) {
        return StringExt.slice(rawResult, "\"partial\" : \"", "\"\n}");
    }
    private String removeTextTag(String rawResult) {
        return StringExt.slice(rawResult, "text\" : \"", "\"\n}");
    }
}
