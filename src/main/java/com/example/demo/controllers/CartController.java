package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("api/cart")
@CrossOrigin(origins="http://localhost:5173",allowCredentials = "true")
public class CartController {
	
	@Autowired
	CartService cartService;
	
	@Autowired
	UserRepository userRepository;

	@PostMapping("/add")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
	
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		
		cartService.addToCart(user.getUserId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
