package com.maybank.payment;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.maybank.payment.entity.Account;
import com.maybank.payment.entity.Transaction;
import com.maybank.payment.repository.AccountRepository;
import com.maybank.payment.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentApplicationTests {

	@Autowired
	private AccountRepository accountRepository;

	@Test
	void contextLoads() {
		accountRepository.saveAll(
			List.of(
				new Account("MYR", new BigDecimal(5000.0)),
				new Account("MYR", new BigDecimal(100000.0)),
				new Account("MYR", new BigDecimal(123330.0))
			)
		);
	}

}
