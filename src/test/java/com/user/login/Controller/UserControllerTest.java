package com.user.login.Controller;

import com.user.login.DTO.UserDTO;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Enum.Role;
import com.user.login.Exception.*;
import com.user.login.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        userDTO = UserDTO.builder()
                .userId(1L)
                .username("testUser")
                .email("test@example.com")
                .homeAddress("123 Street")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    public void testCreateUser_Success() {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.createUser(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        String email = userDTO.getEmail();
        when(userService.createUser(any(UserDTO.class))).thenThrow(new EmailAlreadyExistsException(email));

        EmailAlreadyExistsException ex = assertThrows(EmailAlreadyExistsException.class,
                () -> userController.createUser(userDTO));
        assertEquals(email + " already exists in database", ex.getMessage());
        verify(userService, times(1)).createUser(any(UserDTO.class));
    }

    @Test
    public void testGetCurrentUser_Success() {
        when(userService.getCurrentUser()).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetCurrentUser_Failure() {
        when(userService.getCurrentUser()).thenThrow(new RuntimeException("Service Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userController.getCurrentUser());
        assertEquals("Service Error", exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testGetUser_Success() {
        when(userService.getUser(anyLong())).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userController.getUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).getUser(1L);
    }

    @Test
    public void testGetUser_UserNotFound() {
        Long userId = 1L;
        when(userService.getUser(anyLong())).thenThrow(new UserNotFoundException(userId.toString()));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userController.getUser(userId));
        assertEquals("User ID " + userId + " not found", exception.getMessage());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    public void testGetUsers_Success() {
        List<UserDTO> users = Collections.singletonList(userDTO);
        when(userService.getUsers()).thenReturn(users);

        ResponseEntity<List<UserDTO>> response = userController.getUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(userDTO));
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void testUpdateUser_Success() {
        AuthResponseDTO authResponseDTO = AuthResponseDTO.builder()
                .userId(userDTO.getUserId())
                .user(userDTO)
                .token("dummyToken123")
                .message("User updated successfully")
                .roleMessage("Role: USER")
                .build();

        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(authResponseDTO);

        ResponseEntity<AuthResponseDTO> response = userController.updateUser(1L, userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponseDTO, response.getBody());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        Long userId = 1L;
        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenThrow(new UserNotFoundException(userId.toString()));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.updateUser(userId, userDTO));
        assertEquals("User ID " + userId + " not found", exception.getMessage());
        verify(userService, times(1)).updateUser(anyLong(), any(UserDTO.class));
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser(anyLong());

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        Long userId = 1L;
        doThrow(new UserNotFoundException(userId.toString())).when(userService).deleteUser(anyLong());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userController.deleteUser(userId));
        assertEquals("User ID " + userId + " not found", exception.getMessage());
        verify(userService, times(1)).deleteUser(userId);
    }
}
