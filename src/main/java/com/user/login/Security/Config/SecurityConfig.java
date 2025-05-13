package com.user.login.Security.Config;                                                                     //Package declaration
import org.springframework.context.annotation.Bean;                                                         //To declare beans in Spring
import org.springframework.context.annotation.Configuration;                                                //Marks this class as a configuration class
import org.springframework.security.authentication.AuthenticationManager;                                   //Authentication manager for auth flow
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder; //Builder for custom authentication manager
import org.springframework.security.config.annotation.web.builders.HttpSecurity;                            //Used to configure HTTP security
import org.springframework.security.core.userdetails.User;                                                  //Spring User class for creating user details
import org.springframework.security.core.userdetails.UserDetailsService;                                    //Interface for user details service
import org.springframework.security.core.userdetails.UsernameNotFoundException;                             //Exception thrown when username not found
import org.springframework.security.crypto.factory.PasswordEncoderFactories;                                //Factory for creating password encoders
import org.springframework.security.crypto.password.PasswordEncoder;                                        //Interface for encoding passwords
import org.springframework.security.web.SecurityFilterChain;                                                //Filter chain for HTTP security configuration
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;                //Filter for username/password authentication
import com.user.login.Repository.UserRepository;                                                            //User repository for user data retrieval
import com.user.login.Security.JWT.JwtAuthenticationFilter;

@Configuration  //Declares this as a configuration class for Spring
public class SecurityConfig 
{
    private final UserRepository userRepository;                    //User repository dependency
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  //JWT filter dependency

    //Constructor to inject dependencies
    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) 
    {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean   //Declares PasswordEncoder as a Spring bean
    public PasswordEncoder passwordEncoder() 
    {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();  //Returns the default password encoder
    }

    @Bean //Declares UserDetailsService as a Spring bean
    public UserDetailsService userDetailsService() 
    {
         //If found, build user details with roles and password
        return username -> userRepository.findByUsername(username).map(user -> User.builder().username(user.getUsername()) 
        .password(user.getPassword()).roles(user.getRole().name()).build()).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean   //Declares AuthenticationManager as a Spring bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception 
    {
        return http.getSharedObject(AuthenticationManagerBuilder.class)         //Retrieve AuthenticationManagerBuilder
            .userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder())  //Set the custom user details service and password encoder  
            .and().build();                                                                 //Build and return the authentication manager
    }

    @Bean //Declares SecurityFilterChain as a Spring bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception 
    {
        http
            .csrf().disable()                                                                                   //Disable CSRF protection (useful for API-based applications)
            .authorizeHttpRequests()                                                                            //Start request authorization configuration
                .requestMatchers("/auth/protected")
                .hasAnyRole("CUSTOMER", "ADMIN", "WAREHOUSE_SUPERVISOR", "SALES_CLERK")                //Restrict access to this endpoint to CUSTOMER and ADMIN roles         
                .anyRequest().permitAll()                                                                       //Permit all other requests
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  //Add JWT filter before the authentication filter

        return http.build();                                                                                    //Return the built security filter chain
    }
}