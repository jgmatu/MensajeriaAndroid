package com.practica.android.messageservice.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.practica.android.messageservice.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ActivityGroups extends AppCompatActivity {

    private static final String TAG = ActivityGroups.class.getSimpleName();
    private static final String GROUPS = "/MessageService/Groups/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Set view toolbar...
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestGroups(database);
    }

    private void requestGroups(FirebaseDatabase database) {
        DatabaseReference groupsRef = database.getReference(GROUPS);

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> groups = new ArrayList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    groups.add(postSnapshot.getKey());
                }
                setViewGroups(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupsRef.addValueEventListener(postListener);
    }

    private void setViewGroups(List<String> groups) {
        TableLayout tableLayout = findViewById(R.id.table_groups);

        TableLayout.LayoutParams rows = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 0,
                1.0f / (float) groups.size());

        for (String group : groups) {
            TableRow row = new TableRow(this);

            row.setLayoutParams(rows);
            row.addView(getButton(group));
            tableLayout.addView(row);
        }
    }

    private Button getButton(String group) {
        Button button = new Button(this);
        TableRow.LayoutParams buttons = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT);

        button.setLayoutParams(buttons);
        button.setText(group);
        return button;
    }
}
