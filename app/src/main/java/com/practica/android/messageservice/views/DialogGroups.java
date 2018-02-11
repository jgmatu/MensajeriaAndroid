package com.practica.android.messageservice.views;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.entities.Group;
import com.practica.android.messageservice.entities.User;

import static android.content.ContentValues.TAG;

class DialogGroups {

    private AppCompatActivity activity;
    private User user;
    private FirebaseDatabase database;

    DialogGroups(AppCompatActivity activity, User user, FirebaseDatabase database) {
        this.activity = activity;
        this.user = user;
        this.database = database;
    }

    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final EditText nameGroup = new EditText(activity);

        builder.setTitle("Fire Missiles");
        builder.setView(nameGroup);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = nameGroup.getText().toString();
                queryGroupInFirebase(name);
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


    private void queryGroupInFirebase(final String name) {
        DatabaseReference groupsRef = database.getReference(ActivityGroups.PATHGROUPS);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                checkFirebaseGroup(dataSnapshot, name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupsRef.addValueEventListener(postListener);
    }

    private void checkFirebaseGroup(DataSnapshot dataSnapshot, String name) {

        for (DataSnapshot groupSnapshot: dataSnapshot.getChildren()) {
            if (groupSnapshot.getKey().equals(name)) {
                showMsgInfo("El grupo ya está creado");
                return;
            }
        }

        if (name.matches("\\.\\#\\$\\,\\ \\[\\]")) {
            showMsgInfo("El nombre del grupo no es válido");
            return;
        }
        createGroup(name);
    }

    private void createGroup(String nameGroup) {
        DatabaseReference groupsRef = database.getReference(ActivityGroups.PATHGROUPS + nameGroup);

        groupsRef.setValue(new Group(nameGroup, this.user));
    }

    private void showMsgInfo(String text) {
        int time = Toast.LENGTH_SHORT;

        Toast msg = Toast.makeText(activity, text, time);
        msg.show();
    }
}
