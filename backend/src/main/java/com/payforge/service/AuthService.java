package com.payforge.service;

import com.payforge.dto.request.LoginRequest;
import com.payforge.dto.request.SignupRequest;
import com.payforge.dto.response.AuthResponse;
import com.payforge.dto.response.SignupResponse;
import com.payforge.entity.Role;
import com.payforge.entity.User;
import com.payforge.entity.Wallet;
import com.payforge.exception.BadRequestException;
import com.payforge.repository.UserRepository;
import com.payforge.repository.WalletRepository;
import com.payforge.security.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.payforge.security.JwtService;

import java.math.BigDecimal;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletRepository walletRepository;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService=jwtService;
        this.walletRepository = walletRepository;
    }
    public SignupResponse signup(SignupRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("Email has already registered");
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(Role.CUSTOMER);

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        // 5. Save user to database
        User savedUser = userRepository.save(user);
        //Wallet creation
        Wallet wallet=new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("INR");
        wallet.setActive(true);

        walletRepository.save(wallet);
        // 6. Return response
        return new SignupResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getEmail()
        );
    }
    public AuthResponse login(LoginRequest request){
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new BadRequestException("Email or Password Invalid"));

        boolean passwordmatches=passwordEncoder.matches(request.getPassword(),user.getPassword());
        if(!passwordmatches){
            throw  new BadRequestException("Invalid Password or Email");
        }
        String token=jwtService.generateToken(user);
        return  new AuthResponse("Login Successful", user.getId(), user.getEmail(),token);
    }

}