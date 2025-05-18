    package com.user.login.Service;                                         //Package declaration for the service class
    import com.user.login.DTO.UserDTO;                                      //Import DTO class used to transfer user data between layers
    import com.user.login.DTO.Auth.AuthResponseDTO;                         //Imports the AuthResponseDTO for response data
    import com.user.login.Entity.User;                                      //Import User entity representing the database table
    import com.user.login.Exception.EmailAlreadyExistsException;            //Custom exception when email already exists
    import com.user.login.Exception.UserNotFoundException;                  //Custom exception when user ID is not found
    import com.user.login.Exception.UsernameAlreadyExistsException;         //Custom exception when username already exists
    import com.user.login.Exception.UsernameNotFoundException;              //Custom exception when username is not found
    import com.user.login.Interface.UserInterface;                          //Interface defining user-related operations
    import com.user.login.Mapper.UserMapper;                                //Mapper class for converting between User and UserDTO
    import com.user.login.Repository.UserRepository;                        //Repository interface for CRUD operations on User entities
    import com.user.login.Security.JWT.JwtUtils;                            //Imports utility for JWT handling
    import lombok.RequiredArgsConstructor;                                  //Lombok annotation to generate constructor with required arguments (final fields)
    import org.springframework.security.access.AccessDeniedException;       //Spring Security exception for unauthorized access
    import org.springframework.security.core.Authentication;                //Represents the current authenticated user
    import org.springframework.security.core.context.SecurityContextHolder; //Retrieves authentication from the security context
    import org.springframework.security.crypto.password.PasswordEncoder;    //Spring Security component for encoding passwords
    import org.springframework.stereotype.Service;                          //Marks this class as a Spring service
    import java.util.Optional;                                              //Java utility for working with optional values
    import java.util.List;                                                  //Java utility for working with collections
    import java.util.stream.Collectors;                                     //Java utility for transforming collections

    @Service                    //Defines this class as a Spring-managed service component
    @RequiredArgsConstructor    //Generates constructor for all final fields (dependency injection)
    public class UserService implements UserInterface 
    {
        private final JwtUtils jwtUtils;                
        private final UserRepository userRepository;    //Handles DB operations for User
        private final UserMapper userMapper;            //Converts between User and UserDTO
        private final PasswordEncoder passwordEncoder;  //Encodes passwords securely

        @Override
        public UserDTO createUser(UserDTO userDTO) 
        {
            //Check if username already exists
            if(userRepository.existsByUsername(userDTO.getUsername()))
                throw new UsernameAlreadyExistsException(userDTO.getUsername());
            
            //Check if email already exists
            if(userRepository.existsByEmail(userDTO.getEmail())) 
                throw new EmailAlreadyExistsException(userDTO.getEmail());
            
            userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword())); //Encode the password before saving
            User savedUser = userRepository.save(userMapper.toEntity(userDTO)); //Convert DTO to entity and save
            return userMapper.toDTO(savedUser);                                 //Return the saved user as DTO
        }

        @Override
        public UserDTO getUser(Long userId) 
        {
            authorizeUserOrAdmin(userId);                   //Allow access only to admin or the user themselves
            return userMapper.toDTO(findUserById(userId));  //Fetch and return user as DTO
        }

        @Override
        public List<UserDTO> getUsers() 
        {
            authorizeAdmin();   //Only admin can retrieve all users

            //Fetch all users and convert to DTO list
            return userRepository.findAll().stream().map(userMapper::toDTO).collect(Collectors.toList());
        }

        @Override
        public AuthResponseDTO updateUser(Long userId, UserDTO userDTO) 
        {
            authorizeSelf(userId);                                                          //Ensure the user is authorized to update their own information
            User user = findUserById(userId);                                               //Retrieve the user entity from the database by ID
            Optional.ofNullable(userDTO.getFirstName()).ifPresent(user::setFirstName);      //Update first name if it's provided in the DTO
            Optional.ofNullable(userDTO.getLastName()).ifPresent(user::setLastName);        //Update last name if it's provided in the DTO
            Optional.ofNullable(userDTO.getUsername()).ifPresent(user::setUsername);        //Update username if it's provided in the DTO
            Optional.ofNullable(userDTO.getEmail()).ifPresent(user::setEmail);              //Update email if it's provided in the DTO
            Optional.ofNullable(userDTO.getPhoneNumber()).ifPresent(user::setPhoneNumber);  //Update phone number if it's provided in the DTO
            Optional.ofNullable(userDTO.getHomeAddress()).ifPresent(user::setHomeAddress);  //Update home address if it's provided in the DTO

            //If a new password is provided, encrypt it and update
            Optional.ofNullable(userDTO.getPassword()).ifPresent(pwd -> user.setPassword(passwordEncoder.encode(pwd)));

            User updatedUser = userRepository.save(user);                                   //Save the updated user entity to the database
            UserDTO updatedUserDTO = userMapper.toDTO(updatedUser);                         //Convert the updated user entity to a DTO
            List<String> roles = List.of(updatedUser.getRole().name());                     //Extract role(s) for JWT token creation; adjust if multiple roles exist
            String token = jwtUtils.generateToken(updatedUser.getUsername(), roles);        //Generate a new JWT token with the updated username and roles

            //Return a response DTO containing the updated user, token, and messages
            return AuthResponseDTO.builder().userId(updatedUser.getUserId()).user(updatedUserDTO).token(token).message("User updated successfully")
            .roleMessage("Role: " + updatedUser.getRole().name()).build();
        }

        @Override
        public void deleteUser(Long userId) 
        {
            authorizeAdmin();   //Only admin can delete users

            //Check if user exists before deleting
            if(!userRepository.existsById(userId))
                throw new UserNotFoundException(userId.toString());
            
            userRepository.deleteById(userId);  //Delete user by ID
        }

        public String getAuthenticatedUsername() 
        {
            //Retrieve authentication context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            //If not authenticated, throw exception
            if(auth == null || !auth.isAuthenticated()) 
                throw new AccessDeniedException("User not authenticated");
                
            return auth.getName();  //Return current username
        }

        public UserDTO getCurrentUser() 
        {
            //Get current authenticated user and convert to DTO
            User user = findUserByUsername(getAuthenticatedUsername());
            return userMapper.toDTO(user);
        }

        //Authorization and Lookup Helpers
        private void authorizeAdmin() 
        {
            //Throw exception if current user is not admin
            if(!isAdmin())
                throw new AccessDeniedException("Only 'admin' can access this resource.");
        }

        private void authorizeSelf(Long userId) 
        {
            //Get currently authenticated user
            if(!userId.equals(getAuthenticatedUser().getUserId()))
                throw new AccessDeniedException("You are not authorized to modify this user.");
        }

        private void authorizeUserOrAdmin(Long userId) 
        {
            //Allow access if current user is owner or admin
            User currentUser = getAuthenticatedUser();
            boolean isOwner = userId.equals(currentUser.getUserId());

            if(!isOwner && !isAdmin()) 
                throw new AccessDeniedException("You are not authorized to access this user data.");
        }

        public User getAuthenticatedUser() 
        {
            return findUserByUsername(getAuthenticatedUsername());  //Return current authenticated user entity
        }

        private boolean isAdmin() 
        {
            return "admin".equals(getAuthenticatedUsername());      //Check if current username is 'admin'
        }

        private User findUserById(Long userId) 
        {
            //Lookup user by ID or throw not found exception
            return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
        }

        private User findUserByUsername(String username) 
        {
            //Lookup user by username or throw not found exception
            return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        }
    }