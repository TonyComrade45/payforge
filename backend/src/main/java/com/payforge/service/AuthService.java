package com.payforge.service;

import com.payforge.dto.request.SignupRequest;
import com.payforge.dto.response.AuthResponse;
import com.payforge.entity.User;
import com.payforge.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public AuthResponse signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email has already registered");
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        // 5. Save user to database
        User savedUser = userRepository.save(user);

        // 6. Return response
        return new AuthResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getEmail()
        );
    }

}