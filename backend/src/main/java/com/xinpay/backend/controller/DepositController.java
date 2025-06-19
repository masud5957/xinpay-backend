// ✅ DepositController.java
package com.xinpay.backend.controller;

import com.xinpay.backend.model.DepositRequest;
import com.xinpay.backend.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class DepositController {

    @Autowired
    private DepositService depositService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("userId") String userId,
            @RequestParam("amount") Double amount,
            @RequestPart("file") MultipartFile file) {
        try {
            DepositRequest saved = depositService.uploadDeposit(userId, file, amount);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/deposit/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable String userId) {
        Optional<DepositRequest> deposit = depositService.getDepositByUserId(userId);
        return deposit.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/inr-deposits/pending")
    public ResponseEntity<List<Map<String, Object>>> getPendingDeposits() {
        List<DepositRequest> pending = depositService.getPendingDeposits();
        List<Map<String, Object>> result = new ArrayList<>();

        for (DepositRequest deposit : pending) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", deposit.getId());
            row.put("userId", deposit.getUserId());
            row.put("status", deposit.isVerified() ? "Verified" : "Pending");
            row.put("amount", deposit.getAmount());
            String baseUrl = "http://localhost:8080";
            row.put("screenshotUrl", baseUrl + "/uploads/" + deposit.getImageUrl());

            result.add(row);
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/inr-deposits/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        boolean status = depositService.verifyDeposit(id);
        return status ? ResponseEntity.ok("Verified") : ResponseEntity.status(404).body("Not found");
    }

    @GetMapping("/deposit/all/{userId}")
    public ResponseEntity<List<DepositRequest>> getAll(@PathVariable String userId) {
        return ResponseEntity.ok(depositService.getAllDepositsByUser(userId));
    }
}
