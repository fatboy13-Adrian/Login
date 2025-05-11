package com.user.login.Service;                                             //Package declaration
import com.user.login.Entity.Auth.AuthRequest;                              //AuthRequest entity for user authentication input
import com.user.login.Entity.Auth.AuthResponse;                             //AuthResponse entity for response after authentication
import com.user.login.DTO.Auth.AuthResponseDTO;                             //DTO for formatted response after authentication
import com.user.login.Entity.User;                                          //User entity for user data
import com.user.login.Repository.UserRepository;                            //User repository for querying user data
import com.user.login.Security.JWT.JwtAuthenticationToken;                  //Custom authentication token for JWT authentication
import com.user.login.Security.JWT.JwtUtils;                                //Utility class for JWT token generation and validation
import org.springframework.beans.factory.annotation.Autowired;              //For dependency injection
import org.springframework.security.core.Authentication;                    //Authentication interface for user details
import org.springframework.security.core.authority.SimpleGrantedAuthority;  //Simple authority object for roles
import org.springframework.security.core.context.SecurityContextHolder;     //Holds authentication details
import org.springframework.security.crypto.password.PasswordEncoder;        //For encoding and matching passwords
import org.springframework.stereotype.Service;                              //Marks the class as a service
import java.util.List;                                                      //For handling lists of roles
import java.util.stream.Collectors;                                         //For collecting stream elements into a list

@Service    //Marks the class as a service, so Spring can manage it
public class AuthService 
{
    private final UserRepository userRepository;    //User repository for interacting with the user database
    private final JwtUtils jwtUtils;                //Utility for working with JWT tokens
    private final PasswordEncoder passwordEncoder;  //Password encoder for securely handling passwords

    @Autowired  //Constructor-based dependency injection for necessary services
    public AuthService(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) 
    {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    //Authenticate the user and generate token including roles
    public AuthResponseDTO authenticate(AuthRequest authRequest) 
    {
        //Retrieve user from database based on username
        User user = userRepository.findByUsername(authRequest.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        //Validate if the provided password matches the user's stored password
        if(!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) 
            throw new RuntimeException("Invalid credentials");              //Throw error if password doesn't match

        //Generate JWT token using the username and role(s)
        String token = jwtUtils.generateToken(user.getUsername(), List.of(user.getRole().name()));

        //Create a custom welcome message with the user's role
        String welcomeMessage = "Welcome, " + user.getUsername() + "! Your role is: " + user.getRole().name();

        //Return AuthResponseDTO containing the token, success message, and role-based welcome message
        return AuthResponseDTO.builder().token(token).message("Authentication successful")
        .roleMessage(welcomeMessage).build();
    }

    //Authenticate using JWT token
    public Authentication authenticateWithJwt(String token) 
    {
        //Check if the provided JWT token is valid
        if(jwtUtils.isTokenValid(token)) 
        {
            String username = jwtUtils.getUsernameFromToken(token); //Extract the username from the token

            //Extract the roles from the token and convert them to authorities
            List<SimpleGrantedAuthority> authorities = jwtUtils.getRolesFromToken(token).stream().map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

            //Create a custom JwtAuthenticationToken with the extracted information
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(username, authorities, token);

            //Set the created authentication token into the security context
            SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);

            return jwtAuthenticationToken;  //Return the authentication token
        }
        throw new RuntimeException("Invalid or expired token"); //Throw error if token is invalid or expired
    }

    //Refresh JWT token
    public AuthResponse refreshToken(String oldToken) 
    {
        //Check if the provided old JWT token is valid
        if(jwtUtils.isTokenValid(oldToken)) 
        {
            
            String username = jwtUtils.getUsernameFromToken(oldToken);      //Extract username and roles from the old token
            List<String> roles = jwtUtils.getRolesFromToken(oldToken);
            String newToken = jwtUtils.generateToken(username, roles);      //Generate a new token with the same username and roles
            return AuthResponse.builder().token(newToken).build();          //Return the new token inside an AuthResponse
        }

        throw new RuntimeException("Invalid or expired token");     //Throw error if token is invalid or expired
    }
}