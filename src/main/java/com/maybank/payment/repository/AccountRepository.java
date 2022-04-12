package com.maybank.payment.repository;

import com.maybank.payment.entity.Account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,String> {
    
}
