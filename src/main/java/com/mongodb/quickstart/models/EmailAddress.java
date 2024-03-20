package com.mongodb.quickstart.models;

import org.bson.codecs.pojo.annotations.BsonCreator;

public class EmailAddress {
    private String email;

    @BsonCreator
    public EmailAddress() {
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailAddress{" +
                "email='" + email + '\'' +
                '}';
    }
}
