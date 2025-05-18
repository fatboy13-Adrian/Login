package com.user.login.DataLoader;

import com.user.login.DTO.UserDTO;
import com.user.login.Enum.Role;
import com.user.login.Entity.User;
import com.user.login.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UserDataLoader.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            logger.info("Users already exist, skipping data load.");
            return;
        }

        logger.info("Loading initial user data...");

        UserDTO adminDTO = buildUserDTO("admin", "Admin", "User", "admin@example.com", "+1234567890", "Admin Street, Admin City", "admin123", Role.ADMIN);
        UserDTO customer01DTO = buildUserDTO("customer01", "John", "Doe", "customer01@example.com", "+1987654321", "Customer Lane, Customer Town", "customer123", Role.CUSTOMER);
        UserDTO customer02DTO = buildUserDTO("customer02", "Jane", "Smith", "customer02@example.com", "+1987654322", "Customer Lane, Customer Town", "customer123", Role.CUSTOMER);
        UserDTO salesClerkDTO = buildUserDTO("sales_clerk", "Sally", "Clerk", "sales@example.com", "+1231231234", "Sales Street, Clerk City", "sales123", Role.SALES_CLERK);
        UserDTO warehouseSupervisorDTO = buildUserDTO("warehouse_supervisor", "Walter", "Supervisor", "warehouse@example.com", "+3213214321", "Warehouse Street, Supervisor City", "warehouse123", Role.WAREHOUSE_SUPERVISOR);

        saveUser(adminDTO);
        saveUser(customer01DTO);
        saveUser(customer02DTO);
        saveUser(salesClerkDTO);
        saveUser(warehouseSupervisorDTO);

        logger.info("Initial user data loaded successfully!");
    }

    private UserDTO buildUserDTO(String username, String firstName, String lastName, String email,
                                 String phoneNumber, String homeAddress, String rawPassword, Role role) {
        return UserDTO.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .homeAddress(homeAddress)
                .password(rawPassword)
                .role(role)
                .build();
    }

    private void saveUser(UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .homeAddress(userDTO.getHomeAddress())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(userDTO.getRole())
                .build();

        userRepository.save(user);
    }
}
