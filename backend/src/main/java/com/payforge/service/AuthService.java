package com.payforge.service;

import com.payforge.dto.request.LoginRequest;
import com.payforge.dto.request.SignupRequest;
import com.payforge.dto.response.AuthResponse;
import com.payforge.dto.response.SignupResponse;
import com.payforge.entity.User;
import com.payforge.repository.UserRepository;
import com.payforge.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.payforge.security.JwtService;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
    }
    public SignupResponse signup(SignupRequest request){
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
        return new SignupResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getEmail()
        );
    }
    public AuthResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new RuntimeException("Email or Password Invalid"));

        boolean passwordmatches=passwordEncoder.matches(request.getPassword(),user.getPassword());
        if(!passwordmatches){
            throw  new RuntimeException("Invalid Password or Email");
        }
        String token=jwtService.generateToken(user);
        return  new AuthResponse("Login Successful", user.getId(), user.getEmail(),token);
    }

}