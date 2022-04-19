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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public final class SpeechRecognize {
    private static boolean isModelLoading;
    private static Model model;
    private static SpeechService speechService;


    public static void Init(Context context, Runnable onReady) {
//        String outputPath = (new File("model-small-ru-0.22", "model")).getAbsolutePath();
//        this.model = new Model(outputPath);
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onReady.run();
//            }
//        }, 100);

        if (!isModelLoading || model != null) {
            isModelLoading = true;
            StorageService.unpack(context, "model-small-ru-0.22", "model",
                    (createdModel) -> {
                        model = createdModel;

                        onReady.run();
                    },
                    (exception) -> Log.e("AudioRecognize", "Failed to unpack the model" + exception.getMessage()));
        }
    }


    public static void recognizeMicrophone(RecognitionListener listener) {
        if (speechService != null) {
            speechService.stop();
            speechService = null;
        } else {
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(listener);
            } catch (IOException e) {
                Log.e("AudioRecognize", e.getMessage());
            }
        }
    }
    public static void pause() {
        if (speechService != null) {
            speechService.setPause(true);
        }
    }
    public static void stop() {
        if (speechService != null) {
            speechService.stop();
        }
    }
    public static boolean isReady() {
        return model != null;
    }
}
