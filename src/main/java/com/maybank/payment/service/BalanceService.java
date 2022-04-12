package com.maybank.payment.service;

import java.util.HashMap;

import com.maybank.payment.entity.Account;
import com.maybank.payment.exception.InvalidAccountNoException;
import com.maybank.payment.repository.AccountRepository;
import com.maybank.payment.utils.CurrencyUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    @Autowired
    private AccountRepository accountRepository;

    public HashMap<String,String> getBalance(String accountNo) throws Exception {
        HashMap<String, String> result = new HashMap<>();

        Account account = accountRepository.findById(accountNo)
                                            .orElseThrow(() -> new InvalidAccountNoException());

        result.put("accountBalance", CurrencyUtils.format(account.getClosingBalance().doubleValue()
                                                            , account.getCurrencyCode()
                                                                ));
        result.put("accountCurrency", account.getCurrencyCode());

        return result;
    }

    public HashMap<String,String> convertBalance(String accountNo, String targetCurrency) throws Exception {
        HashMap<String,String> result = new HashMap<>();
        
        HashMap<String, String> accountBalance = getBalance(accountNo);
        double convertedRate = CurrencyUtils.getConvertedRate(Double.parseDouble(accountBalance.get("accountBalance").replace(",", ""))
                                                                , accountBalance.get("accountCurrency")
                                                                , targetCurrency
                                                                );
        
        result.put("accountBalance", accountBalance.get("accountBalance"));
        result.put("accountCurrency", accountBalance.get("accountCurrency"));
        result.put("targetCurrency", targetCurrency);
        result.put("targetBalance", CurrencyUtils.format(convertedRate, targetCurrency));
        
        return result;
    }
}
