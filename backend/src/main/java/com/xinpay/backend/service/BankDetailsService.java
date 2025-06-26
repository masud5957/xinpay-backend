package com.xinpay.backend.service;

import com.xinpay.backend.model.BankDetails;
import com.xinpay.backend.repository.BankDetailsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankDetailsService {

    private final BankDetailsRepository repository;

    public BankDetailsService(BankDetailsRepository repository) {
        this.repository = repository;
    }

    // Return latest (or first if only one exists)
    public BankDetails getBankDetails() {
        return repository.findAll()
                .stream()
                .reduce((first, second) -> second) // get last if more than one
                .orElse(null);
    }

    // Replace or insert
    public BankDetails updateBankDetails(BankDetails newDetails) {
        List<BankDetails> existingList = repository.findAll();

        BankDetails bankDetails;
        if (existingList.isEmpty()) {
            bankDetails = new BankDetails();
        } else {
            bankDetails = existingList.get(0);  // update existing
        }

        bankDetails.setAccountNumber(newDetails.getAccountNumber());
        bankDetails.setIfscCode(newDetails.getIfscCode());
        bankDetails.setAccountHolder(newDetails.getAccountHolder());
        bankDetails.setQrImageUrl(newDetails.getQrImageUrl());

        return repository.save(bankDetails);
    }
}
