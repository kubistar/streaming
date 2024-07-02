package org.sparta.streaming.video.dto;

public class ResponseMessage {
    private String message;
    private Object data;
    private int statusCode;

    public ResponseMessage(String message, Object data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    public ResponseMessage(String message, Object data) {
        this.message = message;
        this.data = data;
        this.statusCode = 200; // Default to HTTP 200 OK
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
