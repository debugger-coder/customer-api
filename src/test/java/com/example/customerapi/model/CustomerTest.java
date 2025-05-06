package com.example.customerapi.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Customer model class.
 */
public class CustomerTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testCustomerBuilder() {
        // Arrange & Act
        Customer customer = Customer.builder()
                .customerId(UUID.randomUUID())
                .givenName("John")
                .middleInitial("A")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        // Assert
        assertNotNull(customer);
        assertNotNull(customer.getCustomerId());
        assertEquals("John", customer.getGivenName());
        assertEquals("A", customer.getMiddleInitial());
        assertEquals("Doe", customer.getSurname());
        assertEquals("john.doe@example.com", customer.getPrimaryEmail());
        assertEquals("123-456-7890", customer.getContactNumber());
    }

    @Test
    public void testCustomerAllArgsConstructor() {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act
        Customer customer = new Customer(
                id,
                "Jane",
                "B",
                "Smith",
                "jane.smith@example.com",
                "987-654-3210"
        );

        // Assert
        assertEquals(id, customer.getCustomerId());
        assertEquals("Jane", customer.getGivenName());
        assertEquals("B", customer.getMiddleInitial());
        assertEquals("Smith", customer.getSurname());
        assertEquals("jane.smith@example.com", customer.getPrimaryEmail());
        assertEquals("987-654-3210", customer.getContactNumber());
    }

    @Test
    public void testCustomerNoArgsConstructor() {
        // Act
        Customer customer = new Customer();

        // Assert
        assertNotNull(customer);
    }

    @Test
    public void testCustomerSetters() {
        // Arrange
        Customer customer = new Customer();
        UUID id = UUID.randomUUID();

        // Act
        customer.setCustomerId(id);
        customer.setGivenName("Alice");
        customer.setMiddleInitial("C");
        customer.setSurname("Johnson");
        customer.setPrimaryEmail("alice.johnson@example.com");
        customer.setContactNumber("555-123-4567");

        // Assert
        assertEquals(id, customer.getCustomerId());
        assertEquals("Alice", customer.getGivenName());
        assertEquals("C", customer.getMiddleInitial());
        assertEquals("Johnson", customer.getSurname());
        assertEquals("alice.johnson@example.com", customer.getPrimaryEmail());
        assertEquals("555-123-4567", customer.getContactNumber());
    }

    @Test
    public void testValidCustomer() {
        // Arrange
        Customer customer = Customer.builder()
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        // Act
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidEmail() {
        // Arrange
        Customer customer = Customer.builder()
                .givenName("John")
                .surname("Doe")
                .primaryEmail("not-an-email")
                .contactNumber("123-456-7890")
                .build();

        // Act
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Assert
        assertFalse(violations.isEmpty());
        boolean hasEmailViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("primaryEmail"));
        assertTrue(hasEmailViolation);
    }

    @Test
    public void testMissingRequiredFields() {
        // Arrange
        Customer customer = Customer.builder().build();

        // Act
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Assert
        assertFalse(violations.isEmpty());
        // There should be at least 3 violations (givenName, surname, primaryEmail are required)
        // There might be more if contactNumber is also required
        assertTrue(violations.size() >= 3);
    }

    @Test
    public void testInvalidPhoneNumber() {
        // Arrange
        Customer customer = Customer.builder()
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("invalid-phone")
                .build();

        // Act
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        // Assert
        assertFalse(violations.isEmpty());
        boolean hasPhoneViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("contactNumber"));
        assertTrue(hasPhoneViolation);
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        Customer customer1 = Customer.builder()
                .customerId(id)
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        Customer customer2 = Customer.builder()
                .customerId(id)
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        Customer customer3 = Customer.builder()
                .customerId(UUID.randomUUID())
                .givenName("Jane")
                .surname("Smith")
                .primaryEmail("jane.smith@example.com")
                .contactNumber("987-654-3210")
                .build();

        // Assert
        assertEquals(customer1, customer2);
        assertEquals(customer1.hashCode(), customer2.hashCode());
        assertFalse(customer1.equals(customer3));
        assertFalse(customer1.hashCode() == customer3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        Customer customer = Customer.builder()
                .customerId(id)
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        // Act
        String toString = customer.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("customerId=" + id));
        assertTrue(toString.contains("givenName=John"));
        assertTrue(toString.contains("surname=Doe"));
        assertTrue(toString.contains("primaryEmail=john.doe@example.com"));
        assertTrue(toString.contains("contactNumber=123-456-7890"));
    }
}
