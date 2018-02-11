package com.practica.android.messageservice.entities;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.practica.android.messageservice.views.ActivityGroups;
import com.practica.android.messageservice.views.ActivityMessages;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@IgnoreExtraProperties
public class Message {
    private String text;
    private String email;
    private long time;

    Message() {
        ;
    }

    public Message (String email, String text) {
        this.text = text;
        this.time = Calendar.getInstance().getTimeInMillis();
        this.email = email;
    }

    public void writeMessage(FirebaseDatabase database, String group) {
        DatabaseReference messagesGroupRef = database.getReference(ActivityGroups.PATHGROUPS + group + ActivityMessages.PATHMESSAGES);

        messagesGroupRef.push().setValue(this);
    }

    @Override
    public String toString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        return String.format("%s: %s %02d:%02d", this.text, this.email, hour, min);
    }
}
