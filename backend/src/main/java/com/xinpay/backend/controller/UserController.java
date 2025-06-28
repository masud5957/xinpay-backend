package com.xinpay.backend.controller;

import com.xinpay.backend.model.User;
import com.xinpay.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUserProfile(@PathVariable Long id) {
        Optional<User> userOptional = userService.getUserById(id);
        return userOptional.orElseThrow(() -> new RuntimeException("User not found"));
    }
}
