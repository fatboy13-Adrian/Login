package com.user.login.Service;

import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.DTO.UserDTO;
import com.user.login.Entity.User;
import com.user.login.Enum.Role;
import com.user.login.Exception.EmailAlreadyExistsException;
import com.user.login.Exception.UserNotFoundException;
import com.user.login.Exception.UsernameAlreadyExistsException;
import com.user.login.Mapper.UserMapper;
import com.user.login.Repository.UserRepository;
import com.user.login.Security.JWT.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // Helper method to mock authentication context with a given username
    private void mockAuthentication(String username) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createUser_shouldThrowException_ifUsernameExists() {
        UserDTO dto = new UserDTO();
        dto.setUsername("existingUser");
        dto.setEmail("newemail@example.com");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldThrowException_ifEmailExists() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newUser");
        dto.setEmail("existingemail@example.com");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_shouldEncodePasswordAndSaveUser() {
        UserDTO dto = new UserDTO();
        dto.setUsername("newUser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("plainPassword");

        User userEntity = new User();
        User savedUser = new User();
        savedUser.setUserId(1L);
        UserDTO savedDto = new UserDTO();
        savedDto.setUserId(1L);

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userMapper.toEntity(any())).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toDTO(savedUser)).thenReturn(savedDto);

        UserDTO result = userService.createUser(dto);

        assertEquals(1L, result.getUserId());
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(userEntity);
    }

    @Test
void getUser_shouldReturnUserDTO_ifAuthorizedAsOwner() {
    Long userId = 1L;
    String username = "user1";

    User user = new User();
    user.setUserId(userId);
    user.setUsername(username);

    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(userId);
    userDTO.setUsername(username);

    mockAuthentication(username);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));  // <--- ADD THIS
    when(userMapper.toDTO(user)).thenReturn(userDTO);

    UserDTO result = userService.getUser(userId);

    assertEquals(userId, result.getUserId());
    assertEquals(username, result.getUsername());
}


 @Test
void getUser_shouldThrowAccessDeniedException_ifNotOwnerOrAdmin() {
    Long userId = 1L;

    mockAuthentication("otherUser");

    User user = new User();
    user.setUserId(userId);
    user.setUsername("user1");

    User otherUser = new User();
    otherUser.setUsername("otherUser");

    when(userRepository.findByUsername("otherUser")).thenReturn(Optional.of(otherUser)); // MUST mock this

    assertThrows(AccessDeniedException.class, () -> userService.getUser(userId));
}



    @Test
    void getUsers_shouldReturnAllUsers_ifAdmin() {
        mockAuthentication("admin");

        User user1 = new User();
        User user2 = new User();
        UserDTO dto1 = new UserDTO();
        UserDTO dto2 = new UserDTO();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDTO(user1)).thenReturn(dto1);
        when(userMapper.toDTO(user2)).thenReturn(dto2);

        List<UserDTO> result = userService.getUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUsers_shouldThrowAccessDeniedException_ifNotAdmin() {
        mockAuthentication("someUser");

        assertThrows(AccessDeniedException.class, () -> userService.getUsers());
    }

    @Test
void updateUser_shouldUpdateUserAndReturnAuthResponse_ifAuthorized() {
    Long userId = 1L;
    String username = "user1";

    mockAuthentication(username);

    UserDTO updateDto = new UserDTO();
    updateDto.setFirstName("John");
    updateDto.setPassword("newPassword");

    User user = new User();
    user.setUserId(userId);
    user.setUsername(username);
    user.setRole(Role.CUSTOMER);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));  // <--- Add this
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(userMapper.toDTO(any(User.class))).thenReturn(updateDto);
    when(jwtUtils.generateToken(eq(username), anyList())).thenReturn("jwtToken");

    AuthResponseDTO response = userService.updateUser(userId, updateDto);

    assertNotNull(response);
    assertEquals("User updated successfully", response.getMessage());
    assertEquals("jwtToken", response.getToken());
    verify(passwordEncoder).encode("newPassword");
    verify(userRepository).save(any(User.class));
}

    @Test
void updateUser_shouldThrowAccessDeniedException_ifNotOwner() {
    Long userId = 1L;
    String authenticatedUsername = "otherUser";
    mockAuthentication(authenticatedUsername);

    // Mock the authenticated user returned by findByUsername
    User otherUser = new User();
    otherUser.setUsername(authenticatedUsername);
    when(userRepository.findByUsername(authenticatedUsername)).thenReturn(Optional.of(otherUser));

    UserDTO updateDto = new UserDTO();

    assertThrows(AccessDeniedException.class, () -> userService.updateUser(userId, updateDto));
}


    @Test
    void deleteUser_shouldDeleteUser_ifAdminAndUserExists() {
        Long userId = 1L;
        mockAuthentication("admin");

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowUserNotFoundException_ifUserDoesNotExist() {
        Long userId = 1L;
        mockAuthentication("admin");

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void deleteUser_shouldThrowAccessDeniedException_ifNotAdmin() {
        Long userId = 1L;
        mockAuthentication("user");

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUserDTO() {
        String username = "user1";
        mockAuthentication(username);

        User user = new User();
        user.setUsername(username);

        UserDTO userDTO = new UserDTO();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        UserDTO currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
    }

    @Test
    void getAuthenticatedUsername_shouldReturnUsername_whenAuthenticated() {
        String username = "user1";
        mockAuthentication(username);

        String actual = userService.getAuthenticatedUsername();

        assertEquals(username, actual);
    }

    @Test
    void getAuthenticatedUsername_shouldThrowAccessDeniedException_whenNotAuthenticated() {
        SecurityContextHolder.clearContext();

        assertThrows(AccessDeniedException.class, () -> userService.getAuthenticatedUsername());
    }
}
