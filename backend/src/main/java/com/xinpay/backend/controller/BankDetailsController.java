package com.xinpay.backend.controller;

import com.xinpay.backend.model.BankDetails;
import com.xinpay.backend.service.BankDetailsService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bank-details")
@CrossOrigin(origins = "*")  // Allow access from mobile apps
public class BankDetailsController {

    private final BankDetailsService service;

    public BankDetailsController(BankDetailsService service) {
        this.service = service;
    }

    // GET latest bank details
    @GetMapping
    public ResponseEntity<BankDetails> getBankDetails() {
        BankDetails details = service.getBankDetails();
        if (details == null) {
            return ResponseEntity.noContent().build(); // Better than 404 if nothing exists
        }
        return ResponseEntity.ok(details);
    }

    // POST or PUT new or updated details
    @PostMapping("/update")
    public ResponseEntity<BankDetails> updateBankDetails(@RequestBody BankDetails details) {
        if (details.getAccountNumber() == null || details.getIfscCode() == null || details.getAccountHolder() == null) {
            return ResponseEntity.badRequest().build();
        }
        BankDetails updated = service.updateBankDetails(details);
        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/admin/update")
    public ResponseEntity<BankDetails> updateBankDetailsWithQr(
            @RequestParam String accountNumber,
            @RequestParam String ifscCode,
            @RequestParam String accountHolder,
            @RequestPart(required = false) MultipartFile qrFile
    ) {
        try {
            String qrUrl = null;

            if (qrFile != null && !qrFile.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + qrFile.getOriginalFilename();
                Path uploadPath = Paths.get("uploads"); // Ensure this folder exists
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                qrFile.transferTo(filePath);
                qrUrl = "/uploads/" + fileName;
            }

            BankDetails newDetails = new BankDetails(accountNumber, ifscCode, accountHolder, qrUrl);
            return ResponseEntity.ok(service.updateBankDetails(newDetails));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
