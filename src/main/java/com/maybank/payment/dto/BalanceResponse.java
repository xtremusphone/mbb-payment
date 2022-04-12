package com.maybank.payment.dto;

public class BalanceResponse extends BaseResponse {
    
    private String accountCurrency;
    private String accountBalance;

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
}