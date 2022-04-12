package com.maybank.payment.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import com.maybank.payment.entity.Account;
import com.maybank.payment.entity.Transaction;
import com.maybank.payment.exception.ExceededMaxBalanceException;
import com.maybank.payment.exception.InsufficientFundException;
import com.maybank.payment.exception.InvalidAccountNoException;
import com.maybank.payment.repository.AccountRepository;
import com.maybank.payment.repository.TransactionRepository;
import com.maybank.payment.utils.CurrencyUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public static final int MAX_TRANSACTION_RECORD = 10;
    private final BigDecimal MAX_ACCOUNT_BALANCE = new BigDecimal(1000000);

    @Transactional
    public HashMap<String, String> creditTransaction(String accountNo, double amount, String currencyCode, String description) throws Exception {
        HashMap<String, String> result = new HashMap<>();
        Account accountBalance = entityManager.find(Account.class, accountNo, LockModeType.PESSIMISTIC_WRITE);

        if(accountBalance == null)
            throw new InvalidAccountNoException();

        double baseRequestAmount = currencyCode.equals(accountBalance.getCurrencyCode()) 
                                        ? amount
                                        : CurrencyUtils.getConvertedRate(amount , currencyCode , accountBalance.getCurrencyCode());

        accountRepository.findById(accountNo).get();

        Transaction transaction = new Transaction();
        transaction.setAccountNo(accountNo);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setCurrencyCode(currencyCode);
        transaction.setBaseAmount(new BigDecimal(baseRequestAmount));
        transaction.setBaseCurrency(accountBalance.getCurrencyCode());
        transaction.setDescription(description);
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        transaction = transactionRepository.save(transaction);

        if(accountBalance.getClosingBalance().add(new BigDecimal(baseRequestAmount)).compareTo(MAX_ACCOUNT_BALANCE) == 1) {
            throw new ExceededMaxBalanceException();
        }

        accountBalance.setClosingBalance(accountBalance.getClosingBalance().add(new BigDecimal(baseRequestAmount)));
        accountBalance = accountRepository.saveAndFlush(accountBalance);

        result.put("transactionId", transaction.getTransactionId());
        result.put("accountCurrency", accountBalance.getCurrencyCode());
        result.put("accountBalance", String.valueOf(accountBalance.getClosingBalance().doubleValue()));

        return result;
    }

    @Transactional
    public HashMap<String,String> debitTransaction(String accountNo, double amount, String currencyCode, String description) throws Exception {
        HashMap<String,String> result =  new HashMap<>();
        Account accountBalance = entityManager.find(Account.class, accountNo, LockModeType.PESSIMISTIC_WRITE);

        if(accountBalance == null)
            throw new InvalidAccountNoException();

        double baseRequestAmount = currencyCode.equals(accountBalance.getCurrencyCode()) 
                                        ? amount
                                        : CurrencyUtils.getConvertedRate(amount , currencyCode , accountBalance.getCurrencyCode());

        Transaction transaction = new Transaction();
        transaction.setAccountNo(accountNo);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setCurrencyCode(currencyCode);
        transaction.setBaseAmount(new BigDecimal(baseRequestAmount));
        transaction.setBaseCurrency(accountBalance.getCurrencyCode());
        transaction.setDescription(description);
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        transaction = transactionRepository.save(transaction);

        if(accountBalance.getClosingBalance().subtract(new BigDecimal(-1 * baseRequestAmount)).compareTo(BigDecimal.ZERO) == -1) {
            throw new InsufficientFundException();
        }

        accountBalance.setClosingBalance(accountBalance.getClosingBalance().subtract(new BigDecimal(-1 * baseRequestAmount)));
        accountBalance = accountRepository.saveAndFlush(accountBalance);

        result.put("transactionId", transaction.getTransactionId());
        result.put("accountCurrency", accountBalance.getCurrencyCode());
        result.put("accountBalance", String.valueOf(accountBalance.getClosingBalance().doubleValue()));

        return result;
    }

    public List<Transaction> transactionHistory(String accountNo, int page) {
        return transactionRepository.findByAccountNo(accountNo, PageRequest.of(page, MAX_TRANSACTION_RECORD + 1));
    }
}
