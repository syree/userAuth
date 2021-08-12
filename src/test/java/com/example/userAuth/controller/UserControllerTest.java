package com.example.userAuth.controller;

import com.example.userAuth.data.Credentials;
import com.example.userAuth.data.Password;
import com.example.userAuth.data.UpdatePassword;
import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.message.SuccessMessage;
import com.example.userAuth.model.User;
import com.example.userAuth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registerUserSuccessfullyTest() throws Exception {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        String jasonRequest = objectMapper.writeValueAsString(user);
        Mockito.when(userService.addNewUser(user)).thenReturn(user);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                              .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                              .andExpect(status().isCreated())
                              .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("User created successfully.",apiResponse.getMessage());
    }

//
    @Test
    public void registerUserWithEmptyFirstNameTest() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setLastName("Chaudhary");
        user.setEmail("harshchaudhary123@gmail.com");
        user.setPassword("Harsh4232@123");
        user.setLoggedIn(false);
        String jasonRequest = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("First name should not be empty.",apiResponse.getMessage());
    }

    @Test
    public void registerUserWithSizeOfFirstNameLessThanTwoTest() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setFirstName("");
        user.setLastName("Chaudhary");
        user.setEmail("harshchaudhary123@gmail.com");
        user.setPassword("Harsh4232@123");
        user.setLoggedIn(false);
        String jasonRequest = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("Minimum length of first name should be 2",apiResponse.getMessage());
    }


    @Test
    public void registerUserWithInvalidEmailTest() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harshchaudharygmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        String jasonRequest = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("Enter a valid email address",apiResponse.getMessage());
    }

    @Test
    public void getAllUsersSuccessfullyTest() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harshchaudharygmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        List<User> users = new ArrayList<>();
        users.add(user);
        Mockito.when(userService.getAllUsers()).thenReturn(users);
        MvcResult mvcResult = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals(SuccessMessage.userFetchedSuccess,apiResponse.getMessage());
    }

    @Test
    public void deleteUserWithEmptyPasswordTest() throws Exception {
        Long userId = 1L;
        Password password = new Password();
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(delete("/delete/{userId}",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("Password should not be empty.",apiResponse.getMessage());
    }

    @Test
    public void deleteUserWithPasswordHavingSizeLessThanTwoTest() throws Exception {
        Long userId = 1L;
        Password password = new Password();
        password.setPassword("h");
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(delete("/delete/{userId}",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("Minimum length of password should be 2.",apiResponse.getMessage());
    }


    @Test
    public void deleteUserSuccessfullyTest() throws Exception {
        Long userId = 1L;
        Password password = new Password();
        password.setPassword("Harsh@123");
        doNothing().when(userService).deleteUser(userId,password.getPassword());
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(delete("/delete/{userId}",userId)
                .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals(SuccessMessage.userDeletedSuccess + userId,apiResponse.getMessage());
    }

    @Test
    public void updateUserWithEmptyPasswordTest() throws Exception {
        Long userId = 1L;
        UpdatePassword password = new UpdatePassword();
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(put("/updatepassword/{userId}",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals("Password should not be empty.",apiResponse.getMessage());
    }

    @Test
    public void updateUserSuccessfullyTest() throws Exception {
        Long userId = 1L;
        UpdatePassword password = new UpdatePassword();
        password.setOldPassword("Harsh@123");
        password.setNewPassword("Harsh@1234");
        doNothing().when(userService).updateUserByPassword(userId,password.getOldPassword(),password.getNewPassword());
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(put("/updatepassword/{userId}",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals(SuccessMessage.passwordUpdatedSuccess,apiResponse.getMessage());
    }

    @Test
    public void loginUserSuccessfullyTest() throws Exception {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        user.setLoggedIn(false);
        Long userId = 1L;
        Credentials credentials = new Credentials();
        credentials.setEmail("harsh@gmail.com");
        credentials.setPassword("Harsh@123");
        Mockito.when(userService.loginUserWithEmailAndPassword(credentials.getEmail(),credentials.getPassword()))
                .thenReturn(user);
        String jasonRequest = objectMapper.writeValueAsString(credentials);
        MvcResult mvcResult = mockMvc.perform(put("/login",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals(SuccessMessage.welcome + user.getFirstName() + " " + user.getLastName(),apiResponse.getMessage());
    }

    @Test
    public void logoutUserSuccessfullyTest() throws Exception {
        Long userId = 1L;
        Password password = new Password();
        password.setPassword("Harsh@123");
        doNothing().when(userService).logoutUserWithIdAndPassword(userId,password.getPassword());
        String jasonRequest = objectMapper.writeValueAsString(password);
        MvcResult mvcResult = mockMvc.perform(put("/logout/{userId}",userId)
                        .content(jasonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ApiResponse<?> apiResponse = objectMapper.readValue(resultContent,ApiResponse.class);
        Assert.assertEquals(SuccessMessage.userLoggedOutSuccess,apiResponse.getMessage());
    }

}
