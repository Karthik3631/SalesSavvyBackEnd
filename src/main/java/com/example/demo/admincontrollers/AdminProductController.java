package com.example.demo.admincontrollers;

import com.example.demo.entity.Product;
import com.example.demo.adminservices.AdminProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin/products")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminProductController {

	private final AdminProductService adminProductService;

	public AdminProductController(AdminProductService adminProductService) {
		this.adminProductService = adminProductService;
	}

	@PostMapping("/add")
	public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> productRequest) {
		try {
			String name = (String) productRequest.get("name");
			String description = (String) productRequest.get("description");
			Double price = Double.valueOf(String.valueOf(productRequest.get("price")));
			Integer stock = Integer.valueOf(String.valueOf(productRequest.get("stock")));
			Integer categoryId = Integer.valueOf(String.valueOf(productRequest.get("categoryId")));
			String imageUrl = (String) productRequest.get("imageUrl");

			Product addedProduct = adminProductService.addProductWithImage(
					name, description, price, stock, categoryId, imageUrl
					);
			return ResponseEntity.status(HttpStatus.CREATED).body(
					Map.of("message", "Product added successfully", "product", addedProduct));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace(); 
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteProduct(@RequestBody Map<String, Integer> requestBody) {
		try {
			Integer productId = requestBody.get("productId");
			adminProductService.deleteProduct(productId);
			return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Something went wrong"));
		}
	}
}
