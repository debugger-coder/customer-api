package com.example.customerapi.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the ResourceNotFoundException class.
 */
public class ResourceNotFoundExceptionTest {

    @Test
    public void testConstructorWithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testConstructorWithResourceDetails() {
        // Arrange
        String resourceName = "Customer";
        String fieldName = "id";
        UUID fieldValue = UUID.randomUUID();

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(resourceName, fieldName, fieldValue);

        // Assert
        String expectedMessage = String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
