package com.xinpay.backend.controller;

import com.xinpay.backend.model.InrDepositRequest;
import com.xinpay.backend.service.InrDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/inr-deposits")
@CrossOrigin(origins = "*")
public class InrDepositController {

    @Autowired
    private InrDepositService inrDepositService;

    // ✅ Upload INR deposit
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("userId") String userId,
            @RequestParam("amount") Double amount,
            @RequestPart("file") MultipartFile file) {
        try {
            InrDepositRequest saved = inrDepositService.uploadDeposit(userId, file, amount);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    // ✅ Get latest deposit status for user
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable String userId) {
        Optional<InrDepositRequest> deposit = inrDepositService.getDepositByUserId(userId);
        return deposit.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Admin: Get pending INR deposits
    @GetMapping("/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingDeposits() {
        List<InrDepositRequest> pending = inrDepositService.getPendingDeposits();
        List<Map<String, Object>> result = new ArrayList<>();

        String baseUrl = "https://xinpay-backend.onrender.com"; // Replace with your production domain

        for (InrDepositRequest deposit : pending) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", deposit.getId());
            row.put("userId", deposit.getUserId());
            row.put("status", deposit.isVerified() ? "Verified" : "Pending");
            row.put("amount", deposit.getAmount());
            row.put("screenshotUrl", baseUrl + "/uploads/" + deposit.getImageUrl());
            result.add(row);
        }

        return ResponseEntity.ok(result);
    }

    // ✅ Admin: Verify deposit
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        boolean status = inrDepositService.verifyDeposit(id);
        return status ? ResponseEntity.ok("Verified") : ResponseEntity.status(404).body("Not found");
    }

    // ✅ User: Get all deposit history
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<InrDepositRequest>> getAll(@PathVariable String userId) {
        return ResponseEntity.ok(inrDepositService.getAllDepositsByUser(userId));
    }

    // ✅ User: Get current INR balance
    @GetMapping("/balance/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable String userId) {
        double total = inrDepositService.getTotalBalanceByUser(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("totalBalance", total);
        return ResponseEntity.ok(response);
    }
}
