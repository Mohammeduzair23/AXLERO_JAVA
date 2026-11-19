package com.axlero.dashboardbuilder.services;

import com.axlero.dashboardbuilder.dto.request.LoginRequest;
import com.axlero.dashboardbuilder.dto.request.RegisterRequest;
import com.axlero.dashboardbuilder.dto.response.AuthResponse;
import com.axlero.dashboardbuilder.dto.response.UserResponse;
import com.axlero.dashboardbuilder.entities.User;
import com.axlero.dashboardbuilder.exceptions.DuplicateResourceException;
import com.axlero.dashboardbuilder.exceptions.ResourceNotFoundException;
import com.axlero.dashboardbuilder.repositories.UserRepository;
import com.axlero.dashboardbuilder.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/** Registration, login and "who am I" use cases. Never logs emails or passwords. */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration rejected: email already registered");
            throw new DuplicateResourceException("Email already registered");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        userRepository.save(user);
        log.info("User registered: id={}", user.getId());
        return new AuthResponse(jwtService.generateToken(email), toResponse(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        // Throws BadCredentialsException on a wrong email or password (mapped to 401 by GlobalExceptionHandler).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = findByEmail(email);
        log.debug("User logged in: id={}", user.getId());
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