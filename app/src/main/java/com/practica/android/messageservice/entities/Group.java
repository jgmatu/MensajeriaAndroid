package com.practica.android.messageservice.entities;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class Group {
    // UID, Messages...
    private HashMap<String, Messages> group;

    Group() {
        group = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuffer format = new StringBuffer();

        for (String key : group.keySet()) {
            format.append(String.format("Key : %s\n", key));
        }
        return format.toString();
    }
}
