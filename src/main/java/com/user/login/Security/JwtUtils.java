package com.user.login.Security;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;  // Import Key class
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final SecretKey secretKey = new SecretKeySpec("your_secret_key".getBytes(), SignatureAlgorithm.HS256.getJcaName());
    private final ObjectMapper objectMapper = new ObjectMapper();  // Jackson ObjectMapper

    // Generate a token for a given username
    public String generateToken(String username) {
        try {
            // Define the key (it's important to use a secure secret key of sufficient length)
            String secretKey = "your-very-secure-secret-key-that-should-be-32-characters-long!";
            Key key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());

            // Generate JWT token with user details
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiry
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.out.println("Token generation failed: " + e.getMessage()); // Debug
            return null;
        }
    }

    // Parse and validate the token
    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder() // Use parserBuilder() instead of deprecated parser()
                .setSigningKey(secretKey) // Use SecretKey object for signing
                .build()
                .parseClaimsJws(token);
    }

    // Check if the token is valid
    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claimsJws = parseToken(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // Invalid token
        }
    }

    // Extract roles from the token using ObjectMapper to deserialize safely
    public List<String> getRolesFromToken(String token) {
        Jws<Claims> claimsJws = parseToken(token);
        try {
            // Safely deserialize the roles from the token's claims using ObjectMapper
            return objectMapper.readValue(claimsJws.getBody().get("roles", String.class), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error extracting roles from token", e);
        }
    }
}
