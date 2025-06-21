package com.xinpay.backend.service;

import com.xinpay.backend.model.InrWithdrawRequest;
import com.xinpay.backend.repository.InrWithdrawRequestRepository;
import com.xinpay.backend.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InrWithdrawService {

    @Autowired
    private InrWithdrawRequestRepository withdrawRepo;

    @Autowired
    private BalanceService balanceService;

    // ✅ Save a new withdraw request with timestamp
    public InrWithdrawRequest saveWithdrawRequest(InrWithdrawRequest request) {
        request.setApproved(false); // Default to pending
        //request.setRequestedAt(LocalDateTime.now()); // ✅ Correctly sets the timestamp
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

    // ✅ Admin: Approve withdrawal and deduct from Balance table
    public boolean approveWithdrawal(Long id) {
        Optional<InrWithdrawRequest> optional = withdrawRepo.findById(id);
        if (optional.isPresent()) {
            InrWithdrawRequest request = optional.get();

            double currentBalance = balanceService.getInr(request.getUserId());

            if (currentBalance >= request.getAmount()) {
                // Deduct from Balance table
                balanceService.subtractInr(request.getUserId(), request.getAmount());

                // Mark as approved
                request.setApproved(true);
                withdrawRepo.save(request);

                return true;
            } else {
                throw new RuntimeException("Insufficient INR balance.");
            }
        }
        return false;
    }
}
