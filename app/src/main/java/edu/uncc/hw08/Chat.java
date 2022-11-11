package edu.uncc.hw08;

import java.sql.Timestamp;

public class Chat {
    public String message;
    public Timestamp createdAt;


    public Chat(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}