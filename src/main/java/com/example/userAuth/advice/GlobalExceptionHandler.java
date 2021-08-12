package com.example.userAuth.advice;

import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.exception.ServiceLayerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.info("In GlobalExceptionHandler's methodArgumentNotValid function");
        BindingResult result = methodArgumentNotValidException.getBindingResult();
        try {
            FieldError fieldError = result.getFieldError();
            ApiResponse<?> apiResponse = new ApiResponse();
            apiResponse.setMessage(fieldError.getDefaultMessage());
            log.info("MethodArgumentNotValidException caught : " + fieldError.getDefaultMessage());
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse<?> apiResponse = new ApiResponse();
            apiResponse.setMessage("Something went wrong in the MethodArgumentNotValidException method.");
            log.info("Something went wrong in the MethodArgumentNotValidException method.");
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(ServiceLayerException.class)
    public ResponseEntity<?> serviceLayerException(ServiceLayerException serviceLayerException) {
        log.info("In GlobalExceptionHandler's serviceLayerException function");
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(serviceLayerException.getErrorMessage());
        log.info("Service layer exception caught : " + serviceLayerException.getErrorMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception exception) {
        log.info("In GlobalExceptionHandler's exception function");
        ApiResponse<?> apiResponse = new ApiResponse();
        apiResponse.setMessage(exception.getMessage());
        log.info("Java's Exception caught : " + exception.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
