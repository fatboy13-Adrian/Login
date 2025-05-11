package com.user.login.Service;                                         //Define the package location for the test class
import com.user.login.Enum.Role;                                        //Import role enum for assigning user roles
import com.user.login.DTO.Auth.AuthResponseDTO;                         //Import the DTO class for authentication responses
import com.user.login.Entity.Auth.AuthRequest;                          //Import the request entity used for login
import com.user.login.Entity.Auth.AuthResponse;                         //Import the response entity used for token refresh
import com.user.login.Entity.User;                                      //Import the User entity model
import com.user.login.Repository.UserRepository;                        //Import repository interface to mock DB operations
import com.user.login.Security.JWT.JwtAuthenticationToken;              //Import custom JWT authentication token implementation
import com.user.login.Security.JWT.JwtUtils;                            //Import utility class for JWT operations
import org.junit.jupiter.api.BeforeEach;                                //Import JUnit lifecycle method for setup
import org.junit.jupiter.api.Test;                                      //Import JUnit for writing unit tests
import org.junit.jupiter.api.extension.ExtendWith;                      //Import JUnit extension support for Mockito
import org.mockito.InjectMocks;                                         //Import Mockito annotation for injecting mocks
import org.mockito.Mock;                                                //Import Mockito annotation for mocking dependencies
import org.mockito.junit.jupiter.MockitoExtension;                      //Import JUnit integration for Mockito extension
import org.springframework.security.core.Authentication;                //Import Spring Security Authentication interface
import org.springframework.security.crypto.password.PasswordEncoder;    //Import password encoder interface from Spring Security
import java.util.List;                                                  //Import collections for roles
import java.util.Optional;                                              //Import optional for handling absent values
import static org.junit.jupiter.api.Assertions.*;                       //Static import for assertions
import static org.mockito.Mockito.*;                                    //Static import for mocking behavior

@ExtendWith(MockitoExtension.class) //Enable Mockito extension for this test class
class AuthServiceTest 
{
    @Mock
    private UserRepository userRepository;      //Mock the UserRepository dependency

    @Mock
    private JwtUtils jwtUtils;                  //Mock the JwtUtils dependency

    @Mock
    private PasswordEncoder passwordEncoder;    //Mock the PasswordEncoder dependency

    @InjectMocks
    private AuthService authService;            //Inject mocked dependencies into AuthService

    //Declare test variables
    private AuthRequest authRequest;
    private User user;

    @BeforeEach //Initialize test data before each test
    void setUp() 
    {
        authRequest = new AuthRequest("testUser", "password");  //create sample auth request
        user = new User();                                                          //instantiate new user
        user.setUsername("testUser");                                       //set username
        user.setPassword("encodedPassword");                                //set encoded password
        user.setRole(Role.CUSTOMER);                                                //assign user role
    }

    @Test   //Test: valid credentials should return successful AuthResponseDTO
    void authenticate_ValidCredentials_ReturnsAuthResponseDTO() 
    {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));                          //mock user found
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);  //mock password match
        when(jwtUtils.generateToken("testUser", List.of("CUSTOMER"))).thenReturn("mockedToken");        //mock token generation
        AuthResponseDTO response = authService.authenticate(authRequest);                                               //call method under test
        assertNotNull(response);                                                                        //assert response is not null
        assertEquals("mockedToken", response.getToken());                                       //assert correct token
        assertEquals("Authentication successful", response.getMessage());                       //assert success message
        assertEquals("Welcome, testUser! Your role is: CUSTOMER", response.getRoleMessage());   //assert role message
    }

    @Test   //Test: user not found should throw RuntimeException
    void authenticate_UserNotFound_ThrowsRuntimeException() 
    {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());   //mock user not found
        
        //expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest)); 
        assertEquals("User not found", exception.getMessage());                         //assert correct error message
    }

    @Test   //Test: invalid password should throw RuntimeException
    void authenticate_InvalidPassword_ThrowsRuntimeException() 
    {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));                                          //mock user found
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);             //mock password mismatch

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticate(authRequest)); //expect exception
        assertEquals("Invalid credentials", exception.getMessage());                                                        //assert correct error message
    }

    @Test   //Test: valid JWT token should return Authentication object
    void authenticateWithJwt_ValidToken_ReturnsAuthentication() 
    {
        String token = "valid.jwt.token";                                           //define sample token
        when(jwtUtils.isTokenValid(token)).thenReturn(true);                //mock valid token
        when(jwtUtils.getUsernameFromToken(token)).thenReturn("testUser");  //mock username extraction
        when(jwtUtils.getRolesFromToken(token)).thenReturn(List.of("USER"));    //mock roles extraction
        Authentication authentication = authService.authenticateWithJwt(token);     //call method under test
        assertNotNull(authentication);                                              //assert authentication is not null
        assertEquals("testUser", authentication.getName());                 //assert correct username
        assertEquals(token, ((JwtAuthenticationToken) authentication).getToken());  //assert correct token
    }

    @Test   //Test: invalid JWT token should throw RuntimeException
    void authenticateWithJwt_InvalidToken_ThrowsRuntimeException() 
    {
        String token = "invalid.jwt.token";                                         //define invalid token
        when(jwtUtils.isTokenValid(token)).thenReturn(false);                   //mock invalid token
        //expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticateWithJwt(token)); 
        assertEquals("Invalid or expired token", exception.getMessage());   //assert correct error message
    }

    @Test   //Test: valid refresh token should return a new token
    void refreshToken_ValidToken_ReturnsNewToken() 
    {
        String oldToken = "old.jwt.token";                                                                          //define old token
        when(jwtUtils.isTokenValid(oldToken)).thenReturn(true);                                             //mock valid token
        when(jwtUtils.getUsernameFromToken(oldToken)).thenReturn("testUser");                               //mock username extraction
        when(jwtUtils.getRolesFromToken(oldToken)).thenReturn(List.of("USER"));                                 //mock roles extraction
        when(jwtUtils.generateToken("testUser", List.of("USER"))).thenReturn("new.jwt.token");  //mock new token generation
        AuthResponse response = authService.refreshToken(oldToken);                                             //call method under test
        assertNotNull(response);                                                                                //assert response is not null
        assertEquals("new.jwt.token", response.getToken());                                             //assert new token value
    }

    @Test   //Test: invalid refresh token should throw RuntimeException
    void refreshToken_InvalidToken_ThrowsRuntimeException() 
    {
        String oldToken = "expired.jwt.token";                                      //define expired token
        when(jwtUtils.isTokenValid(oldToken)).thenReturn(false);                //mock invalid token
        
        //expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(oldToken)); 
        assertEquals("Invalid or expired token", exception.getMessage());   //assert correct error message
    }
}