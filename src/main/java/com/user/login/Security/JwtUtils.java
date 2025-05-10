package com.user.login.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expirationMillis = 1000 * 60 * 60; // 1 hour

    // ✅ Generate token with username and roles
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(secretKey)
                .compact();
    }

    // ✅ Parse token to get claims
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Validate token expiration
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Get username from token
    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    // ✅ Get roles from token
    public List<String> getRolesFromToken(String token) {
    Claims claims = parseToken(token);
    Object rolesObject = claims.get("roles");
    if (rolesObject instanceof List<?>) {
        return ((List<?>) rolesObject).stream()
                .map(Object::toString)  // ensure conversion to String
                .toList();
    }
    throw new RuntimeException("Roles claim is missing or invalid");
}
}
