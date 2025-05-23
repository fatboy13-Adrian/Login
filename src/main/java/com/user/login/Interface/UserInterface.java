package com.user.login.Interface;               //Package declaration
import java.util.List;                          //Importing List for return type of getUsers method
import com.user.login.DTO.UserDTO;              //Importing UserDTO class for user-related data
import com.user.login.DTO.Auth.AuthResponseDTO; //Import DTO class used to transfer user data between layers

public interface UserInterface 
{ 
    UserDTO createUser(UserDTO userDTO);                        //Method to create a new user
    UserDTO getUser(Long userId);                               //Method to retrieve a user by their ID
    List<UserDTO> getUsers();                                   //Method to retrieve a list of all users
    AuthResponseDTO updateUser(Long userId, UserDTO userDTO);   //Method to update an existing user
    void deleteUser(Long userId);                               //Method to delete a user by their ID
}