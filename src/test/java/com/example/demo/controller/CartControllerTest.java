package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Optional;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
public class CartControllerTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private CartController cartController;

    @Test
    public void testAddCartUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<Cart> response = cartController.addTocart(new ModifyCartRequest());
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCartSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(getTestUser());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(getTestItem()));
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(getTestItem().getId());
        request.setQuantity(5);
        request.setUsername(getTestUser().getUsername());
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cart = response.getBody();
        assertEquals(getTestItem().getId(), cart.getItems().get(0).getId());
    }

    @Test
    public void testRemoveFromCart() {
        User user = getTestUser();
        Cart cart = new Cart();
        cart.addItem(getTestItem());
        user.setCart(cart);
        when(userRepository.findByUsername(anyString())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(getTestItem()));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(getTestItem().getId());
        request.setQuantity(1);
        request.setUsername(getTestUser().getUsername());
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Cart cartFromResponse = response.getBody();
        assertEquals(0, cartFromResponse.getItems().size());
    }

    private User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("testPwd");
        user.setCart(new Cart());
        return user;
    }

    private Item getTestItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("orange");
        item.setPrice(new BigDecimal("1.99"));
        item.setDescription("description");
        return item;
    }
}
