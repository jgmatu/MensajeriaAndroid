package com.practica.android.messageservice.entities;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@IgnoreExtraProperties
public class Message {
    private String text;
    private String user;
    private long time;

    public Message() {
        ;
    }

    public Message (String text) {
        this.text = text;
        time = Calendar.getInstance().getTimeInMillis();
        this.user = "Jaime";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();

        result.put("text", this.text);
        result.put("time", this.time);
        result.put("user", this.user);
        return result;
    }

    public String getFormatMessage() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.time);
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);

        return String.format("%s: %s %d:%d", this.text, this.user, hour, min);
    }

    @Override
    public String toString() {
        return String.format("Text : %s\n User : %s\n Time : %d\n", this.text, this.user, this.time);
    }
}
