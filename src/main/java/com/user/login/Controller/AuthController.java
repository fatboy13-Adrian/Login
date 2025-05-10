package com.user.login.Controller;

import com.user.login.DTO.Auth.AuthRequestDTO;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Entity.Auth.AuthRequest;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint for user authentication (login)
    @PostMapping("/login")
public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
    try {
        // Map DTO to Entity
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername(authRequestDTO.getUsername());
        authRequest.setPassword(authRequestDTO.getPassword());

        // Call service method with Entity to get the AuthResponseDTO
        AuthResponseDTO authResponseDTO = authService.authenticate(authRequest);

        return ResponseEntity.ok(authResponseDTO); // Return 200 OK with the response
    } catch (Exception e) {
        // Log the error and send an appropriate response
        AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                .token(null)
                .message("Authentication failed")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDTO);
    }
}

    // Endpoint to refresh the JWT token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody String oldToken) {
        try {
            AuthResponse authResponse = authService.refreshToken(oldToken);
            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                    .token(authResponse.getToken())
                    .message("Token refreshed successfully")
                    .build();
            return ResponseEntity.ok(authResponseDTO);
        } catch (Exception e) {
            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                    .token(null)
                    .message("Token refresh failed")
                    .build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponseDTO);
        }
    }
}
