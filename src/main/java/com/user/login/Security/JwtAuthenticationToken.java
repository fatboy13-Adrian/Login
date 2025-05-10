package com.user.login.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;  // Typically the username (subject of the token)
    private final String credentials;  // The JWT token itself

    // Constructor with username, authorities (roles), and token
    public JwtAuthenticationToken(String principal, List<SimpleGrantedAuthority> authorities, String credentials) {
        super(authorities);  // Pass the authorities (roles) to the superclass
        this.principal = principal;  // Set the principal (username)
        this.credentials = credentials;  // Set the credentials (JWT token)
        setAuthenticated(true);  // Mark as authenticated
    }

    @Override
    public Object getCredentials() {
        return credentials;  // Return the JWT token
    }

    @Override
    public Object getPrincipal() {
        return principal;  // Return the principal (username)
    }
}
