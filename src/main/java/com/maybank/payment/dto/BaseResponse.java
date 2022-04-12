package com.maybank.payment.dto;

import java.util.UUID;

public class BaseResponse {
    
    private String responseCode;
    private String responseDescription;
    private String requestReference;

    public BaseResponse() {
        this.requestReference = UUID.randomUUID().toString();
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getRequestReference() {
        return requestReference;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public void setRequestReference(String requestReference) {
        this.requestReference = requestReference;
    }
}
