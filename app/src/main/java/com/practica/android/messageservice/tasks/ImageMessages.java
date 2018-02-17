package com.practica.android.messageservice.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

class ImageMessages {
    private HashMap<String, ImageMessage> messages;

    ImageMessages() {
        this.messages = new HashMap<>();
    }

    Iterator<ImageMessage> getValues() {
        return messages.values().iterator();
    }

    ImageMessage get (String key) {
        return messages.get(key);
    }

    int size() {
        return messages.size();
    }

    void put (String key, ImageMessage imageMessage) {
        messages.put(key, imageMessage);
    }

    int getNumMessagesImage() {
        int total = 0;

        for (ImageMessage imageMessage : messages.values()) {
            if (!imageMessage.getUrl().equals("")) {
                total++;
            }
        }
        return total;
    }

    HashMap<String, ImageMessage> getMap() {
        return messages;
    }

    SortedSet<ImageMessage> getSorted() {
        return new TreeSet<>(messages.values());
    }
}
