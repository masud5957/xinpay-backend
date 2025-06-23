package com.xinpay.backend.repository;

import com.xinpay.backend.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    
    // Get all accounts for a specific user
    List<Account> findByUserId(String userId);

    // Delete specific account by its ID (optional since deleteById is built-in)
    void deleteById(String id);
}
