package com.payforge.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class LoginRequest {
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Format")
    private String Email;
    @NotBlank(message = "Password is required")
    private String password;
}