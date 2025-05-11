package com.user.login.Service;

import com.user.login.DTO.UserDTO;
import com.user.login.Entity.User;
import com.user.login.Enum.Role;
import com.user.login.Exception.UserNotFoundException;
import com.user.login.Mapper.UserMapper;
import com.user.login.Repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(1L)
                .username("adrian")
                .email("adrian@example.com")
                .homeAddress("Singapore")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        userDTO = UserDTO.builder()
                .userId(1L)
                .username("adrian")
                .email("adrian@example.com")
                .homeAddress("Singapore")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();
    }

    @Test
    void testCreateUser_success() {
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.createUser(userDTO);

        assertEquals(userDTO, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUser(1L);

        assertEquals(userDTO, result);
        verify(userRepository, times(1)).findById(1L);
    }

   @Test
void testCreateUser_mapperReturnsNull_shouldThrowException() {
    when(userMapper.toEntity(userDTO)).thenReturn(null);
    when(userRepository.save(null)).thenThrow(new NullPointerException("User entity is null"));

    assertThrows(NullPointerException.class, () -> userService.createUser(userDTO));

    verify(userMapper).toEntity(userDTO);
    verify(userRepository).save(null);
}


@Test
void testCreateUser_repositoryThrowsException_shouldPropagate() {
    when(userMapper.toEntity(userDTO)).thenReturn(user);
    when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));

    RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(userDTO));

    assertEquals("DB error", ex.getMessage());
    verify(userMapper).toEntity(userDTO);
    verify(userRepository).save(user);
}

    @Test
    void testGetUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUsers_success() {
        List<User> userList = List.of(user);
        List<UserDTO> userDTOList = List.of(userDTO);

        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        List<UserDTO> result = userService.getUsers();

        assertEquals(userDTOList, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testUpdateUser_success() {
        UserDTO updatedDTO = UserDTO.builder()
                .username("newname")
                .email("new@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDTO(user)).thenReturn(updatedDTO);

        UserDTO result = userService.updateUser(1L, updatedDTO);

        assertEquals("newname", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO update = new UserDTO();
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, update));
    }

    @Test
    void testDeleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_notFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
