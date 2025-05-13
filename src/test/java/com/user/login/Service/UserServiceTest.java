package com.user.login.Service;                                         //Declares the package for the UserServiceTest class
import com.user.login.DTO.UserDTO;                                      //Imports the UserDTO class
import com.user.login.Entity.User;                                      //Imports the User entity class
import com.user.login.Exception.EmailAlreadyExistsException;            //Exception for duplicate emails
import com.user.login.Exception.UserNotFoundException;                  //Exception when a user is not found
import com.user.login.Exception.UsernameAlreadyExistsException;         //Exception for duplicate usernames
import com.user.login.Mapper.UserMapper;                                //Imports the UserMapper for mapping entities and DTOs
import com.user.login.Repository.UserRepository;                        //Imports the UserRepository interface
import org.junit.jupiter.api.BeforeEach;                                //JUnit annotation to run before each test
import org.junit.jupiter.api.Test;                                      //JUnit annotation to mark test methods
import org.junit.jupiter.api.extension.ExtendWith;                      //Used to extend test behavior with extensions
import org.mockito.InjectMocks;                                         //Tells Mockito to inject mocks into the tested class
import org.mockito.Mock;                                                //Marks a field to be mocked
import org.mockito.junit.jupiter.MockitoExtension;                      //Imports the MockitoExtension class to enable Mockito annotations in JUnit 5 tests.
import org.springframework.security.access.AccessDeniedException;       //Thrown when access is denied
import org.springframework.security.core.Authentication;                //Represents the authentication token
import org.springframework.security.core.context.SecurityContextHolder; //Holds the security context
import org.springframework.security.core.context.SecurityContext;       //Interface for accessing security context
import java.util.*;                                                     //Imports utility classes like Optional, List, Arrays
import static org.junit.jupiter.api.Assertions.*;                       //Static imports for assertions
import static org.mockito.ArgumentMatchers.any;                         //Allows flexible argument matching in Mockito
import static org.mockito.Mockito.*;                                    //Static imports for mocking behaviors

@ExtendWith(MockitoExtension.class) //Tells JUnit to run with Mockito extension
class UserServiceTest 
{
    @InjectMocks
    private UserService userService;            //Injects mocks into UserService

    @Mock
    private UserRepository userRepository;      //Mock for user data operations

    @Mock
    private UserMapper userMapper;              //Mock for mapping between User and UserDTO

    @Mock
    private SecurityContext securityContext;    //Mock for security context

    @Mock
    private Authentication authentication;      //Mock for authentication details

    @BeforeEach
    void setUp() 
    {
        SecurityContextHolder.setContext(securityContext);  //Set mocked security context before each test
    }

    @Test
    void createUser_shouldThrowException_ifUsernameExists() 
    {
        UserDTO dto = new UserDTO();                                                                        //Create a new UserDTO
        dto.setUsername("existing");                                                                //Set username to simulate existing user
        when(userRepository.existsByUsername("existing")).thenReturn(true);                 //Mock repository to return true
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(dto));  //Assert exception
    }

    @Test
    void createUser_shouldThrowException_ifEmailExists() 
    {
        UserDTO dto = new UserDTO();                                                                //Create a new UserDTO
        dto.setUsername("new");                                                             //Set new username
        dto.setEmail("duplicate@example.com");                                                  //Set duplicate email
        when(userRepository.existsByUsername("new")).thenReturn(false);                 //Username not taken
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);       //Email is taken
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto)); //Assert exception
    }

    @Test
    void createUser_shouldSaveAndReturnUser() 
    {
        UserDTO dto = new UserDTO();                                                            //Input DTO
        dto.setUsername("new");
        dto.setEmail("new@example.com");
        User userEntity = new User(); //Entity created from DTO
        User savedUser = new User(); //Entity returned from DB
        UserDTO savedDto = new UserDTO(); //Final result DTO
        when(userRepository.existsByUsername("new")).thenReturn(false);             //No duplicate username
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);    //No duplicate email
        when(userMapper.toEntity(dto)).thenReturn(userEntity);                                  //Map DTO to entity
        when(userRepository.save(userEntity)).thenReturn(savedUser);                                //Save user entity
        when(userMapper.toDTO(savedUser)).thenReturn(savedDto);                                     //Map back to DTO
        UserDTO result = userService.createUser(dto);                                           //Call method
        assertEquals(savedDto, result);                                                         //Assert result matches
    }

    @Test
    void getUser_shouldReturnUser_ifAuthorized() 
    {
        mockAuthentication("john");                                                 //Mock user is authenticated as 'john'
        User user = new User();                                                             //Simulate user in DB
        user.setUserId(1L);
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));  //Authenticated user found
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));                //Target user found
        when(userMapper.toDTO(user)).thenReturn(new UserDTO());                             //Convert to DTO
        UserDTO result = userService.getUser(1L);                                   //Call method
        assertNotNull(result);                                                              //Assert not null
    }

    @Test
    void getUser_shouldThrow_ifUnauthorized() 
    {
        mockAuthentication("john");                                                                 //Mock as 'john'
        User user = new User();                                                                             //Another user
        user.setUserId(99L);                                                                        //Different ID
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));              //Return logged in user
        assertThrows(AccessDeniedException.class, () -> userService.getUser(1L));   //Expect denial
    }

    @Test
    void getUser_shouldThrow_ifUserNotFound() 
    {
        //Auth as 'john'
        mockAuthentication("john"); 
        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));              //Found auth user
        when(userRepository.findById(1L)).thenReturn(Optional.empty());                             //Target user not found

        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));   //Expect exception
    }

    @Test
    void getUsers_shouldReturnList_ifAdmin() 
    {
        mockAuthentication("admin");                        //Mock admin user
        List<User> users = Arrays.asList(new User(), new User());   //List of users
        when(userRepository.findAll()).thenReturn(users);           //Return all users
        when(userMapper.toDTO(any())).thenReturn(new UserDTO());    //Map each to DTO
        List<UserDTO> result = userService.getUsers();              //Call method
        assertEquals(2, result.size());                     //Expect 2 DTOs
    }

    @Test
    void getUsers_shouldThrow_ifNotAdmin() 
    {
        mockAuthentication("user");                                                     //Not admin
        assertThrows(AccessDeniedException.class, () -> userService.getUsers());    //Expect denial
    }

    @Test
    void updateUser_shouldUpdateFields_ifAuthorized() 
    {
        mockAuthentication("john");                                                         //Auth as 'john'
        User existingUser = new User();                                                             //Existing DB user
        existingUser.setUserId(1L);
        existingUser.setUsername("john");
        existingUser.setEmail("old@example.com");
        UserDTO dto = new UserDTO();                                                                //Updated DTO
        dto.setEmail("new@example.com");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existingUser));  //Find auth user
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));                    //Target matches auth
        when(userRepository.save(existingUser)).thenReturn(existingUser);                               //Save updated
        when(userMapper.toDTO(existingUser)).thenReturn(dto);                                           //Return updated DTO
        UserDTO result = userService.updateUser(1L, dto);                                       //Call method
        assertEquals("new@example.com", result.getEmail());                                     //Check updated
    }

    @Test
    void updateUser_shouldThrow_ifUnauthorized() 
    {
        mockAuthentication("john");                                                                     //Logged in as john
        User user = new User();                                                                                 //Different user
        user.setUserId(99L);                                                                                //Not same ID
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));                      //Logged in user
        UserDTO dto = new UserDTO();                                                                            //Input DTO
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(1L, dto));   //Expect denial
    }

    @Test
    void deleteUser_shouldDelete_ifAdminAndUserExists() 
    {
        mockAuthentication("admin");                            //Admin user
        when(userRepository.existsById(1L)).thenReturn(true);   //User exists
        userService.deleteUser(1L);                                 //Call delete
        verify(userRepository).deleteById(1L);                          //Verify deletion
    }

    @Test
    void deleteUser_shouldThrow_ifNotAdmin() 
    {
        mockAuthentication("john");                                                                 //Non-admin
        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(1L));    //Expect denial
    }

    @Test
    void deleteUser_shouldThrow_ifUserNotFound() {
        mockAuthentication("admin");                                                                //Admin
        when(userRepository.existsById(1L)).thenReturn(false);                                      //User not found
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));    //Expect exception
    }

    private void mockAuthentication(String username) 
    {
        when(securityContext.getAuthentication()).thenReturn(authentication);   //Return mock auth
        when(authentication.isAuthenticated()).thenReturn(true);        //Authenticated
        when(authentication.getName()).thenReturn(username);                    //Return username
    }
}