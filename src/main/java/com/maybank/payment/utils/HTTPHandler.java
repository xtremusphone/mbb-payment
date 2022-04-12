package com.maybank.payment.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class HTTPHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(HTTPHandler.class);

    public static String executeGet(String urlString, int connectTimeout, int readTimeout, String parameter) throws Exception {
        HttpURLConnection conn = null;
        try {
            String fullUrl = parameter == null || "".equals(parameter) ? urlString : urlString + "?" + parameter;
            String method = "GET";
            
            URL url = new URL(fullUrl);
            logger.info("Request " + fullUrl);

            conn = urlString.toLowerCase().startsWith("https") ? 
                                        (HttpsURLConnection) url.openConnection() 
                                        : (HttpURLConnection) url.openConnection();

            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);

            if(conn.getResponseCode() != HttpStatus.OK.value()) 
                logger.error("Error response " + new BufferedReader(
                    new InputStreamReader(
                        conn.getErrorStream()))
                    .lines()
                    .collect(Collectors.joining()));

            String response = new BufferedReader(
                new InputStreamReader(
                    conn.getInputStream()))
                .lines()
                .collect(Collectors.joining());

            return response;
        } finally {
            if(conn != null) conn.disconnect();
        }
    }

}
