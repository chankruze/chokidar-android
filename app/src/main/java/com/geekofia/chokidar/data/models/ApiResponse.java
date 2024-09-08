package com.geekofia.chokidar.data.models;

public class ApiResponse {
    private boolean ok;
    private String message;

    // Constructors
    public ApiResponse(boolean ok, String message, String token, String userId) {
        this.ok = ok;
        this.message = message;
    }

    // Getters and Setters
    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
