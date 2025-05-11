package com.user.login.Controller;                      //Declares the package this class belongs to, part of the overall project structure
import com.user.login.DTO.UserDTO;                      //Imports the UserDTO class, used to transfer user data between layers
import com.user.login.Service.UserService;              //Imports the service class that contains business logic for user operations
import lombok.RequiredArgsConstructor;                  //Lombok annotation to generate a constructor for final fields (here: userService)
import com.user.login.Exception.UserNotFoundException;  //Imports a custom exception class thrown when a user is not found
import org.springframework.http.HttpStatus;             //Imports Spring's HTTP status codes (e.g., 200 OK, 404 Not Found, etc.)
import org.springframework.http.ResponseEntity;         //Imports the ResponseEntity class to build complete HTTP responses
import org.springframework.web.bind.annotation.*;       //Imports annotations to handle HTTP request mappings like GET, POST, PUT, DELETE
import java.util.List;                                  //Imports List collection to hold multiple UserDTOs

@CrossOrigin(origins = "http://localhost:3000")         //Allows cross-origin requests from frontend at http://localhost:3000 (e.g., React app)
@RestController                                         //Marks this class as a REST controller capable of handling RESTful web requests
@RequestMapping("/users")                               //Base URL path for all endpoints in this controller (e.g., /users)
@RequiredArgsConstructor                                //Lombok annotation that generates a constructor for all final fields (e.g., userService)
public class UserController 
{
    private final UserService userService;  //Final service dependency injected via constructor for user-related business logic

    //HTTP POST endpoint to create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) 
    {
        try 
        {
            UserDTO createdUser = userService.createUser(userDTO);              //Delegate to service layer to create a new user
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser); //Return HTTP 201 Created with the newly created user
        } 
        
        catch(Exception e) 
        {
            //Return HTTP 500 Internal Server Error if creation fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to fetch a specific user by ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) 
    {
        try 
        {
            UserDTO user = userService.getUser(userId); //Delegate to service layer to get user by ID
            return ResponseEntity.ok(user);             //Return HTTP 200 OK with the found user
        } 
        
        catch(UserNotFoundException e) 
        {
            //Return HTTP 404 Not Found if user doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } 
        
        catch(Exception e) 
        {
            //Return HTTP 500 Internal Server Error for other errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get user: " + e.getMessage());
        }
    }

    //HTTP GET endpoint to fetch all users
    @GetMapping
    public ResponseEntity<?> getUsers() 
    {
        try 
        {
            List<UserDTO> users = userService.getUsers();   //Get list of all users from service               
            return ResponseEntity.ok(users);                //Return HTTP 200 OK with the user list
        } 
        
        catch(Exception e) 
        {
            //Return HTTP 500 Internal Server Error if something fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get users: " + e.getMessage());
        }
    }

    //HTTP PUT endpoint to update an existing user by ID
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) 
    {
        try 
        {
            UserDTO updatedUser = userService.updateUser(userId, userDTO);  //Update the user and return the updated user data
            return ResponseEntity.ok(updatedUser);                          //Return HTTP 200 OK with the updated user
        } 
        
        catch(UserNotFoundException e) 
        {
            //Return HTTP 404 Not Found if user doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } 
        
        catch(Exception e) 
        {
            //Return HTTP 500 Internal Server Error for general failures
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user: " + e.getMessage());
        }
    }

    //HTTP DELETE endpoint to delete a user by ID
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) 
    {
        try 
        {
            userService.deleteUser(userId);             //Call service layer to delete user
            return ResponseEntity.noContent().build();  //Return HTTP 204 No Content (successful deletion, no body)
        } 
        
        catch(UserNotFoundException e) 
        {
            //Return HTTP 404 Not Found if user doesn't exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } 
        
        catch(Exception e) 
        {
            //Return HTTP 500 Internal Server Error for general errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }
}