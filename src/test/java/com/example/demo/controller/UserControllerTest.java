package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    
    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserController userController;

    @Test
    public void testFindById() {
        User user = getTestUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getPassword(), response.getBody().getPassword());
    }

    @Test
    public void testFindByUserName() {
        User user = getTestUser();
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("testUser");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user.getId(), response.getBody().getId());
        assertEquals(user.getUsername(), response.getBody().getUsername());
        assertEquals(user.getPassword(), response.getBody().getPassword());
    }

    @Test
    public void testCreateUserInvalidPasswordLength() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword("abc");
        ResponseEntity response = userController.createUser(userRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUserPasswordMismatch() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setPassword("abcefghi");
        userRequest.setConfirmPassword("abcdefghijklm");
        ResponseEntity response = userController.createUser(userRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateUserSuccess() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("abcefghi");
        userRequest.setConfirmPassword("abcefghi");
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("testHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(getTestUser());
        ResponseEntity response = userController.createUser(userRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPwd");
        return user;
    }
}
