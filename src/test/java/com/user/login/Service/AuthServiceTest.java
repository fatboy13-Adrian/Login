package com.user.login.Service;
import com.user.login.Enum.Role;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Entity.Auth.AuthRequest;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Entity.User;
import com.user.login.Repository.UserRepository;
import com.user.login.Security.JwtAuthenticationToken;
import com.user.login.Security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest("testUser", "password");
        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setRole(Role.CUSTOMER);
    }

    @Test
    void authenticate_ValidCredentials_ReturnsAuthResponseDTO() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testUser", List.of("CUSTOMER"))).thenReturn("mockedToken");

        AuthResponseDTO response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("mockedToken", response.getToken());
        assertEquals("Authentication successful", response.getMessage());
        assertEquals("Welcome, testUser! Your role is: CUSTOMER", response.getRoleMessage());
    }

    @Test
    void authenticate_UserNotFound_ThrowsRuntimeException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsRuntimeException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void authenticateWithJwt_ValidToken_ReturnsAuthentication() {
        String token = "valid.jwt.token";
        when(jwtUtils.isTokenValid(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("testUser");
        when(jwtUtils.getRolesFromToken(token)).thenReturn(List.of("USER"));

        Authentication authentication = authService.authenticateWithJwt(token);

        assertNotNull(authentication);
        assertEquals("testUser", authentication.getName());
        assertEquals(token, ((JwtAuthenticationToken) authentication).getToken());
    }

    @Test
    void authenticateWithJwt_InvalidToken_ThrowsRuntimeException() {
        String token = "invalid.jwt.token";
        when(jwtUtils.isTokenValid(token)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticateWithJwt(token));
        assertEquals("Invalid or expired token", exception.getMessage());
    }

    @Test
    void refreshToken_ValidToken_ReturnsNewToken() {
        String oldToken = "old.jwt.token";
        when(jwtUtils.isTokenValid(oldToken)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(oldToken)).thenReturn("testUser");
        when(jwtUtils.getRolesFromToken(oldToken)).thenReturn(List.of("USER"));
        when(jwtUtils.generateToken("testUser", List.of("USER"))).thenReturn("new.jwt.token");

        AuthResponse response = authService.refreshToken(oldToken);

        assertNotNull(response);
        assertEquals("new.jwt.token", response.getToken());
    }

    @Test
    void refreshToken_InvalidToken_ThrowsRuntimeException() {
        String oldToken = "expired.jwt.token";
        when(jwtUtils.isTokenValid(oldToken)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(oldToken));
        assertEquals("Invalid or expired token", exception.getMessage());
    }
}
