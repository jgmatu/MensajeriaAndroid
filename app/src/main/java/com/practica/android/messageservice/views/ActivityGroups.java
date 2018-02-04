package com.practica.android.messageservice.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public static final String GROUPVALUE = "group";
    public static final String PATHGROUPS = "/MessageService/Groups/";

    private static final String TAG = ActivityGroups.class.getSimpleName();

    private static final float SIZETEXT = 24;
    private static final int HIGHBUTTONS = 240;
    private static final int MARGINROWS = 25;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Set view toolbar...
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queryFirebaseGroups(database);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_new_group) {
            DialogAddGroup dialogAddGroup = new DialogAddGroup(this);
            dialogAddGroup.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryFirebaseGroups(FirebaseDatabase database) {
        DatabaseReference groupsRef = database.getReference(PATHGROUPS);

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

        rows.setMargins(MARGINROWS, MARGINROWS, MARGINROWS, MARGINROWS);
        tableLayout.removeAllViews();
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
                TableRow.LayoutParams.MATCH_PARENT, HIGHBUTTONS, 1);

        button.setLayoutParams(buttons);
        button.setText(group);
        button.setTextSize(SIZETEXT);
        button.setOnClickListener(new GroupButton(group));
        return button;
    }

    private class GroupButton implements View.OnClickListener {
        private String group;

        GroupButton(String group) {
            this.group = group;
        }

        @Override
        public void onClick(View view) {
            Intent messages = new Intent(ActivityGroups.this, ActivityMessages.class);
            Bundle msg = new Bundle();

            msg.putString(GROUPVALUE, group);
            messages.putExtras(msg);
            startActivity(messages);
        }
    }
}
