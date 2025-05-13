package com.user.login.Controller;                              //Declares the package that this class belongs to, used for grouping related classes
import com.user.login.DTO.Auth.AuthRequestDTO;                  //Imports the DTO (Data Transfer Object) used to receive login credentials from the client
import com.user.login.DTO.Auth.AuthResponseDTO;                 //Imports the DTO used to send authentication results (e.g., JWT token) back to the client
import com.user.login.Entity.Auth.AuthRequest;                  //Imports the internal entity representing login request data in the application logic
import com.user.login.Entity.Auth.AuthResponse;                 //Imports the internal entity representing authentication result used internally
import com.user.login.Service.AuthService;                      //Imports the authentication service which handles business logic for auth operations
import org.springframework.beans.factory.annotation.Autowired;  //Imports Spring's annotation to enable automatic dependency injection
import org.springframework.http.HttpStatus;                     //Imports HTTP status codes such as OK (200), UNAUTHORIZED (401), FORBIDDEN (403)
import org.springframework.http.ResponseEntity;                 //Imports the ResponseEntity class used to build complete HTTP responses (body + status code)
import org.springframework.web.bind.annotation.*;               //Imports Spring annotation to define a REST API controller

@CrossOrigin(origins = "http://localhost:3000")                 //Enables CORS (Cross-Origin Resource Sharing) for frontend access (e.g., React app on port 3000)
@RestController                                                 //Marks this class as a REST controller, which handles HTTP requests and returns JSON/XML
@RequestMapping("/auth")                                        //Base path for all endpoints in this controller will be prefixed with "/auth"
public class AuthController 
{
    private final AuthService authService;  //Declares a final reference to the authentication service

    //Constructor-based dependency injection for the AuthService bean
    @Autowired
    public AuthController(AuthService authService) 
    {
        this.authService = authService;
    }

    //HTTP POST endpoint at /auth/login to authenticate a user
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) 
    {
        try 
        {
            //Create a new AuthRequest entity and populate it from the received DTO
            AuthRequest authRequest = new AuthRequest();
            authRequest.setUsername(authRequestDTO.getUsername());
            authRequest.setPassword(authRequestDTO.getPassword());

            //Pass the request entity to the authentication service and receive a response DTO
            AuthResponseDTO authResponseDTO = authService.authenticate(authRequest);

            //Return HTTP 200 OK status with the response body
            return ResponseEntity.ok(authResponseDTO);
        } 
        
        catch(Exception e) 
        {
            //If any exception occurs during authentication, return a failure message and 401 Unauthorized
            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder().token(null).message("Authentication failed").build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponseDTO);
        }
    }

    //HTTP POST endpoint at /auth/refresh to refresh JWT tokens
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody String oldToken) 
    {
        try 
        {
            //Calls the auth service to refresh the token using the old one
            AuthResponse authResponse = authService.refreshToken(oldToken);

            //Build a response DTO from the refreshed token
            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder().token(authResponse.getToken()).message("Token refreshed successfully").build();

            //Return HTTP 200 OK with the new token in response
            return ResponseEntity.ok(authResponseDTO);
        } 
        
        catch(Exception e) 
        {
            //If token refresh fails, return a 403 Forbidden response with appropriate message
            AuthResponseDTO authResponseDTO = AuthResponseDTO.builder().token(null).message("Token refresh failed").build();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponseDTO);
        }
    }

    //HTTP GET endpoint at /auth/protected used to test access to a protected resource
    @GetMapping("/protected")
    public ResponseEntity<String> getProtectedResource() 
    {
        //Returns a simple success message with HTTP 200 OK
        return ResponseEntity.ok("This is a protected resource.");
    }
}