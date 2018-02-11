package com.practica.android.messageservice.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String email;
    private String uid;

    User() {
        email = "";
    }

    public User (String email, String uid) {
        this.email = email;
        this.uid = uid;
    }
}
