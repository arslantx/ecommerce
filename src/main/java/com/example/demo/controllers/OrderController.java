package com.example.demo.controllers;

import java.util.List;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        logger.info(String.format("New order submit request for user: %s", username));
		User user = userRepository.findByUsername(username);
		if(user == null) {
            logger.error(String.format("No user found with username: %s", username));
			return ResponseEntity.notFound().build();
		}
        UserOrder order = UserOrder.createFromCart(user.getCart());
        try {
            orderRepository.save(order);
            logger.info(String.format("Successfully submitted order for user: %s", username));
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error(String.format("Error submitting order for user: %s", username), e);
            return ResponseEntity.unprocessableEntity().build();
        }
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        logger.info(String.format("New order history request for user: %s", username));
        User user = userRepository.findByUsername(username);
		if(user == null) {
            logger.error(String.format("No user found with username: %s", username));
			return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
