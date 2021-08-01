package com.example.userAuth.exception;

import org.springframework.stereotype.Component;

@Component
public class ServiceLayerException extends Exception{

    private String errorMessage;

    public ServiceLayerException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public ServiceLayerException() {
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
