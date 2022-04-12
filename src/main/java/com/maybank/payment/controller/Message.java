package com.maybank.payment.controller;

public enum Message {
    SUCCESSFUL("000", "Complete Response"),
    // 1XX - Invalid request field
    INVALID_REQUEST_URI("100","Invalid request uri"),
    INVALID_ACCOUNT_NO("101", "Invalid account no"),
    INVALID_CURRENCY_CODE("102", "Invalid currency code"),
    // 2XX - Exception processing request
    INSUFFICIENT_FUNDS("200","Insufficient funds"),
    NO_RECORD_FOUND("201","No record found"),
    EXCEEDED_MAX_BALANCE("202", "Maximum account balance reached"),
    // 5XX - Service exceptions
    UNHANDLED_SERVICE_EXCEPTION("500", "Internal service exception"),
    EXTERNAL_SERVICE_UNAVAILABLE_EXCEPTION("501","External service unavailable")
    ;

    public final String code;
    public final String description;
    
    private Message(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
