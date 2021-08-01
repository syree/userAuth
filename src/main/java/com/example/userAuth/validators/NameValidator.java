package com.example.userAuth.validators;

import org.springframework.stereotype.Component;

@Component
public class NameValidator {
    public  boolean isValidName(String firstName) {
        return firstName.matches("[A-Z][a-z]*");
    }
}
