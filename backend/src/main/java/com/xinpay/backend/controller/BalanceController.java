package com.xinpay.backend.controller;

import com.xinpay.backend.model.Balance;
import com.xinpay.backend.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/balance")
@CrossOrigin(origins = "*")
public class BalanceController {

    @Autowired
    private BalanceRepository balanceRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCombinedBalance(@PathVariable String userId) {
        Balance balance = balanceRepository.findById(userId).orElse(null);
        if (balance == null) {
            balance = new Balance(userId, 0.0, 0.0);
            balanceRepository.save(balance);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("inrBalance", balance.getInrBalance());
        response.put("usdtBalance", balance.getUsdtBalance());

        return ResponseEntity.ok(response);
    }
}
