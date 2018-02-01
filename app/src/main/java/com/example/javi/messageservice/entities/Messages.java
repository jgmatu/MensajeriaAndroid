package com.example.javi.messageservice.entities;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Messages {
    // Key Name
    private HashMap<String, String> messages;

    Messages() {
        messages = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuffer format = new StringBuffer();

        for (String key : messages.keySet()) {
            format.append(String.format("Key : %s\n", key));
        }
        return format.toString();
    }
}
