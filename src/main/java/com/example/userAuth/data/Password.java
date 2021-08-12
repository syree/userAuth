package com.example.userAuth.data;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class Password {
    @NotEmpty(message = "Password should not be empty.")
    @Size(min = 2, message = "Minimum length of password should be 2.")
    private String password;
    public Password() {
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Password(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Password{" +
                "password='" + password + '\'' +
                '}';
    }
}
