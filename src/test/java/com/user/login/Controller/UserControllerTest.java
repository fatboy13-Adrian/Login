package com.user.login.Controller;

import com.user.login.DTO.UserDTO;
import com.user.login.Exception.UserNotFoundException;
import com.user.login.Service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
        // Add other fields if needed
    }

    // ✅ POSITIVE - Create user
    @Test
    void testCreateUserSuccess() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<?> response = userController.createUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    // ❌ NEGATIVE - Create user throws generic exception
    @Test
    void testCreateUserFailure() {
        when(userService.createUser(any(UserDTO.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<?> response = userController.createUser(userDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to create user"));
    }

    // ✅ POSITIVE - Get user by ID
    @Test
    void testGetUserSuccess() {
        when(userService.getUser(1L)).thenReturn(userDTO);

        ResponseEntity<?> response = userController.getUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    // ❌ NEGATIVE - Get user by ID not found
    @Test
    void testGetUserNotFound() {
        when(userService.getUser(1L)).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<?> response = userController.getUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));
    }

    // ❌ NEGATIVE - Get user by ID throws generic error
    @Test
    void testGetUserFailure() {
        when(userService.getUser(1L)).thenThrow(new RuntimeException("DB issue"));

        ResponseEntity<?> response = userController.getUser(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to retrieve user"));
    }

    // ✅ POSITIVE - Get all users
    @Test
    void testGetUsersSuccess() {
        List<UserDTO> userList = Arrays.asList(userDTO);
        when(userService.getUsers()).thenReturn(userList);

        ResponseEntity<?> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
    }

    // ❌ NEGATIVE - Get all users throws error
    @Test
    void testGetUsersFailure() {
        when(userService.getUsers()).thenThrow(new RuntimeException("Unable to retrieve"));

        ResponseEntity<?> response = userController.getUsers();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to retrieve users"));
    }

    // ✅ POSITIVE - Update user
    @Test
    void testUpdateUserSuccess() {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    // ❌ NEGATIVE - Update user not found
    @Test
    void testUpdateUserNotFound() {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));
    }

    // ❌ NEGATIVE - Update user generic error
    @Test
    void testUpdateUserFailure() {
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenThrow(new RuntimeException("Update failed"));

        ResponseEntity<?> response = userController.updateUser(1L, userDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to update user"));
    }

    // ✅ POSITIVE - Delete user
    @Test
    void testDeleteUserSuccess() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ❌ NEGATIVE - Delete user not found
    @Test
    void testDeleteUserNotFound() {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));
    }

    // ❌ NEGATIVE - Delete user fails with generic error
    @Test
    void testDeleteUserFailure() {
        doThrow(new RuntimeException("Delete failed")).when(userService).deleteUser(1L);

        ResponseEntity<?> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Failed to delete user"));
    }
}
