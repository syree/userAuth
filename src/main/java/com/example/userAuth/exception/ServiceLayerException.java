package com.example.userAuth.exception;

import org.springframework.stereotype.Component;

@Component
public class ServiceLayerException extends RuntimeException{
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
    private static final long serialVersionUID = 1L;
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
