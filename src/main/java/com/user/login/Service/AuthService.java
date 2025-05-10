package com.user.login.Service;

import com.user.login.Entity.Auth.AuthRequest;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Entity.User;
import com.user.login.Repository.UserRepository;
import com.user.login.Security.JwtAuthenticationToken;
import com.user.login.Security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Authenticate the user and generate token including roles
    public AuthResponseDTO authenticate(AuthRequest authRequest) {
    User user = userRepository.findByUsername(authRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid credentials");
    }

    // Generate the token
    String token = jwtUtils.generateToken(user.getUsername(), List.of(user.getRole().name()));

    // Create a welcome message using the user's role
    String welcomeMessage = "Welcome, " + user.getUsername() + "! Your role is: " + user.getRole().name();

    // Return the AuthResponseDTO object
    return AuthResponseDTO.builder()
            .token(token)
            .message("Authentication successful")
            .roleMessage(welcomeMessage)
            .build();
}

    // ✅ Authenticate using JWT
    public Authentication authenticateWithJwt(String token) {
        if (jwtUtils.isTokenValid(token)) {
            String username = jwtUtils.getUsernameFromToken(token);
            List<SimpleGrantedAuthority> authorities = jwtUtils.getRolesFromToken(token).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(username, authorities, token);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            return jwtAuthenticationToken;
        }
        throw new RuntimeException("Invalid or expired token");
    }

    // ✅ Refresh JWT token
    public AuthResponse refreshToken(String oldToken) {
        if (jwtUtils.isTokenValid(oldToken)) {
            String username = jwtUtils.getUsernameFromToken(oldToken);
            List<String> roles = jwtUtils.getRolesFromToken(oldToken);
            String newToken = jwtUtils.generateToken(username, roles);
            return AuthResponse.builder().token(newToken).build();
        }
        throw new RuntimeException("Invalid or expired token");
    }
}
