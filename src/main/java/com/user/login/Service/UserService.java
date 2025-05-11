package com.user.login.Service;                                 //Package declaration for service layer
import com.user.login.DTO.UserDTO;                              //Data transfer object (DTO) for User entity
import com.user.login.Entity.User;                              //Entity representing the User
import com.user.login.Exception.EmailAlreadyExistsException;    //Custom exception for email duplication
import com.user.login.Exception.UserNotFoundException; //Custom exception for when a user is not found
import com.user.login.Exception.UsernameAlreadyExistsException; //Custom exception for username duplication
import com.user.login.Interface.UserInterface;                  //Interface defining the user service methods
import com.user.login.Mapper.UserMapper;                        //Mapper for converting between User entity and UserDTO
import com.user.login.Repository.UserRepository;                //Repository interface for user data persistence
import lombok.RequiredArgsConstructor;                          //Automatically generates constructor for dependencies
import org.springframework.stereotype.Service;                  //Marks this class as a service in the Spring context
import java.util.List;                                          //List for managing collections of users
import java.util.stream.Collectors;                             //For converting streams into lists

@Service                    //Marks this class as a service to be managed by Spring
@RequiredArgsConstructor    //Automatically generates constructor with required dependencies
public class UserService implements UserInterface 
{
    private final UserRepository userRepository;    //Repository for accessing user data
    private final UserMapper userMapper;            //Mapper to convert between User and UserDTO

    @Override   //Creates a new user after validating username and email
    public UserDTO createUser(UserDTO userDTO) 
    {
        //Check if username already exists in the repository
        if(userRepository.existsByUsername(userDTO.getUsername()))
            throw new UsernameAlreadyExistsException(userDTO.getUsername());    //Throw exception if username exists

        //Check if email already exists in the repository
        if(userRepository.existsByEmail(userDTO.getEmail())) 
            throw new EmailAlreadyExistsException(userDTO.getEmail());          //Throw exception if email exists

        User user = userMapper.toEntity(userDTO);                               //Convert DTO to entity
        User savedUser = userRepository.save(user);                             //Save the user entity to the database
        return userMapper.toDTO(savedUser);                                     //Return the saved user as DTO
    }

    @Override   //Retrieve a user by their ID
    public UserDTO getUser(Long userId) 
    {
        //Find user by ID, or throw exception if not found
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
        return userMapper.toDTO(user);  //Return the user as a DTO
    }

    @Override   //Retrieve all users and return them as a list of DTOs
    public List<UserDTO> getUsers() 
    {
        return userRepository.findAll().stream() .map(userMapper::toDTO).collect(Collectors.toList());
    }

    //Update an existing user's details
    public UserDTO updateUser(Long userId, UserDTO userDTO) 
    {
        //Find the existing user by ID, or throw exception if not found
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));

        //Partial update logic for different fields of the user
        if(userDTO.getUsername() != null) 
            existingUser.setUsername(userDTO.getUsername());        //Update username if provided
        
        if(userDTO.getEmail() != null) 
            existingUser.setEmail(userDTO.getEmail());              //Update email if provided
        
        if (userDTO.getHomeAddress() != null) 
            existingUser.setHomeAddress(userDTO.getHomeAddress());  //Update home address if provided
        
        if(userDTO.getPassword() != null) 
            existingUser.setPassword(userDTO.getPassword());        //Update password if provided
        
        if(userDTO.getRole() != null) 
            existingUser.setRole(userDTO.getRole());                //Update role if provided

        User updatedUser = userRepository.save(existingUser);       //Save the updated user entity to the database
        return userMapper.toDTO(updatedUser);                       //Return the updated user as DTO
    }

    @Override   //Delete a user by their ID
    public void deleteUser(Long userId) 
    {
        //Check if the user exists by ID, otherwise throw an exception
        if(!userRepository.existsById(userId)) 
            throw new UserNotFoundException(userId.toString());
        
        userRepository.deleteById(userId);  //Delete the user by ID
    }
}