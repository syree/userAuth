package com.example.userAuth.service;

import com.example.userAuth.exception.ServiceLayerException;
import com.example.userAuth.message.ErrorResponseMessage;
import com.example.userAuth.model.User;
import com.example.userAuth.repository.UserRepository;
import com.example.userAuth.validators.PasswordValidator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Stubber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @InjectMocks
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordValidator passwordValidator;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test()
    void addNewUserWithInvalidPasswordTest() throws Exception{
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Mockito.when(passwordValidator.isValidPassword(anyString())).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.addNewUser(user));
        assertEquals(ErrorResponseMessage.notValidPassword,serviceLayerException.getErrorMessage());
    }

    @Test
    void addNewUserWithExistingEmailTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Mockito.when(passwordValidator.isValidPassword(anyString())).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.addNewUser(user));
        assertEquals(ErrorResponseMessage.emailIsTaken,serviceLayerException.getErrorMessage());
    }

    @Test
    void addNewUserWithUnknownExceptionTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Mockito.when(passwordValidator.isValidPassword(anyString())).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenThrow(ServiceLayerException.class);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.addNewUser(user));
        assertEquals(ErrorResponseMessage.serviceLayerErrorAdding,serviceLayerException.getErrorMessage());
    }

    @Test
    void addNewUserSuccessfullyTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Mockito.when(passwordValidator.isValidPassword(anyString())).thenReturn(true);
        Mockito.when(userRepository.findUserByEmail(user.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        User addedUser = userService.addNewUser(user);
        Assert.assertEquals(addedUser, user);
    }

    @Test
    void getAllUsersWithNoUsersTest() {
        Mockito.when(userRepository.findAll()).thenReturn(null);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.getAllUsers());
        assertEquals(ErrorResponseMessage.noUsers,serviceLayerException.getErrorMessage());
    }

    @Test
    void getAllUsersWhereUnknownExceptionIsCaughtTest() {
        User user1 = new User();
        user1.setFirstName("Harsh");
        user1.setLastName("Chaudhary");
        user1.setEmail("harsh@gmail.com");
        user1.setPassword("Harsh@123");
        user1.setLoggedIn(false);
        User user2 = new User();
        user2.setFirstName("Kartik");
        user2.setLastName("Panday");
        user2.setEmail("kartik@gmail.com");
        user2.setPassword("Kartik@123");
        user2.setLoggedIn(false);
        List<User> expected = new ArrayList<>();
        expected.add(user1);
        expected.add(user2);
        Mockito.when(userRepository.findAll()).thenThrow(ServiceLayerException.class);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.getAllUsers());
        Assert.assertEquals(ErrorResponseMessage.serviceLayerErrorFetching,serviceLayerException.getErrorMessage());
    }

    @Test
    void getAllUsersSuccessfullyTest() {
        User user1 = new User();
        user1.setFirstName("Harsh");
        user1.setLastName("Chaudhary");
        user1.setEmail("harsh@gmail.com");
        user1.setPassword("Harsh@123");
        user1.setLoggedIn(false);
        User user2 = new User();
        user2.setFirstName("Kartik");
        user2.setLastName("Panday");
        user2.setEmail("kartik@gmail.com");
        user2.setPassword("Kartik@123");
        user2.setLoggedIn(false);
        List<User> expected = new ArrayList<>();
        expected.add(user1);
        expected.add(user2);
        Mockito.when(userRepository.findAll()).thenReturn(expected);
        List<User> actual = userService.getAllUsers();
        Assert.assertEquals(actual,expected);
    }

    @Test
    void deleteUserWithUnknownIdTest() {
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.deleteUser(userId, password));
        assertEquals(ErrorResponseMessage.noUserExistsWithId+userId+".",serviceLayerException.getErrorMessage());
    }

    @Test()
    void deleteUserWithInvalidPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.deleteUser(userId, password));
        assertEquals(ErrorResponseMessage.notValidPassword,serviceLayerException.getErrorMessage());
    }

    @Test()
    void deleteUserWithIncorrectPasswordProvidedTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String password = "Harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(user.getPassword(),password)).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.deleteUser(userId, password));
        assertEquals(ErrorResponseMessage.passwordNotMatches,serviceLayerException.getErrorMessage());
    }

    @Test()
    void deleteUserWithUnknownExceptionTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(user.getPassword(),password)).thenReturn(true);
        doThrow(new ServiceLayerException()).when(userRepository).deleteById(userId);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.deleteUser(userId, password));
        assertEquals(ErrorResponseMessage.serviceLayerErrorDeleting,serviceLayerException.getErrorMessage());
        verify(userRepository,times(1)).deleteById(userId);
    }

    @Test()
    void deleteUserSuccessfullyTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(user.getPassword(),password)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);
        userService.deleteUser(userId,password);
        verify(userRepository,times(1)).deleteById(userId);
    }

    @Test()
    void updateUserPasswordWithUnknownUserIdTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "Harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(serviceLayerException.getErrorMessage(),ErrorResponseMessage.noUserExistsWithId+userId+".");
    }

    @Test()
    void updateUserPasswordWithInvalidOldPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String oldPassword = "harsh@123";
        String newPassword = "Harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(ErrorResponseMessage.notValidPassword,serviceLayerException.getErrorMessage());
    }

    @Test()
    void updateUserPasswordWithInvalidNewPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(ErrorResponseMessage.notValidPassword,serviceLayerException.getErrorMessage());
    }

    @Test()
    void updateUserPasswordWithUserNotLoggedInTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(true);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(ErrorResponseMessage.userNotLoggedIn,serviceLayerException.getErrorMessage());
    }

    @Test()
    void updateUserPasswordWithIncorrectPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword,user.getPassword())).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(ErrorResponseMessage.oldPasswordWrong,serviceLayerException.getErrorMessage());
    }

    @Test()
    void updateUserPasswordWithNewPasswordSameAsOldPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword,user.getPassword())).thenReturn(true);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword,newPassword));
        assertEquals(ErrorResponseMessage.passwordMatches,serviceLayerException.getErrorMessage());
    }

    @Test()
    void updateUserPasswordWithUnknownExceptionTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "Harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenThrow(ServiceLayerException.class);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.updateUserByPassword(userId, oldPassword, newPassword));
        assertEquals(ErrorResponseMessage.serviceLayerErrorUpdating,serviceLayerException.getErrorMessage());
        verify(userRepository,times(1)).save(user);
    }

    @Test()
    void updateUserPasswordSuccessfullyTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String oldPassword = "Harsh@123";
        String newPassword = "Harsh@1234";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(oldPassword)).thenReturn(true);
        Mockito.when(passwordValidator.isValidPassword(newPassword)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(oldPassword,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.updateUserByPassword(userId,oldPassword,newPassword);
        verify(userRepository,times(1)).save(user);
    }


    @Test()
    void loginUserWithEmailAndPasswordWhereEmailDoesNotExistsTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String email = "harsh@gmail.com";
        String password = "Harsh@123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.loginUserWithEmailAndPassword(email,password));
        assertEquals(ErrorResponseMessage.emailDoestNotExists,serviceLayerException.getErrorMessage());
    }

    @Test()
    void loginUserWithEmailAndPasswordWherePasswordIsInvalidTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String email = "harsh@gmail.com";
        String password = "Harsh@123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.loginUserWithEmailAndPassword(email,password));
        assertEquals(ErrorResponseMessage.notValidPassword,serviceLayerException.getErrorMessage());
    }

    @Test()
    void loginUserWithEmailAndPasswordWherePasswordIsIncorrectTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String email = "harsh@gmail.com";
        String password = "Harsh@123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.loginUserWithEmailAndPassword(email,password));
        assertEquals(ErrorResponseMessage.passwordNotMatches,serviceLayerException.getErrorMessage());
    }

    @Test()
    void loginUserWithEmailAndPasswordWhereUnknownExceptionIsCaught() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String email = "harsh@gmail.com";
        String password = "Harsh@123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenThrow(ServiceLayerException.class);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.loginUserWithEmailAndPassword(email,password));
        assertEquals(ErrorResponseMessage.serviceLayerErrorLoggingIn,serviceLayerException.getErrorMessage());
    }


    @Test()
    void loginUserWithEmailAndPasswordSuccessfully() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String email = "harsh@gmail.com";
        String password = "Harsh@123";
        Mockito.when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(passwordValidator.isValidPassword(password)).thenReturn(true);
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        User savedUser = userService.loginUserWithEmailAndPassword(email, password);
        assertEquals(user,savedUser);
    }

    @Test
    void logoutUserWithIdAndPasswordTest() {
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.logoutUserWithIdAndPassword(userId, password));
        assertEquals(ErrorResponseMessage.noUserExistsWithId+userId+".",serviceLayerException.getErrorMessage());
    }

    @Test()
    void logoutUserWithIdAndPasswordWithUserNotLoggedInTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.logoutUserWithIdAndPassword(userId, password));
        assertEquals(ErrorResponseMessage.userNotLoggedIn,serviceLayerException.getErrorMessage());
    }

    @Test()
    void logoutUserWithIdAndPasswordWithIncorrectPasswordTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(false);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.logoutUserWithIdAndPassword(userId,password));
        assertEquals(ErrorResponseMessage.passwordNotMatches,serviceLayerException.getErrorMessage());
    }

    @Test()
    void logoutUserWithIdAndPasswordWhereUnknownExceptionIsCaughtTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenThrow(ServiceLayerException.class);
        ServiceLayerException serviceLayerException = assertThrows(ServiceLayerException.class, () -> userService.logoutUserWithIdAndPassword(userId,password));
        assertEquals(ErrorResponseMessage.serviceLayerErrorLoggingOut,serviceLayerException.getErrorMessage());
    }

    @Test()
    void logoutUserWithIdAndPasswordSuccessfullyTest() {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(true);
        Long userId = 1L;
        String password = "Harsh@123";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bCryptPasswordEncoder.matches(password,user.getPassword())).thenReturn(true);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        userService.logoutUserWithIdAndPassword(userId,password);
        verify(userRepository,times(1)).save(user);
    }
}