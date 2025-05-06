package com.user.login.Interface;

import java.util.List;

import com.user.login.DTO.UserDTO;

public interface UserInterface {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUser(Long userId);
    List<UserDTO> getUsers();
    UserDTO updateUser(Long userId, UserDTO userDTO);
    void deleteUser(Long userId);
}
