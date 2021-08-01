package com.example.userAuth.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table
public class User {
    public User() {

    }

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            allocationSize = 1,
            sequenceName = "user_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    @Column(nullable = false)
    @NotEmpty(message = "First name should not be empty.")
    @Size(min = 2,message = "Minimum length of first name should be 2")
    private String firstName;
    @Column(nullable = false)
    @NotEmpty(message = "Last name should not be empty.")
    @Size(min = 2,message = "Minimum length of last name should be 2")
    private String lastName;
    @NotEmpty(message = "Password should not be empty.")
    @Size(min = 2,message = "Minimum length of password should be 2")
    @Column(nullable = false)
    private String password;
    @Column(nullable = false,unique = true)
    @NotEmpty(message = "Email should not be empty.")
    @Email(message = "Enter a valid email address")
    private String email;
    @Column()
    private Boolean isLoggedIn;
    public Boolean getLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
