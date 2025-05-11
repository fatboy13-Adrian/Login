package com.user.login.Controller;                      //Package declaration for the UserController tests
import com.user.login.DTO.UserDTO;                      //DTO representing user data (e.g., username, email, etc.)
import com.user.login.Exception.UserNotFoundException;  //Custom exception thrown when a user is not found
import com.user.login.Service.UserService;              //Service class responsible for user-related operations
import com.user.login.Enum.Role;                        //Enum representing the user's role (e.g., CUSTOMER, ADMIN)
import org.junit.jupiter.api.BeforeEach;                //JUnit annotation for setting up before each test
import org.junit.jupiter.api.Test;                      //JUnit annotation to define a test method
import org.junit.jupiter.api.extension.ExtendWith;      //JUnit annotation to extend test functionality
import static org.junit.jupiter.api.Assertions.*;       //JUnit assertions to validate test results
import static org.mockito.Mockito.*;                    //Mockito static methods to mock behavior
import java.util.Collections;                           //Utility class for creating immutable collections like singleton lists for testing purposes
import org.mockito.InjectMocks;                         //To inject mocks into the class under test
import org.mockito.Mock;                                //Annotation to mock dependencies
import org.mockito.junit.jupiter.MockitoExtension;      //Extension for integrating Mockito with JUnit
import org.springframework.http.HttpStatus;             //HTTP Status for assertions
import org.springframework.http.ResponseEntity;         //ResponseEntity for HTTP response handling

@ExtendWith(MockitoExtension.class) //Extend test class with Mockito functionality for mocking
public class UserControllerTest 
{
    @Mock
    private UserService userService;        //Mock the UserService class (dependency of UserController)

    @InjectMocks
    private UserController userController;  //Inject the mocked UserService into UserController

    private UserDTO userDTO;                //DTO to hold mock user data for the tests

    @BeforeEach //Method to set up the common test data before each test runs
    public void setUp() 
    {
        userDTO = UserDTO.builder().userId(1L).username("testUser").email("test@example.com")
        .homeAddress("123 Street").password("password123").role(Role.CUSTOMER).build();
    }

    @Test   //POSITIVE TEST - Test case for successfully creating a user
    public void testCreateUser_Success() 
    {
        //Arrange: Mock the UserService.createUser() method to return the mock userDTO
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        //Act: Call the createUser method in UserController
        ResponseEntity<?> response = userController.createUser(userDTO);

        //Assert: Validate the HTTP status and response body
        assertEquals(HttpStatus.CREATED, response.getStatusCode());                                     //Status should be 201 CREATED
        assertEquals(userDTO, response.getBody());                                                      //Response body should match the mock userDTO
        verify(userService, times(1)).createUser(any(UserDTO.class));   //Verify that createUser was called once
    }

    @Test   //NEGATIVE TEST - Test case for failure when creating a user
    public void testCreateUser_Failure() 
    {
        //Arrange: Mock the UserService.createUser() method to throw an exception
        when(userService.createUser(any(UserDTO.class))).thenThrow(new RuntimeException("Service Error"));

        //Act: Call the createUser method in UserController
        ResponseEntity<?> response = userController.createUser(userDTO);

        //Assert: Validate the HTTP status and error message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());                       //Status should be 500 INTERNAL SERVER ERROR
        assertTrue(response.getBody().toString().contains("Failed to create user"));                //Error message should contain the failure message
        verify(userService, times(1)).createUser(any(UserDTO.class));   //Verify that createUser was called once
    }

    @Test   //POSITIVE TEST - Test case for successfully retrieving a user by ID
    public void testGetUser_Success() 
    {
        //Arrange: Mock the UserService.getUser() method to return the mock userDTO
        when(userService.getUser(anyLong())).thenReturn(userDTO);

        //Act: Call the getUser method in UserController
        ResponseEntity<?> response = userController.getUser(1L);

        //Assert: Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                          //Status should be 200 OK
        assertEquals(userDTO, response.getBody());                                  //Response body should match the mock userDTO
        verify(userService, times(1)).getUser(1L);  //Verify that getUser was called once
    }

    @Test   //NEGATIVE TEST - Test case for user not found when retrieving by ID
    public void testGetUser_UserNotFound() 
    {
        //Arrange: Mock the UserService.getUser() method to throw UserNotFoundException
        when(userService.getUser(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        //Act: Call the getUser method in UserController
        ResponseEntity<?> response = userController.getUser(1L);

        //Assert: Validate the HTTP status and error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());               //Status should be 404 NOT FOUND
        assertTrue(response.getBody().toString().contains("User not found"));       //Error message should indicate user not found
        verify(userService, times(1)).getUser(1L);  //Verify that getUser was called once
    }

    @Test   //POSITIVE TEST - Test case for successfully retrieving all users
    public void testGetUsers_Success() 
    {
        //Arrange: Mock the UserService.getUsers() method to return a list containing the mock userDTO
        when(userService.getUsers()).thenReturn(Collections.singletonList(userDTO));

        //Act: Call the getUsers method in UserController
        ResponseEntity<?> response = userController.getUsers();

        //Assert: Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                      //Status should be 200 OK
        assertTrue(((java.util.List<?>) response.getBody()).contains(userDTO)); //Response body should contain the mock userDTO
        verify(userService, times(1)).getUsers();       //Verify that getUsers was called once
    }

    @Test   //NEGATIVE TEST - Test case for failure when retrieving users
    public void testGetUsers_Failure() 
    {
        //Arrange: Mock the UserService.getUsers() method to throw an exception
        when(userService.getUsers()).thenThrow(new RuntimeException("Service Error"));

        //Act: Call the getUsers method in UserController
        ResponseEntity<?> response = userController.getUsers();

        //Assert: Validate the HTTP status and error message
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());       //Status should be 500 INTERNAL SERVER ERROR
        assertTrue(response.getBody().toString().contains("Failed to get users"));  //Error message should indicate failure
        verify(userService, times(1)).getUsers();           //Verify that getUsers was called once
    }

    @Test   //POSITIVE TEST - Test case for successfully updating a user
    public void testUpdateUser_Success() 
    {
        //Arrange: Mock the UserService.updateUser() method to return the mock userDTO
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(userDTO);

        //Act: Call the updateUser method in UserController
        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        //Assert: Validate the HTTP status and response body
        assertEquals(HttpStatus.OK, response.getStatusCode());                                                      //Status should be 200 OK
        assertEquals(userDTO, response.getBody());                                                                  //Response body should match the mock userDTO
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));    //Verify that updateUser was called once
    }

    @Test   //NEGATIVE TEST - Test case for user not found when updating
    public void testUpdateUser_UserNotFound() 
    {
        //Arrange: Mock the UserService.updateUser() method to throw UserNotFoundException
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        //Act: Call the updateUser method in UserController
        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        //Assert: Validate the HTTP status and error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());                                               //Status should be 404 NOT FOUND
        assertTrue(response.getBody().toString().contains("User not found"));                                   //Error message should indicate user not found
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));    //Verify that updateUser was called once
    }

    @Test   //POSITIVE TEST - Test case for successfully deleting a user
    public void testDeleteUser_Success() 
    {
        //Arrange: Mock the UserService.deleteUser() method to do nothing
        doNothing().when(userService).deleteUser(anyLong());

        //Act: Call the deleteUser method in UserController
        ResponseEntity<?> response = userController.deleteUser(1L);

        //Assert: Validate the HTTP status
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());                  //Status should be 204 NO CONTENT
        verify(userService, times(1)).deleteUser(1L);   //Verify that deleteUser was called once
    }

    @Test   //NEGATIVE TEST - Test case for user not found when deleting
    public void testDeleteUser_UserNotFound() 
    {
        //Arrange: Mock the UserService.deleteUser() method to throw UserNotFoundException
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(anyLong());

        //Act: Call the deleteUser method in UserController
        ResponseEntity<?> response = userController.deleteUser(1L);

        //Assert: Validate the HTTP status and error message
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());                   //Status should be 404 NOT FOUND
        assertTrue(response.getBody().toString().contains("User not found"));           //Error message should indicate user not found
        verify(userService, times(1)).deleteUser(1L);   //Verify that deleteUser was called once
    }
}