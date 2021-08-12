package com.example.userAuth.controller;

import com.example.userAuth.data.Credentials;
import com.example.userAuth.data.Password;
import com.example.userAuth.data.UpdatePassword;
import com.example.userAuth.exception.ApiResponse;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Validated
@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers() {
        log.info("In the controller's getAllUsers() method.");
        ApiResponse<List<User>> apiResponse = new ApiResponse<>();
        List<User> users = userService.getAllUsers();
        apiResponse.setData(users);
        apiResponse.setMessage(SuccessMessage.userFetchedSuccess);
        log.info("All users fetched successfully");
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> addNewUser(@Valid @RequestBody User user) {
        log.info("In the controller's addNewUser() method.");
        ApiResponse<User> apiResponse = new ApiResponse<>();
        User userSaved = userService.addNewUser(user);
        apiResponse.setData(userSaved);
        apiResponse.setMessage(SuccessMessage.userCreatedSuccess);
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId,
                                                     @Valid @RequestBody Password password) {
        log.info("In the controller's deleteUser() method.");
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(SuccessMessage.userDeletedSuccess + userId);
        userService.deleteUser(userId,password.getPassword());
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/updatepassword/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Long userId,
                                                     @Valid @RequestBody UpdatePassword password) {
        log.info("In the controller's updateUser() method.");
        ApiResponse<User> apiResponse = new ApiResponse<>();
        userService.updateUserByPassword(userId, password.getOldPassword(),password.getNewPassword());
        apiResponse.setMessage(SuccessMessage.passwordUpdatedSuccess);
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@Valid @RequestBody Credentials credentials) {
        log.info("In the controller's loginUser() method.");
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        User user = userService.loginUserWithEmailAndPassword(credentials.getEmail(), credentials.getPassword());
        apiResponse.setMessage(SuccessMessage.welcome + user.getFirstName() + " " + user.getLastName());
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/logout/{userId}")
    public ResponseEntity<ApiResponse<?>> logoutUser(@PathVariable Long userId,@Valid @RequestBody Password password) {
        log.info("In the controller's logoutUser() method.");
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(SuccessMessage.userLoggedOutSuccess);
        userService.logoutUserWithIdAndPassword(userId,password.getPassword());
        return new ResponseEntity<ApiResponse<?>>(apiResponse, HttpStatus.OK);
    }
}
