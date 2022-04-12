package com.maybank.payment.servlet;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class LoggerServlet extends DispatcherServlet {

    private static final Logger logger = LoggerFactory.getLogger("LoggerServlet");

    private static final ObjectMapper mapper = new ObjectMapper();
    @Value("${loggerservlet.log.header}")
    private Boolean logHeader;

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("clientIp", requestWrapper.getRemoteAddr());
        rootNode.put("threadId", Thread.currentThread().getName());
        rootNode.put("method", requestWrapper.getMethod());
        rootNode.put("uri", requestWrapper.getRequestURI());
        rootNode.put("requestTimestamp", (new Timestamp(System.currentTimeMillis())).toString());
        
        if(logHeader) 
            rootNode.set("requestHeaders", mapper.valueToTree(getRequestHeaders(requestWrapper)));

        try {
            super.doDispatch(requestWrapper, responseWrapper);
        } finally {
            try{
                JsonNode newNode = mapper.readTree(requestWrapper.getContentAsByteArray());
                rootNode.set("request", newNode);
            } catch (Exception ex) {
                rootNode.set("request", mapper.valueToTree(requestWrapper.getParameterMap()));
            }

            rootNode.put("status", responseWrapper.getStatus());
            rootNode.put("responseTimestamp", (new Timestamp(System.currentTimeMillis())).toString());

            try{
                JsonNode newNode = mapper.readTree(responseWrapper.getContentAsByteArray());
                rootNode.set("response", newNode);
            } catch (Exception ex) {
                JsonNode newNode = mapper.valueToTree(responseWrapper.getContentAsByteArray());
                rootNode.set("response", newNode);
            }

            responseWrapper.copyBodyToResponse();

            if(logHeader) 
                rootNode.set("responseHeaders", mapper.valueToTree(getResponsetHeaders(responseWrapper)));

            logger.info("\n" + rootNode.toPrettyString());
        }
    }

    private Map<String, Object> getRequestHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;

    }

    private Map<String, Object> getResponsetHeaders(ContentCachingResponseWrapper response) {
        Map<String, Object> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            headers.put(headerName, response.getHeader(headerName));
        }
        return headers;
    }
}