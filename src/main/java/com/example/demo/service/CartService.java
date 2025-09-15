package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repositories.CartRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;

@Service
public class CartService {

	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserRepository userrepository;

	@Autowired
	ProductRepository productRepository;

	public void addToCart(int userId,int productId,int quantity) {

		User user=userrepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
		
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
		
		Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);
		if (existingItem.isPresent()) {
			CartItem cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartRepository.save(cartItem);
		} else {
			CartItem newItem = new CartItem(user, product, quantity);
			cartRepository.save(newItem);
		}
	}
}
