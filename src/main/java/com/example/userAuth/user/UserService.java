package com.example.userAuth.user;

import com.example.userAuth.exception.ServiceLayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.rmi.server.ServerCloneException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public static boolean isValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
    public static boolean isValidfirstName(String firstName ) {
        return firstName.matches( "[A-Z][a-z]*" );
    }
    // validate last name
    public static boolean isValidlastName(String lastName ) {
        return lastName.matches( "[A-Z][a-z]*" );
    }
    public static boolean isValidPassword(String password)
    {

        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern p = Pattern.compile(regex);
        if (password == null) {
            return false;
        }
        Matcher m = p.matcher(password);
        return m.matches();
    }
    User addNewUser(User user){
        if(!isValidfirstName(user.getFirstName()) || !isValidlastName(user.getLastName())) {
            throw new ServiceLayerException("601", "Please enter valid name");
        }
        if(!isValid(user.getEmail())) {
            throw new ServiceLayerException("602", "Please enter valid email");
        }
        if(!isValidPassword(user.getPassword())){
            throw new ServiceLayerException("603", "Please enter valid password");
        }
        Optional<User> userByEmail = userRepository.findUserByEmail(user.getEmail());
        if(userByEmail.isPresent())
            throw  new ServiceLayerException("604","Email is taken");
        try{
            return userRepository.save(user);
        }catch (IllegalArgumentException e) {
            throw new ServiceLayerException("605","Enter valid input "+e.getMessage());
        }catch (Exception e) {
            throw new ServiceLayerException("606","Something went wrong in the service layer "+e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new ServiceLayerException("607","No users currently.");
        }
        try{
            return users;
        }catch (Exception e){
            throw  new ServiceLayerException("608","Something went wrong in the service layer while fetching.");
        }
    }

    public void deleteUser(Long userId){
        boolean b = userRepository.existsById(userId);
        if(!b){
            throw new ServiceLayerException("609","No user exists with Id "+userId+".");
        }
        try{
            userRepository.deleteById(userId);
        }catch (Exception e){
            throw new ServiceLayerException("610","Something went wrong while deleting user in the service layer");
        }
    }

//    public User getUserByEmailAndPassword(String email, String password) {
//        Optional<User> userByEmail = userRepository.findUserByEmail(email);
//        if (userByEmail.isPresent())
//            if (userByEmail.get().getPassword().equals(password)) {
//                return userByEmail.get();
//            }
//        }
}
