package com.xinpay.backend.controller;

import com.xinpay.backend.model.User;
import com.xinpay.backend.repository.UserRepository;
import com.xinpay.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ SIGN UP using Map
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String fullName = body.get("fullName");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body("Missing required fields.");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists. Please login.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    // ✅ LOGIN using Map
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body("Missing email or password.");
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist. Please sign up.");
        }

        if (!existingUser.get().getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password.");
        }

        String token = jwtUtil.generateToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
