package com.user.login.Controller;
import com.user.login.Entity.Auth.AuthResponse;
import com.user.login.Entity.Auth.RefreshTokenRequest;
import com.user.login.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint for user authentication (login)
    // @PostMapping("/login")
    // public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    //     try {
    //         AuthResponse authResponse = authService.authenticate(authRequest);
    //         return ResponseEntity.ok(authResponse); // Return 200 OK with the response
    //     } catch (Exception e) {
    //         // Log the error and send an appropriate response
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                 .body(new AuthResponse("Authentication failed", null));
    //     }
    // }

    // Endpoint to refresh the JWT token
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.getOldToken());
            return ResponseEntity.ok(authResponse); // Return the new token in the response
        } catch (Exception e) {
            // Log the error and return an appropriate response if token refresh fails
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new AuthResponse("Token refresh failed", null));
        }
    }
}
