package com.maybank.payment.dto;

public class TransactionResponse extends BaseResponse {
    
    private String transactionId;
    private String accountBalance;
    private String accountCurrency;

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
