package com.xinpay.backend.controller;

import com.xinpay.backend.model.Account;
import com.xinpay.backend.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 👉 Submit a new account
    @PostMapping
    public Account addAccount(@RequestBody Account account) {
        return accountService.saveAccount(account);
    }

    // 👉 Get all accounts by userId
    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUserId(@PathVariable String userId) {
        return accountService.getAccountsByUserId(userId);
    }

 // 👉 Delete a specific account by ID
    @DeleteMapping("/{accountId}")
    public void deleteAccount(@PathVariable String accountId) {
        accountService.deleteAccountById(accountId);
    }

}
