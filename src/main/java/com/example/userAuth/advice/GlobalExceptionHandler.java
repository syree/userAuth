package com.example.userAuth.advice;

import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.exception.UserIdNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException){
        log.info("In MethodArgumentNotValid function");
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(methodArgumentNotValidException.getBindingResult().getFieldError().getDefaultMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolation(ConstraintViolationException constraintViolationException){
        log.info("In constraintViolation function");
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(constraintViolationException.getLocalizedMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserIdNotValidException.class)
    public ResponseEntity<?> userIdNotValid(UserIdNotValidException userIdNotValidException){
        log.info("In userIdNotValid function");
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(userIdNotValidException.getErrorMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
