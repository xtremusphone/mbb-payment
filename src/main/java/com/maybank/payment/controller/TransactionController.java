package com.maybank.payment.controller;

import java.util.HashMap;
import java.util.List;

import com.maybank.payment.dto.TransactionHistoryResponse;
import com.maybank.payment.dto.TransactionRequest;
import com.maybank.payment.dto.TransactionResponse;
import com.maybank.payment.entity.Transaction;
import com.maybank.payment.exception.ExceededMaxBalanceException;
import com.maybank.payment.exception.InsufficientFundException;
import com.maybank.payment.exception.InvalidAccountNoException;
import com.maybank.payment.external.exception.ExternalServiceConnectTimeoutException;
import com.maybank.payment.external.exception.ExternalServiceException;
import com.maybank.payment.external.exception.ExternalServiceReadTimeoutException;
import com.maybank.payment.service.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "api/v1/transaction")
public class TransactionController {
    
    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(path = "/credit")
    public ResponseEntity<TransactionResponse> doCredit(@RequestBody TransactionRequest request) {
        TransactionResponse response = new TransactionResponse();
        try{
            HashMap<String,String> result = transactionService.creditTransaction(request.getAccountNo(), request.getAmount(), request.getCurrencyCode(), request.getDescription());
            
            response.setResponseCode(Message.SUCCESSFUL.code);
            response.setResponseDescription(Message.SUCCESSFUL.description);
            response.setTransactionId(result.get("transactionId"));
            response.setAccountCurrency(result.get("accountCurrency"));
            response.setAccountBalance(result.get("accountBalance"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExceededMaxBalanceException ife) {
            response.setResponseCode(Message.EXCEEDED_MAX_BALANCE.code);
            response.setResponseDescription(Message.EXCEEDED_MAX_BALANCE.description);

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        } catch (InvalidAccountNoException icn) {

            response.setResponseCode(Message.INVALID_ACCOUNT_NO.code);
            response.setResponseDescription(Message.INVALID_ACCOUNT_NO.description);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ExternalServiceConnectTimeoutException | ExternalServiceReadTimeoutException  | ExternalServiceException esrte) {

            response.setResponseCode(Message.EXTERNAL_SERVICE_UNAVAILABLE_EXCEPTION.code);
            response.setResponseDescription(Message.EXTERNAL_SERVICE_UNAVAILABLE_EXCEPTION.description);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            response.setResponseCode(Message.UNHANDLED_SERVICE_EXCEPTION.code);
            response.setResponseDescription(Message.UNHANDLED_SERVICE_EXCEPTION.description);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(path = "/debit")
    public ResponseEntity<TransactionResponse> doDebit(@RequestBody TransactionRequest request) {
        TransactionResponse response = new TransactionResponse();
        try{
            HashMap<String,String> result = transactionService.debitTransaction(request.getAccountNo(), request.getAmount() * -1, request.getCurrencyCode(), request.getDescription());

            response.setResponseCode(Message.SUCCESSFUL.code);
            response.setResponseDescription(Message.SUCCESSFUL.description);
            response.setTransactionId(result.get("transactionId"));
            response.setAccountCurrency(result.get("accountCurrency"));
            response.setAccountBalance(result.get("accountBalance"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InsufficientFundException ife) {
            response.setResponseCode(Message.INSUFFICIENT_FUNDS.code);
            response.setResponseDescription(Message.INSUFFICIENT_FUNDS.description);

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);
        } catch (InvalidAccountNoException icn) {

            response.setResponseCode(Message.INVALID_ACCOUNT_NO.code);
            response.setResponseDescription(Message.INVALID_ACCOUNT_NO.description);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (ExternalServiceConnectTimeoutException | ExternalServiceReadTimeoutException  | ExternalServiceException esrte) {

            response.setResponseCode(Message.EXTERNAL_SERVICE_UNAVAILABLE_EXCEPTION.code);
            response.setResponseDescription(Message.EXTERNAL_SERVICE_UNAVAILABLE_EXCEPTION.description);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            response.setResponseCode(Message.UNHANDLED_SERVICE_EXCEPTION.code);
            response.setResponseDescription(Message.UNHANDLED_SERVICE_EXCEPTION.description);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/history/{accountNo}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(@PathVariable String accountNo, @RequestParam(defaultValue = "0") Integer page) {
        TransactionHistoryResponse response = new TransactionHistoryResponse();
        try{
            List<Transaction> record = transactionService.transactionHistory(accountNo, page);
            if(record.size() == 0) {
                response.setResponseCode(Message.NO_RECORD_FOUND.code);
                response.setResponseDescription(Message.NO_RECORD_FOUND.description);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else {
                if(record.size() > TransactionService.MAX_TRANSACTION_RECORD) {
                    record.remove(record.size() - 1);
                    response.setMoreRecord(true);
                } else {
                    response.setMoreRecord(false);
                }
                
                response.setTransactionList(record);

                response.setResponseCode(Message.SUCCESSFUL.code);
                response.setResponseDescription(Message.SUCCESSFUL.description);

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

            response.setResponseCode(Message.UNHANDLED_SERVICE_EXCEPTION.code);
            response.setResponseDescription(Message.UNHANDLED_SERVICE_EXCEPTION.description);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
