package com.user.login.Service;

import com.user.login.Entity.Auth.AuthRequest;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Entity.User;
import com.user.login.Repository.UserRepository;
import com.user.login.Security.JwtUtils;
import com.user.login.Security.JwtAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    // Authenticate the user and generate a token
    public AuthResponse authenticate(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return AuthResponse.builder().token(token).build();
    }

    // Authenticate using JWT
    public Authentication authenticateWithJwt(String token) {
        if (jwtUtils.isTokenValid(token)) {
            String username = jwtUtils.parseToken(token).getBody().getSubject();  // Fixed here
            List<SimpleGrantedAuthority> authorities = jwtUtils.getRolesFromToken(token).stream()  // Convert roles to authorities
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(username, authorities, token);
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
            return jwtAuthenticationToken;
        }
        throw new RuntimeException("Invalid or expired token");
    }

    // Refresh JWT token
    public AuthResponse refreshToken(String oldToken) {
        if (jwtUtils.isTokenValid(oldToken)) {
            String username = jwtUtils.parseToken(oldToken).getBody().getSubject();  // Fixed here
            String newToken = jwtUtils.generateToken(username);
            return AuthResponse.builder().token(newToken).build();
        }
        throw new RuntimeException("Invalid or expired token");
    }
}
