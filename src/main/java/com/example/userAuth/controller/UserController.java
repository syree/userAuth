package com.example.userAuth.controller;

import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.exception.ControllerException;
import com.example.userAuth.exception.ServiceLayerException;
import com.example.userAuth.exception.UserIdNotValidException;
import com.example.userAuth.message.ErrorResponseMessage;
import com.example.userAuth.message.SuccessMessage;
import com.example.userAuth.model.User;
import com.example.userAuth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        try {
            ApiResponse<List<User>> apiResponse = new ApiResponse<>();
            List<User> users = userService.getAllUsers();
            apiResponse.setData(users);
            apiResponse.setMessage(SuccessMessage.userFetchedSuccess);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
        } catch (ServiceLayerException e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorFetching);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> addNewUser(@Valid @RequestBody User user) {
        try {
            log.info("In Controller try block");
            ApiResponse<User> apiResponse = new ApiResponse<>();
            User userSaved = userService.addNewUser(user);
            apiResponse.setData(userSaved);
            apiResponse.setMessage(SuccessMessage.userCreatedSuccess);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.CREATED);
        } catch (ServiceLayerException e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorAdding);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@Positive(message = "Please enter a positive User Id.")
                                                         @PathVariable Long userId) {
        try {
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(SuccessMessage.userDeletedSuccess + userId);
            userService.deleteUser(userId);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
        } catch (ServiceLayerException e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorDeleting);
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("updatepassword/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(@Positive(message = "Please enter a positive User Id.")
                                                         @PathVariable Long userId,
                                                     @RequestBody Map<String, @NotEmpty String> payload) {
        try {
            ApiResponse<User> apiResponse = new ApiResponse<>();
            User userSaved = userService.updateUserByPassword(userId, payload.get("password"));
            apiResponse.setData(userSaved);
            apiResponse.setMessage(SuccessMessage.passwordUpdatedSuccess);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.CREATED);
        } catch (ServiceLayerException e) {
            ApiResponse<?> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorUpdating);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody Map<String, @NotNull String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        try {
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            User user = userService.loginUserWithEmailAndPassword(email, password);
            apiResponse.setMessage(SuccessMessage.welcome + user.getFirstName() + " " + user.getLastName());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
        } catch (ServiceLayerException e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorLoggingIn);
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/logout/{userId}")
    public ResponseEntity<ApiResponse<?>> logoutUser(@Positive(message = "Please enter a positive User Id.") @PathVariable Long userId){
        try {
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(SuccessMessage.userLoggedOutSuccess);
            userService.logoutUserWithId(userId);
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
        } catch (ServiceLayerException e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(ErrorResponseMessage.controllerLayerErrorLoggingOut);
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }
}
