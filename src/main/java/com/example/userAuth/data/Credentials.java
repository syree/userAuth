package com.example.userAuth.data;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class Credentials {
    @NotEmpty(message = "Email should not be empty.")
    @Email(message = "Enter a valid email address.")
    private String email;
    @NotEmpty(message = "Password should not be empty.")
    @Size(min = 2, message = "Minimum length of password should be 2.")
    private String password;

    public Credentials() {
    }

    public Credentials(String password) {
        this.password = password;
    }

    public Credentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
