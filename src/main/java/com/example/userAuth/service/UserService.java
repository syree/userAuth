package com.example.userAuth.service;

import com.example.userAuth.message.ErrorResponseMessage;
import com.example.userAuth.exception.ServiceLayerException;
import com.example.userAuth.repository.UserRepository;
import com.example.userAuth.model.User;
import com.example.userAuth.validators.EmailValidator;
import com.example.userAuth.validators.NameValidator;
import com.example.userAuth.validators.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import javax.validation.constraints.Positive;
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
    private EmailValidator emailValidator;

    @Autowired
    private NameValidator nameValidator;

    @Autowired
    private PasswordValidator passwordValidator;

    public User addNewUser(User user) throws ServiceLayerException{
        log.info("Adding user....");
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
            log.info("User added with Id "+user.getId());
            return userRepository.save(user);
        }catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorAdding);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorAdding);
        }
    }

    public List<User> getAllUsers() throws ServiceLayerException{
        log.info("Fetching all users...");
        List<User> users = userRepository.findAll();
        if (CollectionUtils.isEmpty(users)) {
            log.error("List is empty");
            throw new ServiceLayerException(ErrorResponseMessage.noUsers);
        }
        try {
            log.info("Fetched successfully all users : ");
            return users;
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorFetching);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorFetching);
        }
    }

    public void deleteUser(Long userId) throws ServiceLayerException{
        log.info("Deleting user with Id "+userId);
        boolean b = userRepository.existsById(userId);
        if (!b) {
            log.error(ErrorResponseMessage.noUserExistsWithId + userId + ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        try {
            log.info("Deleted user with Id "+userId);
            userRepository.deleteById(userId);
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorDeleting);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorDeleting);
        }
    }
    @Transactional
    public User updateUserByPassword(Long userId, String password) throws ServiceLayerException{
        Optional<User> userById = userRepository.findById(userId);
        boolean present = userById.isPresent();
        if(!present){
            log.error(ErrorResponseMessage.noUserExistsWithId + userId + ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        User user = userById.get();
        if (!passwordValidator.isValidPassword(password)) {
            log.warn(ErrorResponseMessage.notValidPassword);
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.warn(ErrorResponseMessage.passwordMatches);
            throw new ServiceLayerException(ErrorResponseMessage.passwordMatches);
        }
        boolean loggedIn = user.getLoggedIn();
        if (!loggedIn) {
            log.warn(ErrorResponseMessage.userNotLoggedIn);
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        try {
            log.info("Updated password successfully.");
            userById.get().setPassword(bCryptPasswordEncoder.encode(password));
            return user;
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorUpdating);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorUpdating);
        }
    }

    @Transactional
    public User loginUserWithEmailAndPassword(String email, String password) throws ServiceLayerException{
        Optional<User> userByEmail = userRepository.findUserByEmail(email);
        boolean present = userByEmail.isPresent();
        if (!present) {
            log.warn(email+" does not exists");
            throw new ServiceLayerException(ErrorResponseMessage.emailDoestNotExists);
        }
        if (!passwordValidator.isValidPassword(password)) {
            log.warn(password+" is not valid");
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (!bCryptPasswordEncoder.matches(password, userByEmail.get().getPassword())) {
            log.warn(password+" doest not match with "+userByEmail.get().getPassword());
            throw new ServiceLayerException(ErrorResponseMessage.passwordNotMatches);
        }
        try {
            log.info("Logged in successfully User with Id "+userByEmail.get().getId());
            userByEmail.get().setLoggedIn(true);
            return userByEmail.get();
        } catch (Exception e) {
            log.error(ErrorResponseMessage.serviceLayerErrorLoggingIn);
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingIn);
        }
    }

    @Transactional
    public void logoutUserWithId(Long userId) throws ServiceLayerException{
        Optional<User> userById = userRepository.findById(userId);
        boolean present = userById.isPresent();
        if(!present){
            log.error(ErrorResponseMessage.noUserExistsWithId + userId + ".");
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        boolean loggedIn = userById.get().getLoggedIn();
        if (!loggedIn) {
            log.error("User "+userId + " is not loggedIn");
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        try {
            log.info("Logged out successfully User "+userId);
            userById.get().setLoggedIn(false);
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingOut);
        }
    }
}
