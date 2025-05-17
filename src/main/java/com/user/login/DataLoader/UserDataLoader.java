package com.user.login.DataLoader;                                      //Declares that this class is part of the com.user.login.DataLoader package
import com.user.login.Entity.User;                                      //Imports the User entity class
import com.user.login.Enum.Role;                                        //Imports the Role enum which defines user roles
import com.user.login.Repository.UserRepository;                        //Imports the UserRepository for DB operations
import org.springframework.beans.factory.annotation.Autowired;          //Allows Spring to inject dependencies automatically
import org.springframework.boot.CommandLineRunner;                      //Enables the class to run code at application startup
import org.springframework.security.crypto.password.PasswordEncoder;    //For securely hashing user passwords
import org.springframework.stereotype.Component;                        //Marks this class as a Spring-managed component

@Component //Registers this class as a Spring Bean so it gets executed during application startup
public class UserDataLoader implements CommandLineRunner 
{
    private final UserRepository userRepository;    //Repository to perform CRUD operations on User
    private final PasswordEncoder passwordEncoder;  //Encoder to hash passwords securely

    @Autowired //Injects the dependencies via constructor
    public UserDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) 
    {
        this.userRepository = userRepository;       //Assign injected UserRepository to local field
        this.passwordEncoder = passwordEncoder;     //Assign injected PasswordEncoder to local field
    }

    @Override //Overrides the run() method to execute logic at app startup
    public void run(String... args) throws Exception 
    {
        //Only load data if no users exist
        if(userRepository.count() == 0) 
        { 
            System.out.println("Loading initial data...");  //Inform that seeding is in progress

            User admin = User.builder().username("admin").email("admin@example.com")
            .homeAddress("Admin Street, Admin City").password(passwordEncoder.encode("admin123"))
            .role(Role.ADMIN).build();

            User customer01 = User.builder().username("customer01").email("customer01@example.com")
            .homeAddress("Customer Lane, Customer Town").password(passwordEncoder.encode("customer123"))
            .role(Role.CUSTOMER).build();

            User customer02 = User.builder().username("customer02").email("customer02@example.com")
            .homeAddress("Customer Lane, Customer Town").password(passwordEncoder.encode("customer123"))
            .role(Role.CUSTOMER).build();

            User salesClerk = User.builder().username("sales_clerk").email("sales@example.com")
            .homeAddress("Sales Street, Clerk City").password(passwordEncoder.encode("sales123"))
            .role(Role.SALES_CLERK).build();

            User warehouseSupervisor = User.builder().username("warehouse_supervisor").email("warehouse@example.com")
            .homeAddress("Warehouse Street, Supervisor City").password(passwordEncoder.encode("warehouse123"))
            .role(Role.WAREHOUSE_SUPERVISOR).build();

            //Save all user data into DB and print confim success message
            userRepository.save(admin);                 
            userRepository.save(customer01);            
            userRepository.save(customer02);            
            userRepository.save(salesClerk);            
            userRepository.save(warehouseSupervisor);
            System.out.println("Initial data loaded successfully!");
        } 
        
        else    
            System.out.println("Data already exists, skipping load...");    //Skip loading if users exist        
    }
}