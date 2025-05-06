package com.example.customerapi.service;

import com.example.customerapi.exception.ResourceNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the CustomerService class.
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private UUID testCustomerId;

    @BeforeEach
    public void setup() {
        testCustomerId = UUID.randomUUID();
        testCustomer = Customer.builder()
                .customerId(testCustomerId)
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();
    }

    @Test
    public void testCreateCustomer() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        Customer createdCustomer = customerService.createCustomer(testCustomer);

        // Assert
        assertNotNull(createdCustomer);
        assertEquals(testCustomerId, createdCustomer.getCustomerId());
        assertEquals("John", createdCustomer.getGivenName());
        verify(customerRepository, times(1)).save(testCustomer);
    }

    @Test
    public void testGetAllCustomers() {
        // Arrange
        Customer customer2 = Customer.builder()
                .customerId(UUID.randomUUID())
                .givenName("Jane")
                .surname("Smith")
                .primaryEmail("jane.smith@example.com")
                .contactNumber("987-654-3210")
                .build();
        List<Customer> customerList = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customerList);

        // Act
        List<Customer> result = customerService.getAllCustomers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllCustomersWithPagination() {
        // Arrange
        Customer customer2 = Customer.builder()
                .customerId(UUID.randomUUID())
                .givenName("Jane")
                .surname("Smith")
                .primaryEmail("jane.smith@example.com")
                .contactNumber("987-654-3210")
                .build();
        List<Customer> customerList = Arrays.asList(testCustomer, customer2);
        Page<Customer> customerPage = new PageImpl<>(customerList);
        Pageable pageable = PageRequest.of(0, 10);
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        // Act
        Page<Customer> result = customerService.getAllCustomers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetCustomerById() {
        // Arrange
        when(customerRepository.findById(testCustomerId)).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<Customer> result = customerService.getCustomerById(testCustomerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCustomerId, result.get().getCustomerId());
        verify(customerRepository, times(1)).findById(testCustomerId);
    }

    @Test
    public void testGetCustomerByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerService.getCustomerById(nonExistentId);

        // Assert
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findById(nonExistentId);
    }

    @Test
    public void testUpdateCustomer() {
        // Arrange
        Customer updatedCustomer = Customer.builder()
                .givenName("John")
                .middleInitial("A")
                .surname("Smith")
                .primaryEmail("john.smith@example.com")
                .contactNumber("555-123-4567")
                .build();

        when(customerRepository.findById(testCustomerId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Customer result = customerService.updateCustomer(testCustomerId, updatedCustomer);

        // Assert
        assertNotNull(result);
        assertEquals(testCustomerId, result.getCustomerId());
        assertEquals("John", result.getGivenName());
        assertEquals("A", result.getMiddleInitial());
        assertEquals("Smith", result.getSurname());
        assertEquals("john.smith@example.com", result.getPrimaryEmail());
        assertEquals("555-123-4567", result.getContactNumber());
        verify(customerRepository, times(1)).findById(testCustomerId);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testUpdateCustomerNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        Customer updatedCustomer = Customer.builder()
                .givenName("John")
                .surname("Smith")
                .primaryEmail("john.smith@example.com")
                .contactNumber("555-123-4567")
                .build();

        when(customerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.updateCustomer(nonExistentId, updatedCustomer);
        });

        assertEquals("Customer not found with id: '" + nonExistentId + "'", exception.getMessage());
        verify(customerRepository, times(1)).findById(nonExistentId);
        verify(customerRepository, times(0)).save(any(Customer.class));
    }

    @Test
    public void testDeleteCustomer() {
        // Arrange
        when(customerRepository.existsById(testCustomerId)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(testCustomerId);

        // Act
        customerService.deleteCustomer(testCustomerId);

        // Assert
        verify(customerRepository, times(1)).existsById(testCustomerId);
        verify(customerRepository, times(1)).deleteById(testCustomerId);
    }

    @Test
    public void testDeleteCustomerNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(customerRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            customerService.deleteCustomer(nonExistentId);
        });

        assertEquals("Customer not found with id: '" + nonExistentId + "'", exception.getMessage());
        verify(customerRepository, times(1)).existsById(nonExistentId);
        verify(customerRepository, times(0)).deleteById(nonExistentId);
    }
}
