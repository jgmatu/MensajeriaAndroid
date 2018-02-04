package com.practica.android.messageservice.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.practica.android.messageservice.R;

import static android.content.ContentValues.TAG;

class DialogAddGroup {

    private AppCompatActivity activity;

    DialogAddGroup(AppCompatActivity activity) {
        this.activity = activity;
    }

    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Fire Missiles");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.e(TAG, "Debug Fire!");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.e(TAG, "Debug Cancel!");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
