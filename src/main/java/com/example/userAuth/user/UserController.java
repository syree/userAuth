package com.example.userAuth.user;

import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.exception.ControllerException;
import com.example.userAuth.exception.ServiceLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<?>> getAllUsers(){
        try {
            ApiResponse<List<User>> apiResponse = new ApiResponse<>();
            List<User> users = userService.getAllUsers();
            apiResponse.setData(users);
            apiResponse.setMessage("All users fetched successfully");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.OK);
        }catch (ServiceLayerException e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            ControllerException ce = new ControllerException("605","Something went wrong in the controller");
            apiResponse.setData(ce);
            apiResponse.setMessage("Something went wrong in the controller");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NO_CONTENT);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> addNewUser(@RequestBody User user){
        try {
            ApiResponse<User> apiResponse = new ApiResponse<>();
            User userSaved = userService.addNewUser(user);
            apiResponse.setData(userSaved);
            apiResponse.setMessage("User created successfully");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.CREATED);
        }catch (ServiceLayerException e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Something went wrong in the controller");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long userId){
        try{
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("User with Id "+userId+" deleted successfully");
            userService.deleteUser(userId);
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.OK);
        }catch (ServiceLayerException e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getMessage());
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("updatepassword/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Long userId,@RequestBody Map<String, String> payload){
        try {
            ApiResponse<?> apiResponse = new ApiResponse<>();
            User userSaved = userService.updateUserByPassword(userId,payload.get("password"));
            apiResponse.setMessage("Password updated successfully.");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.CREATED);
        }catch (ServiceLayerException e){
            ApiResponse<?> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Something went wrong in the controller");
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/login")
    public ResponseEntity<ApiResponse<?>> loginUser(@RequestBody Map<String, String> payload){
        String email = payload.get("email");
        String password = payload.get("password");
        try{
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("User Logged in successfully");
            userService.loginUserWithEmailAndPassword(email,password);
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.OK);
        }catch (ServiceLayerException e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Something went wrong in the controller");
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/logout/{userId}")
    public ResponseEntity<ApiResponse<?>> logoutUser(@PathVariable Long userId){
        System.out.println(userId);
        try{
            ApiResponse<Void> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("User logged out successfully");
            userService.logoutUserWithId(userId);
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.OK);
        }catch (ServiceLayerException e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            apiResponse.setMessage(e.getErrorMessage());
            ControllerException ce = new ControllerException(e.getErrorCode(),e.getErrorMessage());
            return new ResponseEntity<ApiResponse<?>>(apiResponse,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ApiResponse<ControllerException> apiResponse = new ApiResponse<>();
            ControllerException ce = new ControllerException("605","Something went wrong in the controller");
            apiResponse.setMessage("Something went wrong in the controller");
            return new ResponseEntity<ApiResponse<?>>(HttpStatus.BAD_REQUEST);
        }
    }
}
