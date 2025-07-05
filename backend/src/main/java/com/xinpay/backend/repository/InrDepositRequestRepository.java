package com.xinpay.backend.repository;

import com.xinpay.backend.model.InrDepositRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InrDepositRequestRepository extends JpaRepository<InrDepositRequest, Long> {

    Optional<InrDepositRequest> findTopByUserIdOrderByIdDesc(String userId);

    // Only fetch deposits that are not verified and not rejected
    List<InrDepositRequest> findByVerifiedFalseAndRejectedFalse();

    List<InrDepositRequest> findAllByUserIdOrderByIdDesc(String userId);

    List<InrDepositRequest> findByUserIdAndVerifiedTrue(String userId);

    // Optional: Get all rejected requests
    List<InrDepositRequest> findByRejectedTrue();
}
