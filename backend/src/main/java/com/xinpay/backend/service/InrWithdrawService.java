package com.xinpay.backend.service;

import com.xinpay.backend.model.InrDepositRequest;
import com.xinpay.backend.model.InrWithdrawRequest;
import com.xinpay.backend.repository.InrDepositRequestRepository;
import com.xinpay.backend.repository.InrWithdrawRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InrWithdrawService {

    @Autowired
    private InrWithdrawRequestRepository withdrawRepo;

    @Autowired
    private InrDepositRequestRepository depositRepo;

    // ✅ Save a new withdraw request
    public InrWithdrawRequest saveWithdrawRequest(InrWithdrawRequest request) {
        request.setApproved(false); // Default to pending
        return withdrawRepo.save(request);
    }

    // ✅ Get all withdrawals by user
    public List<InrWithdrawRequest> getAllWithdrawalsByUser(String userId) {
        return withdrawRepo.findAllByUserIdOrderByIdDesc(userId);
    }

    // ✅ Get pending withdrawals (admin)
    public List<InrWithdrawRequest> getPendingWithdrawals() {
        return withdrawRepo.findByApprovedFalse();
    }

    // ✅ Approve a withdraw request by ID (admin) with balance deduction
    public boolean approveWithdrawal(Long id) {
        Optional<InrWithdrawRequest> optional = withdrawRepo.findById(id);
        if (optional.isPresent()) {
            InrWithdrawRequest request = optional.get();

            // Calculate total verified balance
            List<InrDepositRequest> verifiedDeposits = depositRepo.findByUserIdAndVerifiedTrue(request.getUserId());
            double currentBalance = verifiedDeposits.stream()
                    .mapToDouble(InrDepositRequest::getAmount)
                    .sum();

            if (currentBalance >= request.getAmount()) {
                // Save negative deposit to reflect deduction
                InrDepositRequest deduction = new InrDepositRequest();
                deduction.setUserId(request.getUserId());
                deduction.setAmount(-request.getAmount());
                deduction.setVerified(true);
                deduction.setImageUrl("withdrawal"); // Optional marker
                depositRepo.save(deduction);

                // Approve withdrawal
                request.setApproved(true);
                withdrawRepo.save(request);
                return true;
            } else {
                throw new RuntimeException("Insufficient balance for withdrawal.");
            }
        }
        return false;
    }
}
