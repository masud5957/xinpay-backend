package com.xinpay.backend.controller;

import com.xinpay.backend.model.InrWithdrawRequest;
import com.xinpay.backend.service.InrWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inr-withdraw")
@CrossOrigin(origins = "*")
public class InrWithdrawController {

    @Autowired
    private InrWithdrawService withdrawService;

    // ✅ User: Submit INR withdraw request
    @PostMapping("/request")
    public ResponseEntity<?> requestWithdraw(@RequestBody InrWithdrawRequest request) {
        InrWithdrawRequest savedRequest = withdrawService.saveWithdrawRequest(request);
        return ResponseEntity.ok(savedRequest);
    }

    // ✅ Admin: View all pending withdrawals
    @GetMapping("/pending")
    public ResponseEntity<List<InrWithdrawRequest>> getPendingWithdrawals() {
        return ResponseEntity.ok(withdrawService.getPendingWithdrawals());
    }

    // ✅ Admin: Approve withdrawal request
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveWithdraw(@PathVariable Long id) {
        boolean approved = withdrawService.approveWithdrawal(id);
        if (approved) {
            return ResponseEntity.ok("Withdrawal approved");
        } else {
            return ResponseEntity.status(404).body("Request not found or insufficient balance");
        }
    }

    // ✅ User: View all withdrawals
    @GetMapping("/all/{userId}")
    public ResponseEntity<List<InrWithdrawRequest>> getAllByUser(@PathVariable String userId) {
        return ResponseEntity.ok(withdrawService.getAllWithdrawalsByUser(userId));
    }
}
