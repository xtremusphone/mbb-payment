package com.maybank.payment.utils;

import java.text.DecimalFormat;
import java.util.Currency;

import com.maybank.payment.external.service.ExchangeRatesApi;

public class CurrencyUtils {
    
    private static ExchangeRatesApi exchangeRateApi = new ExchangeRatesApi();

    public static String format(String amount, String currencyCode) {
        return format(Double.parseDouble(amount), currencyCode);
    }

    public static String format(double amount, String currencyCode) {
        int currencyDecimal = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(currencyDecimal);
        format.setMinimumFractionDigits(currencyDecimal);
        return format.format(amount);
    }

    public static double getConvertedRate(double amount, String sourceCurrency, String targetCurrency) throws Exception {
        return exchangeRateApi.getConvertedRate(amount, sourceCurrency, targetCurrency);
    }
}
