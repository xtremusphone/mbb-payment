package com.maybank.payment.dto;

public class TransactionRequest {
    
    private String accountNo;
    private double amount;
    private String currencyCode;
    private String description;

    public String getAccountNo() {
        return accountNo;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCurrencyCode() {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
}
