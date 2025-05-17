package com.user.login.Controller;                      //Define package for UserController class
import com.user.login.DTO.UserDTO;                      //Import UserDTO for user data transfer objects
import com.user.login.DTO.Auth.AuthResponseDTO;         //Import AuthResponseDTO for user data transfer objects
import com.user.login.Exception.UserNotFoundException;  //Import custom exception for user not found cases
import com.user.login.Service.UserService;              //Import service layer to handle user operations
import lombok.RequiredArgsConstructor;                  //Lombok annotation to generate constructor for final fields
import org.springframework.http.HttpStatus;             //HTTP status codes for responses
import org.springframework.http.ResponseEntity;         //Wrap HTTP response data and status
import org.springframework.web.bind.annotation.*;       //Spring MVC annotations for REST controller and mappings
import java.util.List;                                  //Java List collection for multiple users

@CrossOrigin(origins = "http://localhost:3000")
@RestController             //Marks class as a REST controller
@RequestMapping("/users")   //Base URL path for all endpoints in this controller
@RequiredArgsConstructor    //Lombok auto-generates constructor for final fields
public class UserController 
{                           
    private final UserService userService;  //Inject UserService dependency

    //HTTP POST endpoint to create new user
    @PostMapping                                       
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) 
    {
        try 
        {
            UserDTO createdUser = userService.createUser(userDTO);          //Delegate user creation to service
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);   //Return created user with 201 status
        } 
        
        catch(Exception e) 
        {                                        
            //Handle generic exceptions, return 500 status on failure and include error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to get user by ID
    @GetMapping("/{userId}")                               
    public ResponseEntity<?> getUser(@PathVariable Long userId) 
    {
        try 
        {
            UserDTO user = userService.getUser(userId);     //Fetch user from service by ID
            return ResponseEntity.ok(user);                  //Return user data with 200 OK
        } 
        
        catch(UserNotFoundException e) 
        {                  
            //Handle user not found scenario and return 404 if user absent and include exception message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } 
        
        catch(Exception e) 
        {                               
            //Handle other exceptions and return 500 on failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to get all users
    @GetMapping                                           
    public ResponseEntity<?> getUsers() 
    {
        try 
        {
            List<UserDTO> users = userService.getUsers(); //Retrieve list of all users from service
            return ResponseEntity.ok(users);              //Return list with 200 OK
        } 
        
        catch(Exception e) 
        {                            
            //Handle any errors and return 500 status
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get users: " + e.getMessage());
        }
    }

    //HTTP PUT endpoint to update user by ID
    @PutMapping("/{userId}")                             
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) 
    {
        try 
        {
            AuthResponseDTO authResponse = userService.updateUser(userId, userDTO); //Update user via service
            return ResponseEntity.ok(authResponse);                                 //Return the whole AuthResponseDTO
        } 
        
        catch(UserNotFoundException e) 
        {                 
            //Handle user not found and return 404 if user not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } 
        
        catch(Exception e) 
        {                              
            //Handle other exceptions and return 500 on failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user: " + e.getMessage());
        }
    }

    //HTTP DELETE endpoint to delete user by ID
    @DeleteMapping("/{userId}")                           
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) 
    {
        try 
        {
            userService.deleteUser(userId);                //Delete user via service
            return ResponseEntity.noContent().build();     //Return 204 No Content on success
        } 
        
        catch(UserNotFoundException e) 
        {                 
            //Handle user not found and return 404 if user absent
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } 
        
        catch(Exception e) 
        {                              
            //Handle other exceptions and return 500 on failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to get current authenticated user
    @GetMapping("/me")                                    
    public ResponseEntity<?> getCurrentUser() 
    {
        try 
        {
            UserDTO currentUser = userService.getCurrentUser(); //Fetch current user info from service
            return ResponseEntity.ok(currentUser);              //Return user data with 200 OK
        } 
        
        catch(Exception e) 
        {                              
            //Handle errors and return 500 on failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get current user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to get authenticated username
    @GetMapping("/me/username")                           
    public ResponseEntity<?> getAuthenticatedUsername() 
    {
        try 
        {
            String username = userService.getAuthenticatedUsername();   //Get current username from service
            return ResponseEntity.ok(username);                         //Return username with 200 OK
        } 
        
        catch(Exception e) 
        {
            //Handle error and return 500 on failure
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get username: " + e.getMessage());
        }
    }
}