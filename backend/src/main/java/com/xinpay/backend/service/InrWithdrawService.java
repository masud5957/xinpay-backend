package com.xinpay.backend.service;

import com.xinpay.backend.model.InrWithdrawRequest;
import com.xinpay.backend.repository.InrWithdrawRequestRepository;
import com.xinpay.backend.service.BalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InrWithdrawService {

    @Autowired
    private InrWithdrawRequestRepository withdrawRepo;

    @Autowired
    private BalanceService balanceService;

    public InrWithdrawRequest saveWithdrawRequest(InrWithdrawRequest request) {
        request.setApproved(false);
        return withdrawRepo.save(request);
    }

    public List<InrWithdrawRequest> getAllWithdrawalsByUser(String userId) {
        return withdrawRepo.findAllByUserIdOrderByIdDesc(userId);
    }

    public List<InrWithdrawRequest> getPendingWithdrawals() {
        return withdrawRepo.findByApprovedFalse();
    }

    public boolean approveWithdrawal(Long id) {
        Optional<InrWithdrawRequest> optional = withdrawRepo.findById(id);
        if (optional.isPresent()) {
            InrWithdrawRequest request = optional.get();
            double currentBalance = balanceService.getInr(request.getUserId());
            if (currentBalance >= request.getAmount()) {
                balanceService.subtractInr(request.getUserId(), request.getAmount());
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
