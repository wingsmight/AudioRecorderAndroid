package com.wingsmight.audiorecorder;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wingsmight.audiorecorder.ui.login.User;

import java.util.HashMap;
import java.util.Map;

public final class CloudDatabase {
    public static void addUser(User user) {
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("surname", user.getSurname());
        userMap.put("photoURL", user.getPhotoUrl());
        userMap.put("birthDate", user.getBirthDate());
        userMap.put("facebookProfileUrl", user.getFaceboolProfileUrl());
        userMap.put("phoneNumber", user.getPhoneNumber());
        userMap.put("storageSize", user.getCloudStorageSize());

        dataBase.collection("users")
                .add(userMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("SignUpActivity", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("SignUpActivity", "Error adding document", e);
                    }
                });
    }
}
