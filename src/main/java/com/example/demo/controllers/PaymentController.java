package com.example.demo.controllers;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.service.PaymentService;
import com.example.demo.repositories.UserRepository;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/payment/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createPaymentOrder(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        try {
            BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
            List<Map<String, Object>> cartItemsRaw = (List<Map<String, Object>>) requestBody.get("cartItems");

            List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId((Integer) item.get("productId"));
                orderItem.setQuantity((Integer) item.get("quantity"));
                BigDecimal pricePerUnit = new BigDecimal(item.get("price").toString());
                orderItem.setPricePerUnit(pricePerUnit);
                orderItem.setTotalPrice(pricePerUnit.multiply(BigDecimal.valueOf((Integer) item.get("quantity"))));
                return orderItem;
            }).collect(Collectors.toList());

            Map<String, Object> razorpayOrderId = paymentService.createOrder(user.getUserId(), totalAmount, cartItems);

            return ResponseEntity.ok(razorpayOrderId);
            
        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Razorpay order: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        // Fetch authenticated user
        User user = (User) request.getAttribute("authenticatedUser");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        try {
            String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
            String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
            String razorpaySignature = (String) requestBody.get("razorpaySignature");
            Integer userId = user.getUserId();

            boolean isVerified = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature, userId);
            if (isVerified) {
                return ResponseEntity.ok("Payment verified successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment: " + e.getMessage());
        }
    }
}
