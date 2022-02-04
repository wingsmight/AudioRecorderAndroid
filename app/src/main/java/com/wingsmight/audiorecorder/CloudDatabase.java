package com.wingsmight.audiorecorder;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
                        Log.d("CloudDatabase", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CloudDatabase", "Error adding document", e);
                    }
                });
    }
    public static void loadUser(String userEmail, Consumer<Map<String, Object>> onSuccess) {
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();

        dataBase.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> userData = document.getData();
                                Log.d("CloudDatabase", document.getId() + " => " + userData);

                                onSuccess.accept(userData);
                            }
                        } else {
                            Log.w("CloudDatabase", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
