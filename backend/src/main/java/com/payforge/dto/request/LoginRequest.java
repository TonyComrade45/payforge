package com.payforge.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public  class LoginRequest {
    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Format")
    private String Email;
    @NotBlank(message = "Password is required")
    private String password;
}