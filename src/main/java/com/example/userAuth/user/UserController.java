package com.example.userAuth.user;

import com.example.userAuth.exception.ControllerException;
import com.example.userAuth.exception.ServiceLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<List<User>>(users,HttpStatus.OK);
        }catch (ServiceLayerException e){
            ControllerException ce = new ControllerException(e.getErrorCode(),e.getErrorMessage());
            return new ResponseEntity<ControllerException>(ce,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ControllerException ce = new ControllerException("605","Something went wrong in the controller");
            return new ResponseEntity<ControllerException>(ce,HttpStatus.NO_CONTENT);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> addNewUser(@RequestBody User user){
        try {
            User userSaved = userService.addNewUser(user);
            return new ResponseEntity<User>(userSaved,HttpStatus.CREATED);
        }catch (ServiceLayerException e){
            ControllerException ce = new ControllerException(e.getErrorCode(),e.getErrorMessage());
            return new ResponseEntity<ControllerException>(ce,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            ControllerException ce = new ControllerException("605","Something went wrong in the controller");
            return new ResponseEntity<ControllerException>(ce,HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId){
        try{
            userService.deleteUser(userId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }catch (ServiceLayerException e){
            ControllerException ce = new ControllerException(e.getErrorCode(),e.getErrorMessage());
            return new ResponseEntity<ControllerException>(ce,HttpStatus.NOT_FOUND);
        }catch (Exception e){
            ControllerException ce = new ControllerException("605","Something went wrong in the controller");
            return new ResponseEntity<ControllerException>(HttpStatus.BAD_REQUEST);
        }
    }
}
