package com.birthday.invitation.dto.response;

import java.time.OffsetDateTime;

public class ErrorResponse {

    private int status;
    private String message;
    private OffsetDateTime timestamp;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
