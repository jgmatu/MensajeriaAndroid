package com.practica.android.messageservice.tasks;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.practica.android.messageservice.entities.Message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageMessage implements Comparable {
    private Message message;
    private Bitmap bitmap;

    ImageMessage(com.practica.android.messageservice.entities.Message message) {
        this.message = message;
    }

    String getUrl() {
        return message.getUrl();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ImageMessage msg1 = (ImageMessage) o;

        return (int) (this.message.getTime() - msg1.message.getTime());
    }
}
