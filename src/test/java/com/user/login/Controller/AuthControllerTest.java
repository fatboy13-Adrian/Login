package com.user.login.Controller;                  //Package declaration for the AuthController tests
import com.user.login.DTO.Auth.AuthRequestDTO;      //DTO for the login request payload
import com.user.login.DTO.Auth.AuthResponseDTO;     //DTO for the response payload from login/authentication
import com.user.login.Entity.Auth.AuthResponse;     //Entity class representing an authentication response (token info)
import com.user.login.Service.AuthService;          //The service class responsible for authentication logic
import org.junit.jupiter.api.BeforeEach;            //JUnit annotation for setting up before each test
import org.junit.jupiter.api.Test;                  //JUnit annotation to define a test method
import org.junit.jupiter.api.extension.ExtendWith;  //JUnit annotation to extend test functionality
import static org.mockito.Mockito.*;                //Mockito static methods to mock behavior
import static org.junit.jupiter.api.Assertions.*;   //JUnit assertions to validate test results
import org.mockito.InjectMocks;                     //To inject mocks into the class under test
import org.mockito.Mock;                            //Annotation to mock dependencies
import org.mockito.junit.jupiter.MockitoExtension;  //Extension for integrating Mockito with JUnit
import org.springframework.http.HttpStatus;         //HTTP Status for assertions
import org.springframework.http.ResponseEntity;     //ResponseEntity for HTTP response handling

@ExtendWith(MockitoExtension.class) //Extend test class with Mockito functionality for mocking
class AuthControllerTest 
{
    @Mock
    private AuthService authService;            //Mock the AuthService class (dependency of AuthController)

    @InjectMocks
    private AuthController authController;      //Inject the mocked AuthService into AuthController

    private AuthRequestDTO validRequestDTO;     //DTO to hold a valid login request (username and password)
    private AuthResponseDTO successResponseDTO; //DTO to hold successful authentication response (with token)

    @BeforeEach //Method that runs before each test
    void setUp() 
    {
        //Set up valid login request DTO with mock data
        validRequestDTO = new AuthRequestDTO();
        validRequestDTO.setUsername("user");
        validRequestDTO.setPassword("password");

        //Set up mock success response DTO with a token and message
        successResponseDTO = AuthResponseDTO.builder().token("mock-token").message("Login successful").build();
    }

    @Test   //POSITIVE TEST - Test case for successful login
    void testLoginSuccess() 
    {
        //Mock the behavior of AuthService.authenticate() to return a successful response
        when(authService.authenticate(any())).thenReturn(successResponseDTO);

        //Call the login method in AuthController
        ResponseEntity<AuthResponseDTO> response = authController.login(validRequestDTO);

        //Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                      //Status should be 200 OK
        assertEquals("mock-token", response.getBody().getToken());          //The token should match the mock value
        assertEquals("Login successful", response.getBody().getMessage());  //The message should be success message
    }

    @Test   //NEGATIVE TEST - Test case for login failure (invalid credentials)
    void testLoginFailure() 
    {
        //Mock the behavior of AuthService.authenticate() to throw an exception for invalid credentials
        when(authService.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        //Call the login method in AuthController
        ResponseEntity<AuthResponseDTO> response = authController.login(validRequestDTO);

        //Validate the HTTP status and response body
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());                //Status should be 401 Unauthorized
        assertNull(response.getBody().getToken());                                      //No token should be returned
        assertEquals("Authentication failed", response.getBody().getMessage()); //The message should indicate failure
    }

    @Test   //POSITIVE TEST - Test case for refreshing a token
    void testRefreshTokenSuccess() 
    {
        String oldToken = "oldToken";                   //Define an old token for refresh
        AuthResponse refreshed = new AuthResponse();    //Create a new AuthResponse for refreshed token
        refreshed.setToken("newToken");

        //Mock the behavior of AuthService.refreshToken() to return the refreshed token
        when(authService.refreshToken(oldToken)).thenReturn(refreshed);

        //Call the refreshToken method in AuthController
        ResponseEntity<AuthResponseDTO> response = authController.refreshToken(oldToken);

        //Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                                  //Status should be 200 OK
        assertEquals("newToken", response.getBody().getToken());                        //The new token should be returned
        assertEquals("Token refreshed successfully", response.getBody().getMessage());  //Success message should indicate refresh
    }

    @Test   //NEGATIVE TEST - Test case for refresh token failure (expired token)
    void testRefreshTokenFailure() 
    {
        String oldToken = "expiredToken";   //Define an expired token for the failure scenario

        //Mock the behavior of AuthService.refreshToken() to throw an exception for invalid token
        when(authService.refreshToken(oldToken)).thenThrow(new RuntimeException("Token invalid"));

        //Call the refreshToken method in AuthController
        ResponseEntity<AuthResponseDTO> response = authController.refreshToken(oldToken);

        //Validate the HTTP status and response body
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());                   //Status should be 403 Forbidden
        assertNull(response.getBody().getToken());                                      //No token should be returned
        assertEquals("Token refresh failed", response.getBody().getMessage());  //Failure message should indicate failure
    }

    @Test   //TEST - Test case for accessing a protected resource (no auth needed for this test)
    void testProtectedResource() 
    {
        ResponseEntity<String> response = authController.getProtectedResource();    //Call the getProtectedResource method in AuthController

        //Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                      //Status should be 200 OK
        assertEquals("This is a protected resource.", response.getBody());  //Response body should match the expected string
    }
}