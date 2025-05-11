package com.user.login.Controller;

import com.user.login.DTO.Auth.AuthRequestDTO;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private AuthRequestDTO validRequestDTO;
    private AuthResponseDTO successResponseDTO;

    @BeforeEach
    void setUp() {
        validRequestDTO = new AuthRequestDTO();
        validRequestDTO.setUsername("user");
        validRequestDTO.setPassword("password");

        successResponseDTO = AuthResponseDTO.builder()
                .token("mock-token")
                .message("Login successful")
                .build();
    }

    // ✅ POSITIVE TEST - login
    @Test
    void testLoginSuccess() {
        when(authService.authenticate(any())).thenReturn(successResponseDTO);

        ResponseEntity<AuthResponseDTO> response = authController.login(validRequestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mock-token", response.getBody().getToken());
        assertEquals("Login successful", response.getBody().getMessage());
    }

    // ❌ NEGATIVE TEST - login fails
    @Test
    void testLoginFailure() {
        when(authService.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<AuthResponseDTO> response = authController.login(validRequestDTO);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody().getToken());
        assertEquals("Authentication failed", response.getBody().getMessage());
    }

    // ✅ POSITIVE TEST - refresh token
    @Test
    void testRefreshTokenSuccess() {
        String oldToken = "oldToken";
        AuthResponse refreshed = new AuthResponse();
        refreshed.setToken("newToken");

        when(authService.refreshToken(oldToken)).thenReturn(refreshed);

        ResponseEntity<AuthResponseDTO> response = authController.refreshToken(oldToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newToken", response.getBody().getToken());
        assertEquals("Token refreshed successfully", response.getBody().getMessage());
    }

    // ❌ NEGATIVE TEST - refresh token fails
    @Test
    void testRefreshTokenFailure() {
        String oldToken = "expiredToken";

        when(authService.refreshToken(oldToken)).thenThrow(new RuntimeException("Token invalid"));

        ResponseEntity<AuthResponseDTO> response = authController.refreshToken(oldToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody().getToken());
        assertEquals("Token refresh failed", response.getBody().getMessage());
    }

    // ✅ Simple test for protected endpoint
    @Test
    void testProtectedResource() {
        ResponseEntity<String> response = authController.getProtectedResource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This is a protected resource.", response.getBody());
    }
}
