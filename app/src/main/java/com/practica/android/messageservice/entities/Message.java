package com.practica.android.messageservice.entities;


import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.practica.android.messageservice.views.ActivityGroups;
import com.practica.android.messageservice.views.ActivityMessages;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@IgnoreExtraProperties
public class Message {
    private static final String TAG = Message.class.getSimpleName();
    private String text;
    private String email;
    private long time;
    private String url;

    Message() {
        this.text = "";
        this.email = "";
        this.time = -1;
        this.url = "";
    }

    public Message (String email, String text) {
        this.text = text;
        this.time = Calendar.getInstance().getTimeInMillis();
        this.email = email;
        this.url = "";
    }

    public void write(FirebaseDatabase database, FirebaseStorage storage, Bitmap bitmap, String group) {
        String path = ActivityGroups.PATHGROUPS + group + ActivityMessages.PATHMESSAGES;
        DatabaseReference messagesGroupRef = database.getReference(path);

        // Obtain new key to new message...
        String key = messagesGroupRef.push().getKey();
        DatabaseReference msgRef = database.getReference(path + "/"  + key);

        if (bitmap != null && bitmap.isMutable()) {
            uploadImage(storage, bitmap, key, msgRef);
        } else {
            // The message is only text..
            msgRef.setValue(this);
        }
    }

    private void uploadImage(FirebaseStorage storage, Bitmap bitmap, String keyImage, final DatabaseReference msgRef) {
        StorageReference storageRef = storage.getReference();
        StorageReference imagesReference = storageRef.child(keyImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesReference.putBytes(data);

        // Handle unsuccessful uploads.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if (downloadUrl != null) {
                    Message.this.url = downloadUrl.toString();
                    msgRef.setValue(Message.this);
                }
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message msg = (Message) o;
        return this.url.equals(msg.url) && this.time == msg.time && this.email.equals(msg.email) && this.text.equals(msg.text);
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        if (this.url.equals("")) {
            return String.format("Text Message : %s\n User :%s\n Time: %02d:%02d\n", this.text, this.email, hour, min);
        }
        return String.format("Comment Image: %s\n User :%s\n Time: %02d:%02d\n\nImage\n----------\n", this.text, this.email, hour, min);
    }
}
