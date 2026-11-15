package com.axlero.dashboardbuilder.auth.service;

import com.axlero.dashboardbuilder.auth.dto.AuthResponse;
import com.axlero.dashboardbuilder.auth.dto.LoginRequest;
import com.axlero.dashboardbuilder.auth.dto.RegisterRequest;
import com.axlero.dashboardbuilder.auth.dto.UserResponse;
import com.axlero.dashboardbuilder.common.exception.DuplicateResourceException;
import com.axlero.dashboardbuilder.common.exception.ResourceNotFoundException;
import com.axlero.dashboardbuilder.security.JwtService;
import com.axlero.dashboardbuilder.user.entity.User;
import com.axlero.dashboardbuilder.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(email), toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        // Throws BadCredentialsException on a wrong email or password (mapped to 401).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = findByEmail(email);
        return new AuthResponse(jwtService.generateToken(email), toResponse(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        return toResponse(findByEmail(email));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}