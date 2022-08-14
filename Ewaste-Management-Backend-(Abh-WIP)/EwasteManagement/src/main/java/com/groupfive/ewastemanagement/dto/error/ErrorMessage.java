package com.groupfive.ewastemanagement.dto.error;

import java.util.Date;

public class ErrorMessage {

    private String title;
    private String message;
    private Date timestamp;

    public ErrorMessage(String title, String message, Date timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ErrorMessage() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}