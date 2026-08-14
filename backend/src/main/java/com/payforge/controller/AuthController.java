package com.payforge.controller;

import com.payforge.dto.request.LoginRequest;
import com.payforge.dto.request.SignupRequest;
import com.payforge.dto.response.AuthResponse;
import com.payforge.dto.response.SignupResponse;
import com.payforge.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/api/auth")
@RestController()
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request){
        SignupResponse response=authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/login")
    public  ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        AuthResponse response=authService.login(request);
        return  ResponseEntity.ok(response);
    }
}