package com.xinpay.backend.controller;

import com.xinpay.backend.model.BankDetails;
import com.xinpay.backend.service.BankDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-details")
@CrossOrigin(origins = "*")
public class BankDetailsController {

    private final BankDetailsService service;

    public BankDetailsController(BankDetailsService service) {
        this.service = service;
    }

    // ✅ GET current bank details
    @GetMapping
    public ResponseEntity<BankDetails> getBankDetails() {
        BankDetails details = service.getBankDetails();
        return (details == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(details);
    }

    // ✅ Update via mobile/JSON body
    @PostMapping("/update")
    public ResponseEntity<BankDetails> updateBankDetails(@RequestBody BankDetails details) {
        if (details.getAccountNumber() == null || details.getIfscCode() == null || details.getAccountHolder() == null) {
            return ResponseEntity.badRequest().build();
        }
        BankDetails updated = service.updateBankDetails(details);
        return ResponseEntity.ok(updated);
    }

    // ✅ Admin Panel upload (HTML form with file)
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
                String uploadDir = Paths.get(System.getProperty("user.home"), "xinpay-uploads").toString();
                Path uploadPath = Paths.get(uploadDir);
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);
                qrFile.transferTo(filePath);

                // ✅ Return full URL for Android to access
                String baseUrl = "https://xinpay-backend.onrender.com"; // or http://localhost:8080 for local https://xinpay-backend.onrender.com
                qrUrl = baseUrl + "/uploads/" + fileName;
            }

            BankDetails newDetails = new BankDetails(accountNumber, ifscCode, accountHolder, qrUrl);
            BankDetails updated = service.updateBankDetails(newDetails);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
