package com.maybank.payment.dto;

public class BalanceConvertedResponse extends BalanceResponse {

    private String targetCurrency;
    private String targetBalance;

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public String getTargetBalance() {
        return targetBalance;
    }

    public void setTargetBalance(String targetBalance) {
        this.targetBalance = targetBalance;
    }
}
