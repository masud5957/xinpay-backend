package com.xinpay.backend.repository;

import com.xinpay.backend.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {
    // You can add custom query methods if needed
}
