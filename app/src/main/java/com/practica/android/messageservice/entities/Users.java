package com.practica.android.messageservice.entities;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Users {
    private HashMap<String, User> users;

    Users() {
        this.users = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuffer format = new StringBuffer();

        for (String uid : users.keySet()) {
            format.append(String.format("UID : %s\n", uid));
        }
        return format.toString();
    }

}
