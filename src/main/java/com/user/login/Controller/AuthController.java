package com.user.login.Controller;
import com.user.login.DTO.Auth.AuthRequestDTO;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Entity.Auth.AuthRequest;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Entity.Auth.RefreshTokenRequest;
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

            // Call service method with Entity
            AuthResponse authResponse = authService.authenticate(authRequest);

            // Map Entity to DTO response
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(authResponse.getToken(), "Authentication successful");
            return ResponseEntity.ok(authResponseDTO); // Return 200 OK with the response
        } catch (Exception e) {
            // Log the error and send an appropriate response
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(null, "Authentication failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDTO);
        }
    }

    // Endpoint to refresh the JWT token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getOldToken());
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(authResponse.getToken(), "Token refreshed successfully");
            return ResponseEntity.ok(authResponseDTO);
        } catch (Exception e) {
            AuthResponseDTO authResponseDTO = new AuthResponseDTO(null, "Token refresh failed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponseDTO);
        }
    }
}
