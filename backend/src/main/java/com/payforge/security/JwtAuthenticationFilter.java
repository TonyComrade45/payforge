package com.payforge.security;

import com.payforge.entity.User;
import com.payforge.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository) {

        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Get the requested API path
        String path = request.getServletPath();

        // Signup and login are public endpoints.
        // They do not require JWT authentication.
        if (path.equals("/api/auth/signup") ||
                path.equals("/api/auth/login")) {

            filterChain.doFilter(request, response);
            return;
        }

        // Read Authorization header
        String authHeader = request.getHeader("Authorization");

        // No JWT provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Remove "Bearer " from the token
        String token = authHeader.substring(7);

        // Validate JWT
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract user ID from JWT
        String userId = jwtService.extractUserId(token);

        // Find user in database
        User user = userRepository
                .findById(Long.parseLong(userId))
                .orElse(null);

        if (user != null) {

            // Create authenticated user
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

            // Store authentication in SecurityContext
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        // Continue request
        filterChain.doFilter(request, response);
    }
}