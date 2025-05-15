package com.user.login.Controller;                      //Package declaration for controller tests
import com.user.login.DTO.UserDTO;                      //Import UserDTO for user data representation
import com.user.login.Exception.UserNotFoundException;  //Import exception for user-not-found scenario
import com.user.login.Service.UserService;              //Import UserService to mock business logic
import com.user.login.Enum.Role;                        //Import Role enum for user roles
import org.junit.jupiter.api.BeforeEach;                //Import for setup method before each test
import org.junit.jupiter.api.Test;                      //Import annotation to define test methods
import org.junit.jupiter.api.extension.ExtendWith;      //Import to extend test class with mock support
import static org.junit.jupiter.api.Assertions.*;       //Import JUnit assertions
import static org.mockito.Mockito.*;                    //Import Mockito methods for mocking
import java.util.Collections;                           //Import utility class for creating singleton lists
import org.mockito.InjectMocks;                         //Annotation to inject mocks into test subject
import org.mockito.Mock;                                //Annotation to define mock dependencies
import org.mockito.junit.jupiter.MockitoExtension;      //Extension to enable Mockito with JUnit 5
import org.springframework.http.HttpStatus;             //Import HTTP status codes
import org.springframework.http.ResponseEntity;         //Import class to wrap HTTP responses

@ExtendWith(MockitoExtension.class) //Enable Mockito in JUnit 5 tests
public class UserControllerTest 
{
    @Mock
    private UserService userService;        //Mocked UserService dependency

    @InjectMocks
    private UserController userController;  //Controller under test with mock dependencies injected

    private UserDTO userDTO;                //Sample UserDTO object for test cases

    @BeforeEach
    public void setUp() 
    {
        //Initialize userDTO with test values before each test
        userDTO = UserDTO.builder().userId(1L).username("testUser").email("test@example.com")
        .homeAddress("123 Street").password("password123").role(Role.CUSTOMER).build();
    }

    @Test
    public void testCreateUser_Success() 
    {
        //Simulate successful user creation
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        //Call controller method
        ResponseEntity<?> response = userController.createUser(userDTO);

        //Assert correct response status and body
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());

        //Verify service method was called once
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    public void testCreateUser_Failure() 
    {
        //Simulate exception during user creation
        when(userService.createUser(any(UserDTO.class))).thenThrow(new RuntimeException("Service Error"));

        //Call controller method
        ResponseEntity<?> response = userController.createUser(userDTO);

        //Assert internal server error and error message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to create user"));

        //Verify service method was called once
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    public void testGetUser_Success() 
    {
        //Simulate successful user retrieval
        when(userService.getUser(anyLong())).thenReturn(userDTO);

        //Call controller method
        ResponseEntity<?> response = userController.getUser(1L);

        //Assert success response and returned user
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());

        //Verify correct method call
        verify(userService, times(1)).getUser(1L);
    }

    @Test
    public void testGetUser_UserNotFound() 
    {
        //Simulate user not found scenario
        when(userService.getUser(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        //Call controller method
        ResponseEntity<?> response = userController.getUser(1L);

        //Assert 404 response and message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));

        //Verify service method call
        verify(userService, times(1)).getUser(1L);
    }

    @Test
    public void testGetUsers_Success() 
    {
        //Simulate fetching list of users
        when(userService.getUsers()).thenReturn(Collections.singletonList(userDTO));

        //Call controller method
        ResponseEntity<?> response = userController.getUsers();

        //Assert response contains userDTO
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((java.util.List<?>) response.getBody()).contains(userDTO));

        //Verify service method call
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void testGetUsers_Failure() 
    {
        //Simulate error during fetching users
        when(userService.getUsers()).thenThrow(new RuntimeException("Service Error"));

        //Call controller method
        ResponseEntity<?> response = userController.getUsers();

        //Assert internal server error and message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to get users"));

        //Verify service method call
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void testUpdateUser_Success() 
    {
        //Simulate successful user update
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(userDTO);

        //Call controller method
        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        //Assert response with updated user
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());

        //Verify update call
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() 
    {
        //Simulate update with user not found
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        //Call controller method
        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        //Assert 404 response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));

        //Verify update call
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    public void testDeleteUser_Success() 
    {
        //Simulate successful delete
        doNothing().when(userService).deleteUser(anyLong());

        //Call controller method
        ResponseEntity<?> response = userController.deleteUser(1L);

        //Assert 204 No Content
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        //Verify delete method call
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    public void testDeleteUser_UserNotFound() 
    {
        //Simulate delete with user not found
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(anyLong());

        //Call controller method
        ResponseEntity<?> response = userController.deleteUser(1L);

        //Assert 404 response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));

        //Verify delete method call
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    public void testGetCurrentUser_Success() 
    {
        //Simulate successful fetch of current user
        when(userService.getCurrentUser()).thenReturn(userDTO);

        //Call controller method
        ResponseEntity<?> response = userController.getCurrentUser();

        //Assert 200 OK and correct user
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());

        //Verify service method call
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetCurrentUser_Failure() 
    {
        //Simulate failure fetching current user
        when(userService.getCurrentUser()).thenThrow(new RuntimeException("Service Error"));

        //Call controller method
        ResponseEntity<?> response = userController.getCurrentUser();

        //Assert internal server error
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to get current user"));

        //Verify service method call
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetAuthenticatedUsername_Success() 
    {
        //Simulate fetching username
        when(userService.getAuthenticatedUsername()).thenReturn("testUser");

        //Call controller method
        ResponseEntity<?> response = userController.getAuthenticatedUsername();

        //Assert username in response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody());

        //Verify method call
        verify(userService, times(1)).getAuthenticatedUsername();
    }

    @Test
    public void testGetAuthenticatedUsername_Failure() 
    {
        //Simulate failure in fetching username
        when(userService.getAuthenticatedUsername()).thenThrow(new RuntimeException("Service Error"));

        //Call controller method
        ResponseEntity<?> response = userController.getAuthenticatedUsername();

        //Assert internal server error
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to get username"));

        //Verify method call
        verify(userService, times(1)).getAuthenticatedUsername();
    }
}