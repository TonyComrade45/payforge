package com.payforge.controller;

import com.payforge.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {

        return (User) authentication.getPrincipal();
    }
}