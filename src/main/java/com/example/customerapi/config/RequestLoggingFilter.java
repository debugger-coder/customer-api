package com.example.customerapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to add request information to the Mapped Diagnostic Context (MDC)
 * for structured logging. This enables correlation of log entries for a single request.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        
        // Get client IP address
        String clientIp = request.getRemoteAddr();
        
        // Get request method and path
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        // Add values to MDC
        MDC.put("requestId", requestId);
        MDC.put("clientIp", clientIp);
        MDC.put("method", method);
        MDC.put("path", path);
        
        // Record request start time
        long startTime = System.currentTimeMillis();
        
        try {
            // Log the incoming request
            logger.info("Received request: {} {}", method, path);
            
            // Continue with the request
            filterChain.doFilter(request, response);
        } finally {
            // Calculate request duration
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("duration", String.valueOf(duration));
            MDC.put("statusCode", String.valueOf(response.getStatus()));
            
            // Log the completed request
            logger.info("Completed request: {} {} - Status: {} - Duration: {}ms", 
                    method, path, response.getStatus(), duration);
            
            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }
}
