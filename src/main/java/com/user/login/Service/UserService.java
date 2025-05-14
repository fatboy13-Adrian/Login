package com.user.login.Service;                                         //Package declaration for service layer
import com.user.login.DTO.UserDTO;                                      //Data transfer object (DTO) for User entity
import com.user.login.Entity.User;                                      //Entity representing the User
import com.user.login.Exception.EmailAlreadyExistsException;            //Custom exception for email duplication
import com.user.login.Exception.UserNotFoundException;                  //Custom exception for when a user is not found
import com.user.login.Exception.UsernameAlreadyExistsException;         //Custom exception for username duplication
import com.user.login.Interface.UserInterface;                          //Interface defining the user service methods
import com.user.login.Mapper.UserMapper;                                //Mapper for converting between User entity and UserDTO
import com.user.login.Repository.UserRepository;                        //Repository interface for user data persistence
import lombok.RequiredArgsConstructor;                                  //Automatically generates constructor for dependencies
import org.springframework.security.core.Authentication;                //Class for authentication details
import org.springframework.security.core.context.SecurityContextHolder; //Provides access to the security context
import org.springframework.security.crypto.password.PasswordEncoder;    //Import for encoding passwords securely
import org.springframework.beans.factory.annotation.Autowired;          //Import for dependency injection of beans
import org.springframework.security.access.AccessDeniedException;       //Exception thrown when access is denied
import org.springframework.stereotype.Service;                          //Marks this class as a service to be managed by Spring
import java.util.List;                                                  //List for managing collections of users
import java.util.stream.Collectors;                                     //For converting streams into lists

@Service                    //Marks this class as a service to be managed by Spring
@RequiredArgsConstructor    //Automatically generates constructor with required dependencies
public class UserService implements UserInterface 
{
    private final UserRepository userRepository;   //Repository for accessing user data

    @Autowired
    private final UserMapper userMapper;           //Mapper to convert between User and UserDTO

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override  //Creates a new user after validating username and email (Open to all)
    public UserDTO createUser(UserDTO userDTO) 
    {
        //Check if the username already exists in the repository
        if(userRepository.existsByUsername(userDTO.getUsername()))
            throw new UsernameAlreadyExistsException(userDTO.getUsername());    //Throw exception ifusername exists

        //Check if the email already exists in the repository
        if(userRepository.existsByEmail(userDTO.getEmail())) 
            throw new EmailAlreadyExistsException(userDTO.getEmail());          //Throw exception ifemail exists

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword()); //Hash and encode the password before saving new user account into DB.
        userDTO.setPassword(encodedPassword);                                   //Set the encoded password back into the DTO
        User user = userMapper.toEntity(userDTO);                               //Convert DTO to entity
        User savedUser = userRepository.save(user);                             //Save the user entity to the database
        return userMapper.toDTO(savedUser);                                     //Return the saved user as DTO
    }

    @Override  //Retrieve a user by their ID (Only can see your own user details)
    public UserDTO getUser(Long userId) 
    {
        checkUserOrAdminAuthorization(userId);  //Admin will have full acecss while other roles can only access their own user account  
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));    //Find the user or throw exception
        return userMapper.toDTO(user);          //Return the found user as DTO
    }

    @Override  //Retrieve all users and return them as a list of DTOs (Only Admin have the access rights)
    public List<UserDTO> getUsers() 
    {
        checkAdminAuthorization();  //Ensure only an admin can access all users
        return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());   //Convert all users to DTOs and return
    }

    @Override   //Update an existing user's details (Only can update your own user details)
    public UserDTO updateUser(Long userId, UserDTO userDTO) 
    {
        checkUserByIdAuthorization(userId); //Ensure the authenticated user has permission to update this user

        //Find the existing user by ID, or throw exception ifnot found
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));

        //Partial update logic for different fields of the user
        if(userDTO.getUsername() != null) 
            existingUser.setUsername(userDTO.getUsername());                        //Update username if provided
        
        if(userDTO.getEmail() != null) 
            existingUser.setEmail(userDTO.getEmail());                              //Update email if provided
        
        if(userDTO.getHomeAddress() != null) 
            existingUser.setHomeAddress(userDTO.getHomeAddress());                  //Update home address if provided
        
        if(userDTO.getPassword() != null) 
        {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword()); //Encode the new password before saving it     
            existingUser.setPassword(encodedPassword);                              //Update password with the encoded one
        }

        User updatedUser = userRepository.save(existingUser);                       //Save the updated user entity to the database
        return userMapper.toDTO(updatedUser);                                       //Return the updated user as DTO
    }

    @Override  //Delete a user by their ID (Only admin have the access rights)
    public void deleteUser(Long userId) 
    {
        checkAdminAuthorization();  //Ensure only an admin can delete users
        
        //Check ifthe user exists by ID, otherwise throw an exception
        if(!userRepository.existsById(userId)) 
            throw new UserNotFoundException(userId.toString());
        
        userRepository.deleteById(userId);  //Delete the user by ID
    }

    private String getAuthenticatedUsername() 
    {
        //Retrieve the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        //if no authentication or not authenticated, throw an exception
        if(authentication == null || !authentication.isAuthenticated()) 
            throw new AccessDeniedException("User not authenticated");
        
        return authentication.getName();    //Return the username of the authenticated user
    }

    private void checkUserByIdAuthorization(Long userId) 
    {
        //Retrieve the authenticated user's username
        String authUsername = getAuthenticatedUsername();
        
        //Fetch the user details using the username
        User authenticatedUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException(authUsername));

        //Check if the authenticated user matches the requested user ID
        if(!userId.equals(authenticatedUser.getUserId())) 
            throw new AccessDeniedException("You are not authorized to access this user data.");
    }

    private void checkAdminAuthorization() 
    {
        //Retrieve the authenticated user's username from security context
        String authUsername = getAuthenticatedUsername();
        
        //Check if the logged-in user is 'admin'
        if(!"admin".equals(authUsername)) 
            throw new AccessDeniedException("Only 'admin' can access this resource.");
    }

    private void checkUserOrAdminAuthorization(Long userId) 
    {
        //Retrieve the authenticated user's username from security context
        String authUsername = getAuthenticatedUsername();

        //Fetch the full authenticated User object using the username and throws a custom exception if user is not found in database
        User authenticatedUser = userRepository.findByUsername(authUsername).orElseThrow(() -> new UserNotFoundException(authUsername));

        boolean isAdmin = "admin".equals(authUsername);                      //Check if the authenticated user is an admin by comparing username to "admin"
        boolean isOwner = userId.equals(authenticatedUser.getUserId());     //Check if the authenticated user's ID matches the requested user ID

        //If user is neither admin nor owner of the requested user data, deny access
        if(!isAdmin && !isOwner) 
            throw new AccessDeniedException("You are not authorized to access this user data.");
    }
}