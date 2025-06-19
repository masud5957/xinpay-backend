package com.xinpay.backend.service;

import com.xinpay.backend.model.Balance;
import com.xinpay.backend.model.DepositRequest;
import com.xinpay.backend.repository.BalanceRepository;
import com.xinpay.backend.repository.DepositRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class DepositService {

    @Autowired
    private DepositRequestRepository depositRequestRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    public DepositRequest uploadDeposit(String userId, MultipartFile file, Double amount) throws IOException {
        String originalName = file.getOriginalFilename();
        long size = file.getSize();

        if (originalName == null || originalName.isEmpty() || size == 0) {
            throw new IOException("Invalid file. Name or size is missing.");
        }

        String extension = "";
        if (originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        String fileName = UUID.randomUUID() + extension;
        String uploadDir = System.getProperty("user.home") + File.separator + "xinpay-uploads" + File.separator;
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();

        File destination = new File(uploadDir + fileName);
        file.transferTo(destination);

        DepositRequest deposit = new DepositRequest();
        deposit.setUserId(userId);
        deposit.setImageUrl(fileName);
        deposit.setVerified(false);
        deposit.setAmount(amount);

        return depositRequestRepository.save(deposit);
    }

    public Optional<DepositRequest> getDepositByUserId(String userId) {
        return depositRequestRepository.findTopByUserIdOrderByIdDesc(userId);
    }

    public List<DepositRequest> getAllDepositsByUser(String userId) {
        return depositRequestRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    public boolean verifyDeposit(Long id) {
        Optional<DepositRequest> depositOpt = depositRequestRepository.findById(id);
        if (depositOpt.isPresent()) {
            DepositRequest req = depositOpt.get();
            if (!req.isVerified()) {
                req.setVerified(true);
                depositRequestRepository.save(req);

                // 🔁 Update user's balance
                Balance balance = balanceRepository.findById(req.getUserId())
                        .orElseGet(() -> {
                            Balance newBalance = new Balance();
                            newBalance.setUserId(req.getUserId());
                            newBalance.setInrBalance(0.0);
                            newBalance.setUsdtBalance(0.0);
                            return newBalance;
                        });

                balance.setInrBalance(balance.getInrBalance() + req.getAmount());
                balanceRepository.save(balance);
            }
            return true;
        }
        return false;
    }

    public List<DepositRequest> getPendingDeposits() {
        return depositRequestRepository.findByVerifiedFalse();
    }

    // ✅ Get total verified deposit balance for user
    public double getTotalBalanceByUser(String userId) {
        Balance balance = balanceRepository.findById(userId).orElse(null);
        return balance != null ? balance.getInrBalance() : 0.0;
    }
}
