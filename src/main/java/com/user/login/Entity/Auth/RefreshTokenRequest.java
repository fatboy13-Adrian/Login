package com.user.login.Entity.Auth; //Declares the package for the RefreshTokenRequest class, which is part of the 'Auth' module within the 'Entity' package.
import lombok.AllArgsConstructor;   //Imports the AllArgsConstructor annotation to generate a constructor with parameters for all fields in the class.
import lombok.Builder;              //Imports the Builder annotation to generate a builder pattern for the class, allowing for easy object construction.
import lombok.Getter;               //Imports the Getter annotation to automatically generate getter methods for all fields in the class.
import lombok.NoArgsConstructor;    //Imports the NoArgsConstructor annotation to generate a no-argument constructor for the class.
import lombok.Setter;               //Imports the Setter annotation to automatically generate setter methods for all fields in the class.

@Getter                             //Automatically generates getter methods for all fields in the class.
@Setter                             //Automatically generates setter methods for all fields in the class.
@AllArgsConstructor                 //Automatically generates a constructor with parameters for all fields in the class.
@NoArgsConstructor                  //Automatically generates a no-argument constructor for the class.
@Builder                            //Automatically generates a builder pattern for the class to simplify object creation.
public class RefreshTokenRequest 
{
    private String oldToken;        //Declares a private field named 'oldToken' that stores the value of the old token for refresh.
}