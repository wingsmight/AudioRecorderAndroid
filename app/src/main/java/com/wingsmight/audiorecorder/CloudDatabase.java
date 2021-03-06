package com.wingsmight.audiorecorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.wingsmight.audiorecorder.ui.login.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CloudDatabase {
    private static long storageSize = CloudStoragePlan.getSize(CloudStoragePlan.Plan.free200MB);
    private static long filledStorageSize = 0;

    private static long usedStorageSize = 0;
    private static Double storageFillPercent = 0.0;
    private static long availableStorageSize = CloudStoragePlan.getSize(CloudStoragePlan.Plan.free200MB);


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
    public static void uploadRecord(String fileName) {
        if (isStorageFull()) {
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("AudioRecords").child(currentUserId).child(fileName);

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();

        ref.putFile(Uri.fromFile(new File(fileName)), metadata);

        updateUsedStorageSize();
    }
    public static void getStorageSize(Context context, Consumer<Long> onSuccess) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "Test01@test.com");
        loadUser(email, new Consumer<Map<String, Object>>() {
            @Override
            public void accept(Map<String, Object> userData) {
                long storedSize = (Long)userData.get("storageSize");
                onSuccess.accept(storedSize);
            }
        });
    }
    public static void getUsedStorageSize(Consumer<Long> onSuccess) {
        final long[] storedBytes = {0};
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("AudioRecords").child(currentUserId);

        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> storageReferences = listResult.getItems();
                for (StorageReference storageReference :
                        storageReferences) {
                    storedBytes[0] += storageReference.getMetadata().getResult().getSizeBytes();
                }

                onSuccess.accept(storedBytes[0]);
            }
        });
    }
    public static void updateUsedStorageSize() {
        final long[] storedBytes = {0};
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref = firebaseStorage.getReference().child("AudioRecords").child(currentUserId);

        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult storageListResult) {
                List<StorageReference> items = storageListResult.getItems();
                for (StorageReference item : items) {
                    item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata itemMetadata) {
                            storedBytes[0] += itemMetadata.getSizeBytes();
                            usedStorageSize = storedBytes[0];
                            storageFillPercent = (double) usedStorageSize / (double) availableStorageSize;

                            // save it
                        }
                    });
                }
            }
        });
    }
    public static void changeStoragePlan(CloudStoragePlan.Plan newPlan, Consumer<Boolean> onSuccess) {
        availableStorageSize = CloudStoragePlan.getSize(newPlan);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        // Read the documents at a specific path
        database.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update(new HashMap<String, Object>()
                                {{
                                    put("storageSize", availableStorageSize);
                                }});

                                storageFillPercent = (double) usedStorageSize / (double) availableStorageSize;

                                //save it

                                onSuccess.accept(true);
                            }
                        } else {
                            Log.w("CloudDatabase", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private static boolean isStorageFull() {
        return usedStorageSize >= availableStorageSize;
    }
}
