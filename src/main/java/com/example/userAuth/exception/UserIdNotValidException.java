package com.example.userAuth.exception;

import org.springframework.stereotype.Component;

@Component
public class UserIdNotValidException extends Exception{
    private String errorMessage;
    public UserIdNotValidException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }
    public UserIdNotValidException(){
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
