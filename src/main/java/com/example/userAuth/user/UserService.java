package com.example.userAuth.user;

import com.example.userAuth.message.ErrorResponseMessage;
import com.example.userAuth.exception.ServiceLayerException;
import com.example.userAuth.validators.EmailValidator;
import com.example.userAuth.validators.NameValidator;
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
    private EmailValidator emailValidator;

    @Autowired
    private NameValidator nameValidator;

    @Autowired
    private PasswordValidator passwordValidator;

    User addNewUser(User user) {
        log.info("Adding user....");
        if(user == null)
            throw new NullPointerException(ErrorResponseMessage.passwordMatches);
        if (!nameValidator.isValidName(user.getFirstName())) {
            log.error(ErrorResponseMessage.notValidFirstName);
            throw new ServiceLayerException(ErrorResponseMessage.notValidFirstName);
        }
        if (!nameValidator.isValidName(user.getLastName())) {
            log.error(ErrorResponseMessage.notValidLastName);
            throw new ServiceLayerException(ErrorResponseMessage.notValidLastName);
        }
        if (!emailValidator.isValid(user.getEmail())) {
            log.error(ErrorResponseMessage.notValidEmail);
            throw new ServiceLayerException(ErrorResponseMessage.notValidEmail);
        }
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
            log.info("User added");
            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new ServiceLayerException(ErrorResponseMessage.notValidInput + e.getMessage());
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorAdding);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (CollectionUtils.isEmpty(users)) {
            throw new ServiceLayerException(ErrorResponseMessage.noUsers);
        }
        try {
            return users;
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorFetching);
        }
    }

    public void deleteUser(Long userId) {
        boolean b = userRepository.existsById(userId);
        if (!b) {
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorDeleting);
        }
    }
    @Transactional
    public User updateUserByPassword(Long userId, String password) {
        boolean b = userRepository.existsById(userId);
        if (!b) {
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        Optional<User> userbyId = userRepository.findById(userId);
        User user = userbyId.get();
        if (!passwordValidator.isValidPassword(password)) {
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        }
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new ServiceLayerException(ErrorResponseMessage.passwordMatches);
        }
        if (!user.getLoggedIn()) {
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        try {
            userbyId.get().setPassword(bCryptPasswordEncoder.encode(password));
            return user;
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorUpdating);
        }
    }

    @Transactional
    public User loginUserWithEmailAndPassword(String email, String password) {
        Optional<User> userByEmail = userRepository.findUserByEmail(email);
        if (!userByEmail.isPresent())
            throw new ServiceLayerException(ErrorResponseMessage.emailDoestNotExists);
        if (!passwordValidator.isValidPassword(password))
            throw new ServiceLayerException(ErrorResponseMessage.notValidPassword);
        if (!bCryptPasswordEncoder.matches(password, userByEmail.get().getPassword())) {
            throw new ServiceLayerException(ErrorResponseMessage.passwordNotMatches);
        }
        try {
            userByEmail.get().setLoggedIn(true);
            return userByEmail.get();
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingIn);
        }
    }

    @Transactional
    public void logoutUserWithId(Long userId) {
        boolean b = userRepository.existsById(userId);
        if (!b) {
            throw new ServiceLayerException(ErrorResponseMessage.noUserExistsWithId + userId + ".");
        }
        Optional<User> userById = userRepository.findById(userId);
        if (!userById.get().getLoggedIn()) {
            throw new ServiceLayerException(ErrorResponseMessage.userNotLoggedIn);
        }
        try {
            userById.get().setLoggedIn(false);
        } catch (Exception e) {
            throw new ServiceLayerException(ErrorResponseMessage.serviceLayerErrorLoggingOut);
        }
    }

}
