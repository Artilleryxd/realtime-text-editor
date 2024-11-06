package com.example.realtimetextproject;

public class User {
    private String name;
    private String email;
    private boolean presence;
    private String activeDoc;

    // Empty constructor required for Firestore
    public User() {
    }

    // Constructor with parameters
    public User(String name, String email, boolean presence, String activeDoc) {
        this.name = name;
        this.email = email;
        this.presence = presence;
        this.activeDoc = activeDoc;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPresence() {
        return presence;
    }

    public void setPresence(boolean presence) {
        this.presence = presence;
    }

    public String getActiveDoc() {
        return activeDoc;
    }

    public void setActiveDoc(String activeDoc) {
        this.activeDoc = activeDoc;
    }
}
