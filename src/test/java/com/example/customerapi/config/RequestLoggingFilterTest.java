package com.example.customerapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the RequestLoggingFilter class.
 */
@ExtendWith(MockitoExtension.class)
public class RequestLoggingFilterTest {

    private RequestLoggingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setup() {
        filter = new RequestLoggingFilter();

        // Clear MDC before each test
        MDC.clear();
    }

    @AfterEach
    public void tearDown() {
        // Clear MDC after each test
        MDC.clear();
    }

    @Test
    public void testDoFilterInternal() throws ServletException, IOException {
        // Arrange
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/customers");
        when(response.getStatus()).thenReturn(200);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);

        // Verify MDC is cleared after filter execution
        assertNull(MDC.get("requestId"));
        assertNull(MDC.get("clientIp"));
        assertNull(MDC.get("method"));
        assertNull(MDC.get("path"));
        assertNull(MDC.get("duration"));
        assertNull(MDC.get("statusCode"));
    }

    @Test
    public void testDoFilterInternalWithException() throws ServletException, IOException {
        // Arrange
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/customers");
        when(response.getStatus()).thenReturn(500);

        // Simulate an exception during filter chain execution
        Exception testException = new RuntimeException("Test exception");
        doThrow(testException).when(filterChain).doFilter(request, response);

        // Act & Assert
        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Verify MDC is cleared even when an exception occurs
        assertNull(MDC.get("requestId"));
        assertNull(MDC.get("clientIp"));
        assertNull(MDC.get("method"));
        assertNull(MDC.get("path"));
        assertNull(MDC.get("duration"));
        assertNull(MDC.get("statusCode"));
    }
}
