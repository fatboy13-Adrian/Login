package com.user.login.DTO.Auth;    //Declares the package that this class belongs to
import com.user.login.DTO.UserDTO;  //Import DTO class used to transfer user data between layers
import lombok.AllArgsConstructor;   //Generates a constructor with all arguments
import lombok.Builder;              //Generates a builder for object creation
import lombok.Getter;               //Generates getters for all fields
import lombok.NoArgsConstructor;    //Generates a no-argument constructor
import lombok.Setter;               //Generates setters for all fields

@Getter                             //Generates getter methods for all fields
@Setter                             //Generates setter methods for all fields
@Builder                            //Provides a builder pattern for creating instances of this class
@AllArgsConstructor                 //Generates a constructor with all fields
@NoArgsConstructor                  //Generates a no-argument constructor
public class AuthResponseDTO 
{
    private Long userId;                        //user ID variable for response
    private UserDTO user;                       //User DTO for response
    private String token, message, roleMessage; //token, message and role message for response
}
