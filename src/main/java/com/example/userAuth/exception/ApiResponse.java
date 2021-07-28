package com.example.userAuth.exception;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private String message;
    public ApiResponse(String status, T data, String message) {
        this.data = data;
        this.message = message;
    }
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ApiResponse() {

    }
}

