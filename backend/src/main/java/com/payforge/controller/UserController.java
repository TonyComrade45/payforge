package com.payforge.controller;

import com.payforge.dto.request.UpdateProfileRequest;
import com.payforge.dto.response.UserProfileResponse;
import com.payforge.entity.User;
import com.payforge.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {

        return (User) authentication.getPrincipal();
    }
    @GetMapping("/profile")
    public UserProfileResponse getProfile(
            @AuthenticationPrincipal User user) {

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    @PutMapping("/profile")
    public UserProfileResponse updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateProfileRequest request) {

        user.setName(request.getName());

        userRepository.save(user);

        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}