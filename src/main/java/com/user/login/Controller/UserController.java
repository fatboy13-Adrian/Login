package com.user.login.Controller;

import com.user.login.DTO.UserDTO;
import com.user.login.DTO.Auth.AuthResponseDTO;
import com.user.login.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    // Get the currently authenticated user
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }

    // Get a specific user by userId
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("userId") Long userId) {
        UserDTO user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = userService.getUsers();
        return ResponseEntity.ok(users);
    }

    // Update user by userId
    @PatchMapping("/{userId}")
    public ResponseEntity<AuthResponseDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO userDTO) {
        AuthResponseDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete user by userId
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
