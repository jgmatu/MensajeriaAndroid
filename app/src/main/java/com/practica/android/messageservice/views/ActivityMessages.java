package com.practica.android.messageservice.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.practica.android.messageservice.tasks.ManagementMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ActivityMessages extends AppCompatActivity {

    public static final String PATHMESSAGES = "/messages/";
    public static final float SIZETEXT = 24;

    public static final int ACTION_REQUEST_GALLERY = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;

    private static final String TAG = ActivityMessages.class.getSimpleName();

    private List<Message> messages;
    private String group;
    private User user;
    private ManagementMessages managementMessages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        this.group = bundle.getString(ActivityGroups.GROUPVALUE);
        this.user = new User(bundle.getString(ActivityLogin.EMAIL), bundle.getString(ActivityLogin.UID));
        this.messages = new ArrayList<>();
        this.managementMessages = new ManagementMessages(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setButtonWriteMessage();
        setButtonCaptureImage();
        setViewMessagesGroup();
    }

    private void setButtonCaptureImage() {
        FloatingActionButton fab = findViewById(R.id.capture_image);

        fab.setOnClickListener(new ButtonCamera());
    }

    private void setButtonWriteMessage() {
        FloatingActionButton fab = findViewById(R.id.write_message);

        fab.setOnClickListener(new ButtonMessage());
    }

    private void setViewMessagesGroup() {
        final String path = ActivityGroups.PATHGROUPS + this.group + PATHMESSAGES;

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference(path);
        ValueEventListener messagesListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Message> messages = new HashMap<>();

                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (!isExistMessage(message)) {
                        addMessage(message);
                        messages.put(messageSnapshot.getKey(), message);
                    }
                }
                SortedSet<Message> sorted =  new TreeSet<>(messages.values());
                createAllBasicViewTable(sorted);
                managementMessages.setAllViewsAllMessagesSorted(sorted);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupRef.addValueEventListener(messagesListener);
    }

    private void addMessage(Message message) {
        this.messages.add(message);
    }

    private boolean isExistMessage(Message message) {
        return this.messages.contains(message);
    }

    private void createAllBasicViewTable(SortedSet<Message> sorted) {
        TableLayout table = findViewById(R.id.table_messages);

        for (Message message: sorted) {
            setTableTextMessage(table, message);
            if (!message.getUrl().equals("")) {
                setTableImage(table, message);
            }
        }
    }


    private void setTableImage(TableLayout table, Message message) {
        TableRow row = getRow();

        row.addView(getImageMessage(message));
        table.addView(row);
    }

    private ImageView getImageMessage(Message message) {
        ImageView imageView = new ImageView(this);

        imageView.setLayoutParams(new TableRow.LayoutParams(512, 512));
        imageView.setTag(message.getKey());
        return imageView;
    }

    private void setTableTextMessage(TableLayout table, Message message) {
        TableRow row = getRow();

        row.addView(getTextMessage(message));
        table.addView(row);
    }

    private TableRow getRow() {
        TableRow row = new TableRow(this);
        TableLayout.LayoutParams rows = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);

        row.setLayoutParams(rows);
        return row;
    }

    private TextView getTextMessage(com.practica.android.messageservice.entities.Message message) {
        TextView msg = new TextView(this);
        TableRow.LayoutParams buttons = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 1);

        msg.setLayoutParams(buttons);
        msg.setTextSize(ActivityMessages.SIZETEXT);
        msg.setText(message.toString());
        return msg;
    }


    private class ButtonMessage implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            setViewComment();
        }
        
        private void setViewComment() {
            LinearLayout editText = findViewById(R.id.edit_message);

            if (editText.getVisibility() == View.VISIBLE) {
                sendMessageFirebase();
                setHideComment(editText);
            } else {
                setVisibleComment(editText);
            }
        }

        private void setVisibleComment(LinearLayout editMessage) {
            EditText editText = findViewById(R.id.edit_text);

            editMessage.setVisibility(View.VISIBLE);
            setKeyboardAutomatic(editText);
        }

        private void setHideComment(LinearLayout editMessage) {
            EditText editText = findViewById(R.id.edit_text);
            ImageView imageView = findViewById(R.id.view_image);

            editMessage.setVisibility(View.INVISIBLE);
            hideKeyboardAutomatic(editText);

            imageView.setImageResource(0);
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(null);
            editText.setText("");
            imageView.setDrawingCacheEnabled(false);
        }

        private void sendMessageFirebase() {
            EditText editText = findViewById(R.id.edit_text);
            ImageView imageView = findViewById(R.id.view_image);

            String text = editText.getText().toString();

            if (imageView.isDrawingCacheEnabled()) {
                imageView.buildDrawingCache();
            }
            Message message = new Message(ActivityMessages.this.user.getEmail(), text);
            managementMessages.sendMessageAndImage(message, imageView.getDrawingCache(), ActivityMessages.this.group);
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
    }

    private class ButtonCamera implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            DialogCaptureImage dialogCaptureImage = new DialogCaptureImage(ActivityMessages.this);
            dialogCaptureImage.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == REQUEST_IMAGE_CAPTURE || requestCode == ACTION_REQUEST_GALLERY)) {
            Bundle extras = data.getExtras();

            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                ImageView image = findViewById(R.id.view_image);

                image.setDrawingCacheEnabled(true);
                image.setImageBitmap(imageBitmap);
            }
        }
    }
}
