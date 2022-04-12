package com.maybank.payment.external.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maybank.payment.external.exception.ExternalServiceConnectTimeoutException;
import com.maybank.payment.external.exception.ExternalServiceException;
import com.maybank.payment.external.exception.ExternalServiceReadTimeoutException;
import com.maybank.payment.utils.HTTPHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRatesApi {
    
    private final Logger logger = LoggerFactory.getLogger("ExchangeRateAPI");
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String API_V7_CONVERT_URI = "/api/v7/convert";

    private String url;
    private int connectTimeout;
    private int readTimeout;
    private String key;
    private HashMap<String,Double> exchangeRate =  new HashMap<>();
    
    public ExchangeRatesApi() {
        init();
    }

    private void init() {
        try(InputStream stream = getClass().getClassLoader().getResourceAsStream("external/exchangerate.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            url = properties.getProperty("api.url");
            connectTimeout = Integer.parseInt(properties.getProperty("api.timeout.connect", "1000"));
            readTimeout = Integer.parseInt(properties.getProperty("api.timeout.read", "5000"));
            key = properties.getProperty("api.key");
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage(), ex);
        }
    }

    private void loadCurrency(String sourceCurrency, String targetCurrency) throws Exception {
        try{
            String sourceDestCurr = sourceCurrency + "_" + targetCurrency;
            String response = HTTPHandler.executeGet(url + API_V7_CONVERT_URI, 
                                                        connectTimeout, 
                                                        readTimeout, 
                                                        "q=" + sourceDestCurr + "&compact=ultra&apiKey=" + key
                                                        );
            exchangeRate.put(sourceDestCurr, Double.parseDouble(
                                                mapper.readTree(response)
                                                        .get(sourceDestCurr)
                                                            .asText())
                                                            );
        } catch (ConnectException ce) {
            throw new ExternalServiceConnectTimeoutException();                                                
        } catch (SocketTimeoutException ste) {
            throw new ExternalServiceReadTimeoutException();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ExternalServiceException();
        }
    }

    public double getConvertedRate(double amount, String sourceCurrency, String targetCurrency) throws Exception {
        String sourceDestCurr = sourceCurrency + "_" + targetCurrency;
        if(!exchangeRate.containsKey(sourceDestCurr))
            loadCurrency(sourceCurrency, targetCurrency);
        return amount * exchangeRate.get(sourceDestCurr);
    }
}
