package com.example.realtimetextproject;

public class Document {
    private String name;
    private String createdBy;
    private String content;

    // No-argument constructor required for Firestore deserialization
    public Document() {
    }

    // Constructor with parameters
    public Document(String name, String createdBy, String content) {
        this.name = name;
        this.createdBy = createdBy;
        this.content = content;
    }

    // Getters and setters for Firestore mapping
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


