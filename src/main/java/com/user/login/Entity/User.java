package com.user.login.Entity;
import jakarta.persistence.*;       //JPA annotations for persistence mapping
import lombok.*;                    //Lombok annotations to generate boilerplate code like getters, setters, constructors, and builders

import com.user.login.Enum.Role;

@Entity                     //Marks this class as a JPA entity, mapping it to a table in the database
@Table(name = "customers")  //Specifies the name of the table in the database
@Getter                     //Generates getter methods for all fields
@Setter                     //Generates setter methods for all fields
@NoArgsConstructor          //Generates a no-argument constructor
@AllArgsConstructor         //Generates a constructor with all fields
@Builder                    //Provides a builder pattern for creating instances of this class
public class User {
    @Id                                                                                 //Marks this field as the primary key in the database
    @GeneratedValue(strategy = GenerationType.IDENTITY)                                 //Automatically generates the primary key using the IDENTITY strategy
    @Column(name = "customerId")                                                        //Specifies the column name for the primary key in the database
    private Long customerId;                                                            //Unique identifier for the customer

    @Column(nullable = false, unique = true)                                            //Marks the column as not nullable and unique in the database
    private String username;                                                            //Username of the customer

    @Column(nullable = false, unique = true)                                            //Marks the column as not nullable and unique in the database
    private String email;                                                               //Email address of the customer

    @Column(nullable = false)                                                           //Marks the column as not nullable
    private String homeAddress;                                                         //Home address of the customer

    @Column(nullable = false)                                                           //Marks the column as not nullable
    private String password;                                                            //Password of the customer

    @Enumerated(EnumType.STRING)                                                //Specifies that the order status is an enum and should be stored as a string in the database
    @Column(name = "role", nullable = false)                             //Specifies the column name for order status
    private Role role;                                            //Order status (e.g., PENDING, COMPLETED)
}
