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

import org.vosk.android.RecognitionListener;


public class SpeechRecognitionListener implements
        RecognitionListener {
    @Override
    public void onResult(String hypothesis) {
        Log.i("SpeechRecognition", "Result: " + hypothesis);
    }

    @Override
    public void onFinalResult(String hypothesis) {
        Log.i("SpeechRecognition", "Final result: " + hypothesis);
    }

    @Override
    public void onPartialResult(String hypothesis) {
        Log.i("SpeechRecognition", "Speech: " + hypothesis);
    }

    @Override
    public void onError(Exception e) {
        Log.e("SpeechRecognition", e.getMessage());
    }

    @Override
    public void onTimeout() {
        Log.e("SpeechRecognition", "Timeout!");
    }
}
