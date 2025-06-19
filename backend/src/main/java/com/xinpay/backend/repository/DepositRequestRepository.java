package com.xinpay.backend.repository;

import com.xinpay.backend.model.DepositRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {
    Optional<DepositRequest> findTopByUserIdOrderByIdDesc(String userId);
    List<DepositRequest> findByVerifiedFalse();
    List<DepositRequest> findAllByUserIdOrderByIdDesc(String userId);
    List<DepositRequest> findByUserIdAndVerifiedTrue(String userId); // ✅ New for balance
}
