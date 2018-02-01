package com.example.javi.messageservice.entities;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Groups {
    // Name, Group
    private HashMap<String, Group> groups;

    Groups() {
        groups = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuffer format = new StringBuffer("*** : ");

        for (String key : groups.keySet()) {
            format.append(String.format("Key : %s\n", key));
        }
        return format.toString();
    }
}
