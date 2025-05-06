package com.user.login.DataLoader;

import com.user.login.Entity.User;
import com.user.login.Enum.Role;
import com.user.login.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("Loading initial data...");

            User admin = User.builder()
                .username("admin")
                .email("admin@example.com")
                .homeAddress("Admin Street, Admin City")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();

            User customer = User.builder()
                .username("customer")
                .email("customer@example.com")
                .homeAddress("Customer Lane, Customer Town")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.CUSTOMER)
                .build();

            User salesClerk = User.builder()
                .username("sales_clerk") // fixed typo from "sales_ckerk"
                .email("sales@example.com")
                .homeAddress("Sales Street, Clerk City")
                .password(passwordEncoder.encode("sales123"))
                .role(Role.SALES_CLERK)
                .build();

            User warehouseSupervisor = User.builder()
                .username("warehouse_supervisor")
                .email("warehouse@example.com")
                .homeAddress("Warehouse Street, Supervisor City")
                .password(passwordEncoder.encode("warehouse123"))
                .role(Role.WAREHOUSE_SUPERVISOR)
                .build();

            userRepository.save(admin);
            userRepository.save(customer);
            userRepository.save(salesClerk);
            userRepository.save(warehouseSupervisor);

            System.out.println("Initial data loaded successfully!");
        } else {
            System.out.println("Data already exists, skipping load...");
        }
    }
}
