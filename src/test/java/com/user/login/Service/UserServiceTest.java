package com.user.login.Service;                         //Declare the package for the UserService test class
import com.user.login.DTO.UserDTO;                      //Import Data Transfer Object for User
import com.user.login.Entity.User;                      //Import Entity class for User
import com.user.login.Enum.Role;                        //Import Role enum used for user roles
import com.user.login.Exception.UserNotFoundException;  //Import custom exception for user not found cases
import com.user.login.Mapper.UserMapper;                //Import mapper for converting between User and UserDTO
import com.user.login.Repository.UserRepository;        //Import repository interface for database operations
import java.util.*;                                     //Import utility classes
import org.junit.jupiter.api.BeforeEach;                //Runs setup before each test
import org.junit.jupiter.api.Test;                      //Marks a method as a test case
import org.junit.jupiter.api.extension.ExtendWith;      //Integrates Mockito with JUnit 5
import org.mockito.InjectMocks;                         //Injects mocks into the class under test
import org.mockito.Mock;                                //Creates mock objects
import org.mockito.junit.jupiter.MockitoExtension;      //Enables Mockito extension for JUnit 5
import static org.junit.jupiter.api.Assertions.*;       //Provides assertion methods
import static org.mockito.ArgumentMatchers.any;         //Allows flexible argument matching in mocks
import static org.mockito.Mockito.*;                    //Provides mocking utilities (e.g., when(), verify())

@ExtendWith(MockitoExtension.class) //Enable Mockito support in JUnit 5 tests
public class UserServiceTest 
{
    @Mock
    private UserRepository userRepository;  //Mock the UserRepository dependency

    @Mock
    private UserMapper userMapper;          //Mock the UserMapper dependency

    @InjectMocks
    private UserService userService;        //Inject the mocked dependencies into UserService

    private User user;
    private UserDTO userDTO;                //Declare sample User and UserDTO objects

    @BeforeEach //Set up reusable test data before each test
    void setUp() 
    {
        //Create sample User entity
        user = User.builder().userId(1L).username("adrian").email("adrian@example.com")
        .homeAddress("Singapore").password("password123").role(Role.CUSTOMER).build();

        //Create sample UserDTO
        userDTO = UserDTO.builder().userId(1L).username("adrian").email("adrian@example.com")
        .homeAddress("Singapore").password("password123").role(Role.CUSTOMER).build();
    }

    @Test   //Test successful user creation
    void testCreateUser_success() 
    {
        when(userMapper.toEntity(userDTO)).thenReturn(user);                        //Map DTO to Entity
        when(userRepository.save(user)).thenReturn(user);                           //Save user to repo
        when(userMapper.toDTO(user)).thenReturn(userDTO);                           //Map Entity back to DTO
        UserDTO result = userService.createUser(userDTO);                           //Call method under test
        assertEquals(userDTO, result);                                              //Verify output matches input
        verify(userRepository, times(1)).save(user);    //Ensure save() is called once
    }

    @Test   //Test successful retrieval of user by ID
    void testGetUser_success() 
    {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));        //Mock user found
        when(userMapper.toDTO(user)).thenReturn(userDTO);                           //Map Entity to DTO
        UserDTO result = userService.getUser(1L);                           //Call method under test
        assertEquals(userDTO, result);                                              //Verify returned user matches expected
        verify(userRepository, times(1)).findById(1L);  //Ensure findById is called once
    }

    @Test   //Test behavior when mapper returns null, expecting NullPointerException
    void testCreateUser_mapperReturnsNull_shouldThrowException() 
    {
        when(userMapper.toEntity(userDTO)).thenReturn(null);                                            //Simulate mapper failure
        when(userRepository.save(null)).thenThrow(new NullPointerException("User entity is null")); //Throw exception
        assertThrows(NullPointerException.class, () -> userService.createUser(userDTO));        //Assert exception
        verify(userMapper).toEntity(userDTO);       //Verify mapping attempt
        verify(userRepository).save(null);  //Verify save call
    }

    @Test   //Test propagation of repository exception during user creation
    void testCreateUser_repositoryThrowsException_shouldPropagate() 
    {
        when(userMapper.toEntity(userDTO)).thenReturn(user);                                    //Mock valid entity
        when(userRepository.save(user)).thenThrow(new RuntimeException("DB error"));    //Simulate DB error
        
        //Expect RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.createUser(userDTO));  

        assertEquals("DB error", ex.getMessage());  //Validate error message
        verify(userMapper).toEntity(userDTO);               //Ensure mapping was attempted
        verify(userRepository).save(user);                  //Ensure save was attempted
    }

    @Test   //Test behavior when user is not found by ID
    void testGetUser_notFound() 
    {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());                                 //Simulate user not found
        assertThrows(UserNotFoundException.class, () -> userService.getUser(1L));   //Expect custom exception
        verify(userRepository, times(1)).findById(1L);                      //Ensure repository method was called
    }

    @Test   //Test retrieving a list of all users
    void testGetUsers_success() 
    {
        List<User> userList = List.of(user);                                //Mock entity list
        List<UserDTO> userDTOList = List.of(userDTO);                       //Expected DTO list
        when(userRepository.findAll()).thenReturn(userList);                //Return list of users
        when(userMapper.toDTO(user)).thenReturn(userDTO);                   //Map each to DTO
        List<UserDTO> result = userService.getUsers();                      //Call method under test
        assertEquals(userDTOList, result);                                  //Verify result
        verify(userRepository, times(1)).findAll(); //Verify repository method call
    }

    @Test   //Test successful user update
    void testUpdateUser_success() 
    {
        UserDTO updatedDTO = UserDTO.builder().username("newname").email("new@example.com").build(); //Define updated data
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));    //Simulate user exists
        when(userRepository.save(any(User.class))).thenReturn(user);        //Simulate save
        when(userMapper.toDTO(user)).thenReturn(updatedDTO);                    //Map to updated DTO
        UserDTO result = userService.updateUser(1L, updatedDTO);        //Call update method
        assertEquals("newname", result.getUsername());                  //Validate username update
        assertEquals("new@example.com", result.getEmail());             //Validate email update
        verify(userRepository).save(any(User.class));                       //Ensure save was called
    }

    @Test   //Test user update when user not found
    void testUpdateUser_notFound() 
    {
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); //Simulate user not found
        UserDTO update = new UserDTO();                                     //Empty DTO for test
        
        //Expect exception
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, update)); 
    }

    @Test   //Test successful user deletion
    void testDeleteUser_success() 
    {
        when(userRepository.existsById(1L)).thenReturn(true);   //Simulate user exists
        userService.deleteUser(1L);                                 //Call delete method
        verify(userRepository).deleteById(1L);                          //Ensure delete was called
    }

    @Test   //Test user deletion when user does not exist
    void testDeleteUser_notFound() 
    {
        when(userRepository.existsById(1L)).thenReturn(false);                                      //Simulate user missing
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));    //Expect exception
    }
}