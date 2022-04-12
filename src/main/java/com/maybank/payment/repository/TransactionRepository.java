package com.maybank.payment.repository;

import java.util.List;

import com.maybank.payment.entity.Transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,String> {
    
    public List<Transaction> findByAccountNo(String accountNo, Pageable pageable);

}
