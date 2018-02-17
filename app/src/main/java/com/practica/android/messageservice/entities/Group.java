package com.practica.android.messageservice.entities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.practica.android.messageservice.views.ActivityGroups;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Group {

    private String name;
    private HashMap<String, Message> messages;
    private HashMap<String, User> users;

    Group() {
        this.name = "errorGroup";
        this.users = new HashMap<>();
        this.messages = new HashMap<>();
    }
    
    public Group(String name, User user) {
        this.name = name;
        this.users = new HashMap<>();
        this.messages = new HashMap<>();
        this.users.put(user.getUid(), user);
    }

    public boolean isRegister(String uid) {
        return users.containsKey(uid);
    }

    public int size() {
        return this.users.size();
    }

    public void insert(FirebaseDatabase database, User user) {
        DatabaseReference userRef = database.getReference(ActivityGroups.PATHGROUPS + this.name + "/users/" + user.getUid());

        userRef.setValue(new User(user.getEmail(), user.getUid()));
    }

    public void exit(FirebaseDatabase database, User user) {
        DatabaseReference userRef = database.getReference(ActivityGroups.PATHGROUPS + this.name + "/users/" + user.getUid());

        userRef.setValue(null);
    }

    @Override
    public String toString() {
        StringBuilder format = new StringBuilder();

        for (String uid: users.keySet()) {
            format.append(String.format("UID : %s\n", uid));
        }
        format.append("\n\n");
        for (String keyMsg: messages.keySet()) {
            format.append(String.format("Key ImageMessage : %s\n", keyMsg));
        }
        return format.toString();
    }
}
