// ✅ DepositService.java
package com.xinpay.backend.service;

import com.xinpay.backend.model.DepositRequest;
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
        Optional<DepositRequest> deposit = depositRequestRepository.findById(id);
        if (deposit.isPresent()) {
            DepositRequest req = deposit.get();
            req.setVerified(true);
            depositRequestRepository.save(req);
            return true;
        }
        return false;
    }

    public List<DepositRequest> getPendingDeposits() {
        return depositRequestRepository.findByVerifiedFalse();
    }
}