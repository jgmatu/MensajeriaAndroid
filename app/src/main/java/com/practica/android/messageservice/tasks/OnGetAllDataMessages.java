package com.practica.android.messageservice.tasks;

import com.google.firebase.database.DatabaseError;


public interface OnGetAllDataMessages {
    void onStart();
    void onSuccess();
    void onFailed(DatabaseError databaseError);
}