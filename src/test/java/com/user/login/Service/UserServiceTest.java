package com.user.login.Service;                                         //Declares the package for the UserServiceTest class
import com.user.login.DTO.UserDTO;                                      //Imports the UserDTO class
import com.user.login.DTO.Auth.AuthResponseDTO;                         //Imports the AuthResponseDTO for response data
import com.user.login.Entity.User;                                      //Imports the User entity class
import com.user.login.Enum.Role;                                        //Imports the Role enum
import com.user.login.Exception.EmailAlreadyExistsException;            //Exception for duplicate emails
import com.user.login.Exception.UserNotFoundException;                  //Exception when a user is not found
import com.user.login.Exception.UsernameAlreadyExistsException;         //Exception for duplicate usernames
import com.user.login.Mapper.UserMapper;                                //Imports the UserMapper for mapping entities and DTOs
import com.user.login.Repository.UserRepository;                        //Imports the UserRepository interface
import com.user.login.Security.JWT.JwtUtils;                            //Imports utility for JWT handling
import org.junit.jupiter.api.BeforeEach;                                //JUnit annotation to run before each test
import org.junit.jupiter.api.Test;                                      //JUnit annotation to define a test method
import org.junit.jupiter.api.extension.ExtendWith;                      //Used to extend test behavior with extensions
import org.mockito.InjectMocks;                                         //Tells Mockito to inject mocks into the tested class
import org.mockito.Mock;                                                //Marks a field to be mocked
import org.mockito.junit.jupiter.MockitoExtension;                      //Enables Mockito annotations in JUnit 5 tests
import org.springframework.security.access.AccessDeniedException;       //Exception thrown for unauthorized access
import org.springframework.security.core.Authentication;                //Represents the authentication token
import org.springframework.security.core.context.SecurityContextHolder; //Holds the security context globally
import org.springframework.security.crypto.password.PasswordEncoder;    //Handles password encoding
import org.springframework.security.core.context.SecurityContext;       //Interface for accessing security context
import java.util.*;                                                     //Imports utility classes like Optional, List, Arrays
import static org.junit.jupiter.api.Assertions.*;                       //Static imports for assertion methods
import static org.mockito.ArgumentMatchers.any;                         //Allows any argument matching in Mockito
import static org.mockito.ArgumentMatchers.anyList;                     //Allows matching of any list in Mockito
import static org.mockito.ArgumentMatchers.eq;                          //Allows equality match in Mockito
import static org.mockito.Mockito.*;                                    //Imports all Mockito static methods

@ExtendWith(MockitoExtension.class) //Tells JUnit to run the test with Mockito support
class UserServiceTest 
{
    @InjectMocks
    private UserService userService;            //Automatically injects the below mocks into this service

    @Mock
    private UserRepository userRepository;      //Mocked repository for user-related DB operations

    @Mock
    private UserMapper userMapper;              //Mocked mapper to convert between entity and DTO

    @Mock
    private SecurityContext securityContext;    //Mocked security context for authentication

    @Mock
    private Authentication authentication;      //Mocked authentication object

    @Mock
    private PasswordEncoder passwordEncoder;    //Mocked password encoder

    @Mock
    private JwtUtils jwtUtils;                  //Mocked utility for JWT token creation

    @BeforeEach
    void setUp() 
    {
        SecurityContextHolder.setContext(securityContext);  //Setup mocked security context before each test
    }

    @Test
    void createUser_shouldThrowException_ifUsernameExists() 
    {
        UserDTO dto = new UserDTO();                                                                    //Create a test user DTO
        dto.setUsername("existing");                                                            //Set username to simulate existing user
        when(userRepository.existsByUsername("existing")).thenReturn(true);                 //Simulate existing username in DB
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(dto));  //Assert exception is thrown
    }

    @Test
    void createUser_shouldThrowException_ifEmailExists() 
    {
        UserDTO dto = new UserDTO();                                                                //Create a test user DTO
        dto.setUsername("new");                                                             //Simulate available username
        dto.setEmail("duplicate@example.com");                                                  //Set email that already exists
        when(userRepository.existsByUsername("new")).thenReturn(false);                 //Username does not exist
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);       //Email already taken
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto)); //Expect exception
    }

    @Test
    void createUser_shouldSaveAndReturnUser() 
    {
        UserDTO dto = new UserDTO();                    //Input DTO
        dto.setUsername("new");
        dto.setEmail("new@example.com");
        dto.setPassword("plainPassword");   //Raw password
        User userEntity = new User();               //Entity to be saved
        User savedUser = new User();                //Saved entity
        UserDTO savedDto = new UserDTO();           //DTO returned from mapper

        //Setup mocks
        when(userRepository.existsByUsername("new")).thenReturn(false);                 //No username conflict
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);            //No email conflict
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");    //Simulate encoding
        when(userMapper.toEntity(dto)).thenReturn(userEntity);                                          //Map DTO to entity
        when(userRepository.save(userEntity)).thenReturn(savedUser);                                    //Save entity
        when(userMapper.toDTO(savedUser)).thenReturn(savedDto);                                         //Map saved entity back to DTO
        UserDTO result = userService.createUser(dto);                                                   //Call method under test
        assertEquals(savedDto, result);                                                                 //Validate returned result
    }

    @Test
    void getUser_shouldReturnUser_ifAuthorized() 
    {
        mockAuthentication("john");                                                 //Authenticated as 'john'
        User user = new User();                                                             //Create user
        user.setUserId(1L);
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));  //Authenticated user found
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));                //Target user found
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());                             //Map user to DTO
        UserDTO result = userService.getUser(1L);                                   //Call method
        assertNotNull(result);                                                              //Assert user returned
    }

    @Test
    void getUser_shouldThrow_ifUnauthorized() 
    {
        mockAuthentication("john");                                                         //Authenticated user 'john'
        User user = new User();                                                                     //Create another user
        user.setUserId(99L);                                                                //Different user ID
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));          //Auth user mismatch
        assertThrows(AccessDeniedException.class, () -> userService.getUser(1L));   //Should throw access denied
    }

    @Test
    void getUser_shouldThrow_ifUserNotFound() 
    {
        mockAuthentication("john");                                                             //Authenticated as john
        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));              //Logged-in user found
        when(userRepository.findById(1L)).thenReturn(Optional.empty());                             //Target user not found
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));   //Should throw user not found
    }

    @Test
    void getUsers_shouldReturnList_ifAdmin() 
    {
        mockAuthentication("admin");                        //Authenticated as admin
        List<User> users = Arrays.asList(new User(), new User());   //Dummy users
        when(userRepository.findAll()).thenReturn(users);           //Return all users
        when(userMapper.toDTO(any())).thenReturn(new UserDTO());    //Map each to DTO
        List<UserDTO> result = userService.getUsers();              //Call method
        assertEquals(2, result.size());                     //Assert size of result
    }

    @Test
    void getUsers_shouldThrow_ifNotAdmin() 
    {
        mockAuthentication("user");                                                     //Authenticated as non-admin
        assertThrows(AccessDeniedException.class, () -> userService.getUsers());    //Expect exception
    }

    @Test
    void updateUser_shouldUpdateFields_ifAuthorized() 
    {
        User existingUser = new User();     //Existing user setup
        existingUser.setUserId(1L);
        existingUser.setUsername("john");
        existingUser.setEmail("old@example.com");
        existingUser.setRole(Role.CUSTOMER);

        UserDTO userDTO = new UserDTO();    //DTO with updated fields
        userDTO.setEmail("new@example.com");

        mockAuthentication("john"); //Authenticated as 'john'

        //Setup mocks
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDTO(existingUser)).thenReturn(userDTO);
        when(jwtUtils.generateToken(eq("john"), anyList())).thenReturn("dummy-token");

        AuthResponseDTO authResponseDTO = userService.updateUser(1L, userDTO);  //Call update method

        //Assertions
        assertNotNull(authResponseDTO);                                                     //Assert response is not null
        assertEquals(1L, authResponseDTO.getUserId());                              //Check user ID
        assertEquals(userDTO, authResponseDTO.getUser());                                   //Check updated DTO
        assertEquals("dummy-token", authResponseDTO.getToken());                    //Token value
        assertEquals("User updated successfully", authResponseDTO.getMessage());    //Message
        assertEquals("Role: CUSTOMER", authResponseDTO.getRoleMessage());           //Role info
    }

    @Test
    void updateUser_shouldThrow_ifUnauthorized() 
    {
        mockAuthentication("john");                                                                 //Authenticated as john
        User user = new User();                                                                                 //Target user is different
        user.setUserId(99L);                                                                            //Different ID
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));                      //Current user info
        UserDTO dto = new UserDTO();                                                                            //Update DTO
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(1L, dto));   //Unauthorized access
    }

    @Test
    void deleteUser_shouldDelete_ifAdminAndUserExists() 
    {
        mockAuthentication("admin");                            //Admin user
        when(userRepository.existsById(1L)).thenReturn(true);   //User exists
        userService.deleteUser(1L);                                 //Call delete
        verify(userRepository).deleteById(1L);                          //Verify deletion was called
    }

    @Test
    void deleteUser_shouldThrow_ifNotAdmin() 
    {
        mockAuthentication("john"); //Non-admin
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(1L));    //Unauthorized deletion
    }

    @Test
    void deleteUser_shouldThrow_ifUserNotFound() 
    {
        mockAuthentication("admin");                            //Admin
        when(userRepository.existsById(1L)).thenReturn(false);  //User does not exist
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));    //Expect exception
    }

    //Utility method to mock authentication context with given username
    private void mockAuthentication(String username) 
    {
        when(securityContext.getAuthentication()).thenReturn(authentication);   //Set mock authentication
        when(authentication.getName()).thenReturn(username);                    //Return given username as authenticated user
    }
}