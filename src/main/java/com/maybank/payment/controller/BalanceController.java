package com.maybank.payment.controller;

import java.util.HashMap;

import com.maybank.payment.dto.BalanceConvertedResponse;
import com.maybank.payment.dto.BalanceResponse;
import com.maybank.payment.exception.InvalidAccountNoException;
import com.maybank.payment.exception.InvalidCurrencyCodeException;
import com.maybank.payment.external.exception.ExternalServiceConnectTimeoutException;
import com.maybank.payment.external.exception.ExternalServiceException;
import com.maybank.payment.external.exception.ExternalServiceReadTimeoutException;
import com.maybank.payment.service.BalanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/balance")
public class BalanceController {
    
    private final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping(path = "/{accountNo}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String accountNo) {
        BalanceResponse response = new BalanceResponse();
        try{
            HashMap<String,String> serviceResp = balanceService.getBalance(accountNo);

            response.setResponseCode(Message.SUCCESSFUL.code);
            response.setResponseDescription(Message.SUCCESSFUL.description);
            response.setAccountBalance(serviceResp.get("accountBalance"));
            response.setAccountCurrency(serviceResp.get("accountCurrency"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InvalidAccountNoException icn) {

            response.setResponseCode(Message.INVALID_ACCOUNT_NO.code);
            response.setResponseDescription(Message.INVALID_ACCOUNT_NO.description);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {

            logger.error(e.getMessage(), e);

            response.setResponseCode(Message.UNHANDLED_SERVICE_EXCEPTION.code);
            response.setResponseDescription(Message.UNHANDLED_SERVICE_EXCEPTION.description);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(path = "/{accountNo}/convert/{currency}")
    public ResponseEntity<BalanceConvertedResponse> getConvertedBalance(@PathVariable String accountNo, @PathVariable String currency) {
        BalanceConvertedResponse response = new BalanceConvertedResponse();
        try{
            HashMap<String,String> serviceResp = balanceService.convertBalance(accountNo, currency);

            response.setResponseCode(Message.SUCCESSFUL.code);
            response.setResponseDescription(Message.SUCCESSFUL.description);
            response.setAccountBalance(serviceResp.get("accountBalance"));
            response.setAccountCurrency(serviceResp.get("accountCurrency"));
            response.setTargetBalance(serviceResp.get("targetBalance"));
            response.setTargetCurrency(serviceResp.get("targetCurrency"));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (InvalidAccountNoException icn) {

            response.setResponseCode(Message.INVALID_ACCOUNT_NO.code);
            response.setResponseDescription(Message.INVALID_ACCOUNT_NO.description);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (InvalidCurrencyCodeException icc) {

            response.setResponseCode(Message.INVALID_CURRENCY_CODE.code);
            response.setResponseDescription(Message.INVALID_CURRENCY_CODE.description);

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
}
