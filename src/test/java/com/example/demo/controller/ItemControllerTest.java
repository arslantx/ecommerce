package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
public class ItemControllerTest {
    
    @MockBean
    private ItemRepository itemRepository;

    @Autowired
    private ItemController itemController;

    @Test
    public void testGetItems() {
        List<Item> itemList = getTestItemList();
        when(itemRepository.findAll()).thenReturn(itemList);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemList.size(), response.getBody().size());
        assertEquals(itemList.get(0).getId(), response.getBody().get(0).getId());
    }

    @Test
    public void testGetItemById() {
        Item item = getTestItemList().get(0);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item.getId(), response.getBody().getId());
    }

    @Test
    public void testGetItemsByName() {
        List<Item> itemList = getTestItemList();
        when(itemRepository.findByName(anyString())).thenReturn(Arrays.asList(itemList.get(1)));
        ResponseEntity<List<Item>> response = itemController.getItemsByName("orange");
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(itemList.get(1).getId(), response.getBody().get(0).getId());
    }

    @Test
    public void testFetItemsByNameNotFound() {
        when(itemRepository.findByName(anyString())).thenReturn(null);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("orange");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
