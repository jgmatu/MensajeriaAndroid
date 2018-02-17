package com.practica.android.messageservice.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.entities.Group;
import com.practica.android.messageservice.entities.User;

import java.util.ArrayList;
import java.util.List;

public class ActivityGroups extends AppCompatActivity {

    public static final String GROUPVALUE = "group";
    public static final String PATHGROUPS = "/MessageService/Groups/";

    private static final String TAG = ActivityGroups.class.getSimpleName();

    private static final int MAXUSERS = 6;
    private static final float SIZETEXT = 24;
    private static final int HIGHBUTTONS = 240;
    private static final int MARGINROWS = 25;

    private User user;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Set view toolbar...
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle loginInfo = getIntent().getExtras();
        if (loginInfo != null) {
            String uid = loginInfo.getString(ActivityLogin.UID);
            String email = loginInfo.getString(ActivityLogin.EMAIL);
            this.user = new User(email, uid);
            queryFirebaseGroups();
        }
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
            DialogGroups dialogGroups = new DialogGroups(this, this.user, this.database);
            dialogGroups.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void queryFirebaseGroups() {
        final DatabaseReference groupsRef = database.getReference(PATHGROUPS);

        ValueEventListener groupsListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Group> groups = new ArrayList<>();

                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    groups.add(groupSnapshot.getValue(Group.class));
                }
                setViewGroups(groups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ;
            }
        };
        groupsRef.addValueEventListener(groupsListener);
    }

    private void setViewGroups(List<Group> groups) {
        TableLayout tableLayout = findViewById(R.id.table_groups);
        TableLayout.LayoutParams rows = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, 0,
                1.0f / (float) groups.size());

        rows.setMargins(MARGINROWS, MARGINROWS, MARGINROWS, MARGINROWS);
        tableLayout.removeAllViews();
        for (Group group : groups) {
            TableRow row = new TableRow(this);

            row.setLayoutParams(rows);
            row.addView(getGroupButton(group));
            row.addView(getRegisterButton(group));
            tableLayout.addView(row);
        }
    }

    private Button getGroupButton(Group group) {
        Button button = new Button(this);
        TableRow.LayoutParams paramsButtonGroup = new TableRow.LayoutParams(
                0, HIGHBUTTONS, 0.67f);

        button.setLayoutParams(paramsButtonGroup);
        button.setText(group.getName());
        button.setTextSize(SIZETEXT);
        button.setOnClickListener(new GroupButton(group));
        return button;
    }

    private ImageButton getRegisterButton(Group group) {
        ImageButton button = new ImageButton(this);
        TableRow.LayoutParams paramsButtonRegister = new TableRow.LayoutParams(0, HIGHBUTTONS, 0.33f);

        button.setLayoutParams(paramsButtonRegister);
        button.setOnClickListener(new GroupAction(group));

        if (group.isRegister(this.user.getUid())) {
            button.setImageDrawable(getResources().getDrawable(R.mipmap.exit_group));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.mipmap.register_user_group));
        }
        return button;
    }

    private class GroupButton implements View.OnClickListener {
        private Group group;

        GroupButton(Group group) {
            this.group = group;
        }

        @Override
        public void onClick(View view) {
            if (!this.group.isRegister(ActivityGroups.this.user.getUid())) {
                showMsgInfo("The user is not Register on Group!");
                return;
            }

            Intent messages = new Intent(ActivityGroups.this, ActivityMessages.class);
            Bundle msg = new Bundle();

            msg.putString(GROUPVALUE, this.group.getName());
            msg.putString(ActivityLogin.UID, ActivityGroups.this.user.getUid());
            msg.putString(ActivityLogin.EMAIL, ActivityGroups.this.user.getEmail());
            messages.putExtras(msg);
            startActivity(messages);
        }
    }

    private class GroupAction implements View.OnClickListener {

        private Group group;

        GroupAction(Group group) {
            this.group = group;
        }

        @Override
        public void onClick(View view) {
            if (this.group.isRegister(ActivityGroups.this.user.getUid())) {
                this.group.exit(ActivityGroups.this.database, ActivityGroups.this.user);
                return;
            }

            if (this.group.size() < MAXUSERS) {
                this.group.insert(ActivityGroups.this.database, ActivityGroups.this.user);
            } else {
                showMsgInfo("Max users on the Group!");
            }
        }
    }

    private void showMsgInfo(String text) {
        int time = Toast.LENGTH_SHORT;

        Toast msg = Toast.makeText(this, text, time);
        msg.show();
    }
}
