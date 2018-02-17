package com.practica.android.messageservice.tasks;

import com.google.firebase.database.DatabaseError;


public interface OnGetAllDataMessages {
    void onStart();
    void onSuccess(ImageMessages messageData);
    void onFailed(DatabaseError databaseError);
}