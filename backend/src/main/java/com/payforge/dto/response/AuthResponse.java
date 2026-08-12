package com.payforge.dto.response;

public class AuthResponse {
    private String message;
    private Long userId;
    private String email;

    public AuthResponse(String message, Long userId, String email) {
        this.message = message;
        this.userId = userId;
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}