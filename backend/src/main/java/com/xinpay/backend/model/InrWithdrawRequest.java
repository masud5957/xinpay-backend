package com.xinpay.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inr_withdraw_requests")
public class InrWithdrawRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Double amount;

    private String accountNumber;

    private String ifscCode;

    private boolean approved;

    // Constructors
    public InrWithdrawRequest() {}

    public InrWithdrawRequest(String userId, Double amount, String accountNumber, String ifscCode, boolean approved) {
        this.userId = userId;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.approved = approved;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }
}
