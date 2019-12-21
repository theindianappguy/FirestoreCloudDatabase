package com.ithendianappguy.firestoreclouddatabase;

import com.google.firebase.firestore.Exclude;

public class Note {
    String title;
    String description;
    private String documentId;
    private int priority;

    public Note(String title,String description, int priority){
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public Note(){
        //public no-arg constructor needed
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
