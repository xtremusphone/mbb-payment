package com.maybank.payment.dto;

import java.util.List;

import com.maybank.payment.entity.Transaction;

public class TransactionHistoryResponse extends BaseResponse {
    
    private Boolean moreRecord;
    private List<Transaction> transactionList;
    
    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public Boolean getMoreRecord() {
        return moreRecord;
    }

    public void setMoreRecord(Boolean moreRecord) {
        this.moreRecord = moreRecord;
    }

    public void setTransactionList(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

}
