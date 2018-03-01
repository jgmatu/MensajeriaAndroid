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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@IgnoreExtraProperties
public class Message implements  Comparable {
    private static final String TAG = Message.class.getSimpleName();
    private String text;
    private String email;
    private long time;
    private String url;
    private String key;

    Message() {
        this.text = "";
        this.email = "";
        this.time = -1;
        this.url = "";
        this.key = "";
    }

    public Message (String email, String text) {
        this.text = text;
        this.time = Calendar.getInstance().getTimeInMillis();
        this.email = email;
        this.url = "";
        this.key = "";
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
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        StringBuilder formatMsg = new StringBuilder();

        if (this.url.equals("")) {
            formatMsg.append(String.format("Text: %s\n ", this.text));
        } else {
            formatMsg.append(String.format("Image: %s\n", this.text));
        }
        formatMsg.append(String.format("User :%s\nTime: %s\n", this.email, format.format(calendar.getTime())));
        return formatMsg.toString();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (o == this) {
            return 0;
        }
        if (!(o instanceof Message)) {
            return -1;
        }
        Message msg = (Message) o;
        return (int) (this.time - msg.time);
    }
}
