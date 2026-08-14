package com.payforge.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {
    private String message;
    private Long userId;
    private String email;
    private String token;

    public AuthResponse(String message, Long userId, String email,String token) {
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.token=token;
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