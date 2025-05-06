package com.user.login.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;  // The username of the authenticated user
    private final String jwtToken;   // The JWT token (may not be used directly in most cases)

    // Constructor for unauthenticated JwtAuthenticationToken (used when receiving the token)
    public JwtAuthenticationToken(String username) {
        super(null);  // No authorities yet
        this.principal = username;
        this.jwtToken = null;
        setAuthenticated(false);  // Not authenticated yet
    }

    // Constructor for authenticated JwtAuthenticationToken (used after token validation)
    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, String jwtToken) {
        super(authorities);  // Authorities should be set for authenticated user
        this.principal = principal;
        this.jwtToken = jwtToken;
        setAuthenticated(true);  // Mark as authenticated
    }

    @Override
    public Object getCredentials() {
        return jwtToken;  // Return JWT if necessary (e.g., for logging or other purposes)
    }

    @Override
    public Object getPrincipal() {
        return principal;  // Return username (principal) as the authenticated user
    }
}
