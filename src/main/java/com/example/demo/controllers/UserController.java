package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${security.password.length.min}")
    private Integer minPasswordLength;
    
    @Value("${security.password.length.max}")
    private Integer maxPasswordLength;


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity createUser(@RequestBody CreateUserRequest createUserRequest) {
        logger.info(String.format("New createUser request for user: %s", createUserRequest.getUsername()));
        User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
        user.setCart(cart);
        String password = createUserRequest.getPassword();
        if (password.length() < minPasswordLength || password.length() > maxPasswordLength) {
            logger.error(String.format("Password length %d does not match requirement for user: %s", password.length(), user.getUsername()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Password length must be between %d and %d characters",
                            minPasswordLength, maxPasswordLength));
        }
        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.error(String.format("Password fields do not match for user: %s", createUserRequest.getUsername()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and Confirm Password fields do not match");
        }
        String hashedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
        user.setPassword(hashedPassword);
        try {
            userRepository.save(user);
            logger.info(String.format("Successfully created user: %s", createUserRequest.getUsername()));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error(String.format("Error during createUser for user: %s", createUserRequest.getUsername()), e);
            return ResponseEntity.unprocessableEntity().build();
        }
	}
	
}
