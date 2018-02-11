package com.practica.android.messageservice.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.entities.Message;
import com.practica.android.messageservice.entities.User;

import java.util.ArrayList;
import java.util.List;


public class ActivityMessages extends AppCompatActivity {
    public static final String PATHMESSAGES = "/messages/";

    private static final String TAG = ActivityMessages.class.getSimpleName();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final float SIZETEXT = 24;

    private TableLayout tableMessages;
    private String group;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__messages);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        this.group = bundle.getString(ActivityGroups.GROUPVALUE);
        this.user = new User(bundle.getString(ActivityLogin.EMAIL), bundle.getString(ActivityLogin.UID));
        this.tableMessages = findViewById(R.id.table_messages);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setButtonWriteMessage();
        setViewGroup();
    }

    private void setButtonWriteMessage() {
        FloatingActionButton fab = findViewById(R.id.write_message);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.edit_message);

                if (editText.getVisibility() == View.VISIBLE) {
                    editText.setVisibility(View.INVISIBLE);
                    hideKeyboardAutomatic(editText);

                    sendMessageFirebase(editText.getText().toString());
                    editText.setText("");
                } else {
                    editText.setVisibility(View.VISIBLE);
                    setKeyboardAutomatic(editText);
                }
            }
        });
    }

    private void sendMessageFirebase(String text) {
        Message message = new Message(this.user.getEmail(), text);
        message.writeMessage(database, this.group);
    }

    private void setKeyboardAutomatic(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboardAutomatic(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    private void setViewGroup() {
        final String path = ActivityGroups.PATHGROUPS + this.group + PATHMESSAGES;

        DatabaseReference groupRef = database.getReference(path);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> messages = new ArrayList<>();

                tableMessages.removeAllViews();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    messages.add(postSnapshot.getKey());
                }
                setViewMessages(messages, path);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupRef.addValueEventListener(postListener);
    }

    private void setViewMessages(List<String> messages, String path) {
        for (String message : messages) {
            queryFirebaseMessage(path + message);
        }
    }

    private void queryFirebaseMessage(final String messagePath) {
        DatabaseReference msgRef = database.getReference(messagePath);

        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);

                if (message != null) {
                    setViewMessage(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        msgRef.addValueEventListener(messageListener);
    }

    private void setViewMessage (Message message) {
        TableRow row = new TableRow(this);
        TableLayout.LayoutParams rows = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        row.setLayoutParams(rows);
        row.addView(getTextMessage(message));
        this.tableMessages.addView(row);
    }

    private TextView getTextMessage(Message message) {
        TextView msg = new TextView(this);
        TableRow.LayoutParams buttons = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);

        msg.setLayoutParams(buttons);
        msg.setTextSize(SIZETEXT);
        msg.setText(message.toString());
        return msg;
    }
}
