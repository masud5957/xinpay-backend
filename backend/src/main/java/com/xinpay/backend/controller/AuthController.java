package com.xinpay.backend.controller;

import com.xinpay.backend.model.User;
import com.xinpay.backend.repository.UserRepository;
import com.xinpay.backend.security.JwtUtil;
import com.xinpay.backend.service.EmailService;

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

    @Autowired
    private EmailService emailService;

    // 🔐 In-memory OTP + user data storage
    private final Map<String, Map<String, String>> tempUserStore = new HashMap<>();

    // ✅ SIGN UP (OTP generation only)
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String fullName = body.get("fullName");

        if (email == null || password == null || fullName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields."));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already exists. Please login."));
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP

        Map<String, String> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("password", password);
        userData.put("otp", otp);
        tempUserStore.put(email, userData);

        try {
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok(Map.of(
                    "message", "OTP sent to your email. Please verify to complete registration."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send OTP email. Please try again."));
        }
    }

    // ✅ VERIFY OTP and CREATE USER
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing email or OTP."));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User already registered."));
        }

        Map<String, String> userData = tempUserStore.get(email);
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No pending signup found. Try signing up again."));
        }

        if (!userData.get("otp").equals(otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid OTP."));
        }

        // ✅ OTP is valid → Save user
        User user = new User();
        user.setEmail(email);
        user.setPassword(userData.get("password")); // TODO: Hash in production
        user.setFullName(userData.get("fullName"));
        user.setVerified(true);
        userRepository.save(user);
        tempUserStore.remove(email);

        String token = jwtUtil.generateToken(email);

        return ResponseEntity.ok(Map.of(
                "message", "OTP verified. Account activated.",
                "token", token,
                "userId", String.valueOf(user.getId())
        ));
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing email or password."));
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User does not exist."));
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Incorrect password."));
        }

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account not verified. Please verify OTP."));
        }

        String token = jwtUtil.generateToken(email);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", String.valueOf(user.getId())
        ));
    }
}
