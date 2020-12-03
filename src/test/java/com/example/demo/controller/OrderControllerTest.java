package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderController orderController;

    @Test
    public void testGetOrdersForUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(getTestUser());
        when(orderRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(getTestOrder()));
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(getTestOrder().getId(), response.getBody().get(0).getId());
    }

    @Test
    public void testGetOrderForUserWhenUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity response = orderController.getOrdersForUser("testUser");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testSubmitOrder() {
        User user = getTestUser();
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(getTestItemList());
        cart.setTotal(new BigDecimal("10.99"));
        user.setCart(cart);
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(getTestOrder());
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private UserOrder getTestOrder() {
        UserOrder order = new UserOrder();
        order.setId(1L);
        order.setUser(getTestUser());
        return order;
    }

    private User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPwd");
        user.setCart(new Cart());
        return user;
    }

    private List<Item> getTestItemList() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("apple");
        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("orange");
        return Arrays.asList(item1, item2);
    }
}
