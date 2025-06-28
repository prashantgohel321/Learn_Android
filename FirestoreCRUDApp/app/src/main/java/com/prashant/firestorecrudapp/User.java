package com.prashant.firestorecrudapp;


import com.google.firebase.firestore.Exclude;

public class User {
    private String id; // Document ID from Firestore
    private String name;
    private String email;

    // Required public no-argument constructor for Firestore
    public User() {
    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    // @Exclude annotation prevents this field from being directly stored in Firestore
    // as it's the document ID, not a field within the document.
    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

