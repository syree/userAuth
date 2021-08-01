package com.example.userAuth.controller;

import com.example.userAuth.exception.ApiResponse;
import com.example.userAuth.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void registerUser() throws Exception {
        User user = new User();
        user.setFirstName("Harsh");
        user.setLastName("Chaudhary");
        user.setEmail("harsh@gmail.com");
        user.setPassword("Harsh@123");
        String jasonRequest = objectMapper.writeValueAsString(user);
        MvcResult mvcResult = mockMvc.perform(post("/register")
                              .contentType(jasonRequest).content(MediaType.APPLICATION_JSON_VALUE))
                              .andExpect(status().isOk())
                              .andReturn();
        String resultContent = mvcResult.getResponse().getContentAsString();
        ResponseEntity<ApiResponse<?>> responseEntity = objectMapper.readValue(resultContent,ResponseEntity.class);
        Assert.assertTrue(responseEntity.getStatusCode() == HttpStatus.OK);
    }
}
