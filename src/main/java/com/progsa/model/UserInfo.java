package com.progsa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.progsa.Constants.INITIAL_BALANCE;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserInfo {

    @Id
    @Column(name = "email_address", nullable = false, unique = true) // Making email the primary key
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "balance")
    private double balance;

    public UserInfo() {
        // Default constructor
        this.balance = INITIAL_BALANCE; // Initialize balance to 1000
    }

    public UserInfo(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.balance = INITIAL_BALANCE; // Initialize balance to 1000
    }
}
