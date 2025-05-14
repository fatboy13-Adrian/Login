package com.user.login.DTO;                     //Declares the package that this class belongs to
import org.springframework.web.bind.annotation.CrossOrigin;

import com.user.login.Enum.Role;                //Imports the Role enum used to define user roles
import jakarta.validation.constraints.Email;    //Validates that the email format is correct
import jakarta.validation.constraints.NotBlank; //Ensures the field is not null or empty
import jakarta.validation.constraints.NotNull;  //Ensures the field is not null (used for non-String fields)
import jakarta.validation.constraints.Size;     //Validates the length of a string
import lombok.*;                                //Imports Lombok annotations to reduce boilerplate code (e.g., getters, setters, constructors)

@Getter             //Lombok: generates getter methods for all fields
@Setter             //Lombok: generates setter methods for all fields
@NoArgsConstructor  //Lombok: generates a no-argument constructor
@AllArgsConstructor //Lombok: generates a constructor with all fields as parameters
@Builder            //Lombok: enables the builder pattern for creating instances of this class
@CrossOrigin(origins = "http://localhost:3000")
public class UserDTO 
{ 
    private Long userId;                                                                //ID of the user (typically auto-generated; not validated manually)

    @NotBlank(message = "Username is required")                                         //Validates that username is not null or empty
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")  //Validates username length
    private String username; //Field to store the user's username

    @NotBlank(message = "Email is required")                                            //Validates that email is not null or empty
    @Email(message = "Email must be valid")                                             //Validates email format
    private String email;                                                               //Field to store the user's email address

    @NotBlank(message = "Home address is required")                                     //Validates that home address is not null or empty
    private String homeAddress;                                                         //Field to store the user's home address

    @NotBlank(message = "Password is required")                                         //Validates that password is not null or empty
    @Size(min = 8, message = "Password must be at least 8 characters")                  //Validates password length
    private String password;                                                            //Field to store the user's password

    @NotNull(message = "Role is required")                                              //Ensures the role is not null
    private Role role;                                                                  //Field to store the user's role (e.g., ADMIN, CUSTOMER)
}