package com.example.customerapi.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the GlobalExceptionHandler class.
 */
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    public void setup() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("test/uri");
    }

    @Test
    public void testHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Customer not found");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleResourceNotFoundException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Resource Not Found", errorResponse.getError());
        assertEquals("Customer not found", errorResponse.getMessage());
        assertEquals("test/uri", errorResponse.getPath());
    }

    @Test
    public void testHandleDataIntegrityViolationException() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException(
                "could not execute statement; SQL [n/a]; constraint [primaryEmail]");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Data Integrity Violation", errorResponse.getError());
        assertEquals("Email address is already in use", errorResponse.getMessage());
        assertEquals("test/uri", errorResponse.getPath());
    }

    @Test
    public void testHandleDataIntegrityViolationExceptionGeneric() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Some other constraint violation");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleDataIntegrityViolationException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Data Integrity Violation", errorResponse.getError());
        assertEquals("Some other constraint violation", errorResponse.getMessage());
        assertEquals("test/uri", errorResponse.getPath());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("customer", "givenName", "First name is required"));
        fieldErrors.add(new FieldError("customer", "primaryEmail", "Email must be valid"));
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleValidationExceptions(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Validation Error", errorResponse.getError());
        // The message contains the map of field errors
        assertNotNull(errorResponse.getMessage());
        assertEquals("test/uri", errorResponse.getPath());
    }

    @Test
    public void testHandleGlobalException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<Object> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals("Unexpected error", errorResponse.getMessage());
        assertEquals("test/uri", errorResponse.getPath());
    }
}
