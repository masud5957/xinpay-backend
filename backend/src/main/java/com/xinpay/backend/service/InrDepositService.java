package com.xinpay.backend.service;

import com.xinpay.backend.model.Balance;
import com.xinpay.backend.model.InrDepositRequest;
import com.xinpay.backend.repository.BalanceRepository;
import com.xinpay.backend.repository.InrDepositRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class InrDepositService {

    @Autowired
    private InrDepositRequestRepository inrDepositRequestRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private NotificationService notificationService;

    public InrDepositRequest uploadDeposit(String userId, MultipartFile file, Double amount) throws IOException {
        String originalName = file.getOriginalFilename();
        long size = file.getSize();
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

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

        InrDepositRequest deposit = new InrDepositRequest();
        deposit.setUserId(userId);
        deposit.setImageUrl(fileName);
        deposit.setVerified(false);
        deposit.setAmount(amount);

        InrDepositRequest savedDeposit = inrDepositRequestRepository.save(deposit);

        // 🔔 Notify user
        notificationService.sendNotification(userId, "Deposit Received", "We have received your INR deposit request of ₹" + amount + ". Please wait up to 1 hour for verification.");

        return savedDeposit;
    }

    public Optional<InrDepositRequest> getDepositByUserId(String userId) {
        return inrDepositRequestRepository.findTopByUserIdOrderByIdDesc(userId);
    }

    public List<InrDepositRequest> getAllDepositsByUser(String userId) {
        return inrDepositRequestRepository.findAllByUserIdOrderByIdDesc(userId);
    }

    public boolean verifyDeposit(Long id) {
        Optional<InrDepositRequest> depositOpt = inrDepositRequestRepository.findById(id);
        if (depositOpt.isPresent()) {
            InrDepositRequest req = depositOpt.get();
            if (!req.isVerified()) {
                req.setVerified(true);
                req.setVerifiedAt(java.time.LocalDateTime.now());
                inrDepositRequestRepository.save(req);

                // 🔁 Update user's INR balance
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

                // 🔔 Notify user
                notificationService.sendNotification(req.getUserId(), "Deposit Verified", "Your INR deposit of ₹" + req.getAmount() + " has been verified and added to your balance.");
            }
            return true;
        }
        return false;
    }

    public List<InrDepositRequest> getPendingDeposits() {
        return inrDepositRequestRepository.findByVerifiedFalse();
    }

    public double getTotalBalanceByUser(String userId) {
        Balance balance = balanceRepository.findById(userId).orElse(null);
        return balance != null ? balance.getInrBalance() : 0.0;
    }
}
