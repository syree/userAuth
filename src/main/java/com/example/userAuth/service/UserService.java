package com.example.userAuth.service;

import com.example.userAuth.exception.ServiceLayerException;
import com.example.userAuth.message.ErrorResponseMessage;
import com.example.userAuth.model.User;
import com.example.userAuth.repository.UserRepository;
import com.example.userAuth.validators.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;


    public User addNewUser(User user) {
        log.info("Adding user {}",user);
        if (!passwordValidator.isValidPassword(user.getPassword())) {
            log.error(ErrorResponseMessage.notValidPassword);
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        Optional<User> userByEmail = userRepository.findUserByEmail(user.getEmail());
        if (userByEmail.isPresent()) {
            log.warn(ErrorResponseMessage.emailIsTaken);
            throw new ServiceLayerException(ErrorResponseMessage.emailIsTaken);
        }
        try {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setLoggedIn(false);
            User savedUser = userRepository.save(user);
            log.info("User added  " + savedUser);
            return savedUser;
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorAdding);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorAdding);
        }
    }

    public List<User> getAllUsers() {
        List<User> users;
        try {
             log.info("Fetching all users...");
             users = userRepository.findAll();
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorFetching);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorFetching);
        }
        if (CollectionUtils.isEmpty(users)) {
            log.error("List is empty");
            throw new ServiceLayerException(ErrorResponseMessage.noUsers);
        }
        log.info("Fetched successfully all users : ");
        return users;
    }

    public void deleteUser(Long userId, String password) {
        log.info("Deleting user with Id " + userId);
        Optional<User> userById = userRepository.findById(userId);
        boolean present = userById.isPresent();
        if (!present) {
            log.error(ErrorResponseMessage.noUserExistsWithId + userId + ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        User user = userById.get();
        if (!passwordValidator.isValidPassword(password)) {
            log.warn(password + " is not valid.");
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.error("User " + userId + " entered the wrong password.");
            throw new ServiceLayerException(ErrorResponseMessage.passwordNotMatches);
        }
        try {
            userRepository.deleteById(userId);
            log.info("Deleted user with Id " + userId);
        } catch (Exception e) {
            log.error("Service layer error while deleting user with Id " + userId);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorDeleting);
        }
    }

    @Transactional
    public void updateUserByPassword(Long userId, String oldPassword, String newPassword) {
        log.info("Updating user's password.");
        Optional<User> userById = userRepository.findById(userId);
        boolean present = userById.isPresent();
        if (!present) {
            log.error(ErrorResponseMessage.noUserExistsWithId + userId + ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        User user = userById.get();
        if (!passwordValidator.isValidPassword(oldPassword)) {
            log.warn(oldPassword + " is not valid.");
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (!passwordValidator.isValidPassword(newPassword)) {
            log.warn(newPassword + " is not valid.");
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        boolean loggedIn = user.getLoggedIn();
        if (!loggedIn) {
            log.warn("User with Id " + userId + " is not logged in.");
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Old password is wrong w.r.t to the user Id " + userId);
            throw new ServiceLayerException(ErrorResponseMessage.oldPasswordWrong);
        }
        if (oldPassword.equals(newPassword)) {
            log.warn("Old password matches with new password");
            throw new ServiceLayerException(ErrorResponseMessage.passwordMatches);
        }
        try {
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Updated password successfully.");
        } catch (Exception e) {
            log.error("Service layer error while updating user's password with Id " + userId);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorUpdating);
        }
    }

    @Transactional
    public User loginUserWithEmailAndPassword(String email, String password) {
        log.info("Logging in user with email and password");
        Optional<User> userByEmail = userRepository.findUserByEmail(email);
        boolean present = userByEmail.isPresent();
        if (!present) {
            log.warn(email + " does not exists");
            throw new ServiceLayerException(ErrorResponseMessage.emailDoestNotExists);
        }
        User user = userByEmail.get();
        Long userId = user.getId();
        String userPassword = user.getPassword();
        if (!passwordValidator.isValidPassword(password)) {
            log.warn(password + " is not valid");
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (!bCryptPasswordEncoder.matches(password, userPassword)) {
            log.warn(password + " is incorrect.");
            throw new ServiceLayerException(ErrorResponseMessage.passwordNotMatches);
        }
        try {
            user.setLoggedIn(true);
            userRepository.save(user);
            log.info("Logged in successfully User with Id " + userId);
            return user;
        } catch (Exception e) {
            log.error("Service layer error while logging in user " + " with Id " + userId);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingIn);
        }
    }

    @Transactional
    public void logoutUserWithIdAndPassword(Long userId, String password) {
        log.info("Logging out user with id " + userId);
        Optional<User> userById = userRepository.findById(userId);
        boolean present = userById.isPresent();
        if (!present) {
            log.error(ErrorResponseMessage.noUserExistsWithId + userId+ ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        User user = userById.get();
        boolean loggedIn = user.getLoggedIn();
        if (!loggedIn) {
            log.error("User " + userId+ " is not loggedIn");
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.error("User " + userId + " entered the wrong password.");
            throw new ServiceLayerException(ErrorResponseMessage.passwordNotMatches);
        }
        try {
            user.setLoggedIn(false);
            userRepository.save(user);
            log.info("Logged out successfully User " + userId);
        } catch (Exception e) {
            log.error("Service layer error while logging out user " + " with Id " + userId);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingOut);
        }
    }
}
