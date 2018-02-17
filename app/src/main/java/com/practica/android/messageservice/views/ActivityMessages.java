package com.practica.android.messageservice.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.practica.android.messageservice.R;
import com.practica.android.messageservice.entities.Message;
import com.practica.android.messageservice.entities.User;
import com.practica.android.messageservice.tasks.ManagementMessages;

import java.util.ArrayList;
import java.util.List;

public class ActivityMessages extends AppCompatActivity {

    public static final String PATHMESSAGES = "/messages/";
    public static final float SIZETEXT = 24;

    public static final int ACTION_REQUEST_GALLERY = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;

    private static final String TAG = ActivityMessages.class.getSimpleName();
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();

    private List<Message> messages;
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
        this.messages = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setButtonWriteMessage();
        setButtonCaptureImage();
        setViewMessagesGroup();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public boolean isExistMessage(Message message) {
        return this.messages.contains(message);
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

        DatabaseReference groupRef = database.getReference(path);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> messages = new ArrayList<>();

                for (DataSnapshot msgSnapshot: dataSnapshot.getChildren()) {
                    String keyMsg = msgSnapshot.getKey();
                    messages.add(keyMsg);
                }
                new ManagementMessages(ActivityMessages.this, path)
                        .setViewAllMessagesSorted(messages);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        groupRef.addValueEventListener(postListener);
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
            message.write(database, storage, imageView.getDrawingCache(), ActivityMessages.this.group);
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
            new DialogCaptureImage(ActivityMessages.this).show();
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
