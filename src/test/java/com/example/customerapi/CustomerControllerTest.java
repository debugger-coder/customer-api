
package com.example.customerapi;

import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.MvcResult; // Unused import

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the CustomerController.
 * Uses Spring Boot's testing support to test the full API flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;
    private UUID testCustomerId;

    /**
     * Set up test data before each test.
     * Creates a test customer in the database that can be used by the tests.
     */
    @BeforeEach
    public void setup() {
        // Create a test customer for use in tests using Lombok's Builder pattern
        testCustomer = Customer.builder()
                .givenName("Jane")
                .surname("Smith")
                .primaryEmail("jane.smith@example.com")
                .contactNumber("987-654-3210")
                .build();

        testCustomer = customerRepository.save(testCustomer);
        testCustomerId = testCustomer.getCustomerId();
        assertNotNull(testCustomerId, "Test customer ID should not be null");
    }

    /**
     * Clean up after each test.
     * Removes all customers from the database to ensure test isolation.
     */
    @AfterEach
    public void cleanup() {
        customerRepository.deleteAll();
    }

    /**
     * Test creating a new customer via the API.
     * Verifies that the customer is created with the correct data and a valid ID.
     */
    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = Customer.builder()
                .givenName("John")
                .surname("Doe")
                .primaryEmail("john.doe@example.com")
                .contactNumber("123-456-7890")
                .build();

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.givenName").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.primaryEmail").value("john.doe@example.com"));
    }

    /**
     * Test retrieving all customers via the API.
     * Verifies that the list contains the test customer created in setup().
     */
    @Test
    public void testGetAllCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(testCustomerId.toString()));
    }

    /**
     * Test retrieving a specific customer by ID via the API.
     * Verifies that the correct customer data is returned.
     */
    @Test
    public void testGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/customers/{id}", testCustomerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(testCustomerId.toString()))
                .andExpect(jsonPath("$.givenName").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Smith"));
    }

    /**
     * Test retrieving a non-existent customer by ID via the API.
     * Verifies that a 404 Not Found response is returned.
     */
    @Test
    public void testGetCustomerByIdNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test updating an existing customer via the API.
     * Verifies that the customer data is updated correctly.
     */
    @Test
    public void testUpdateCustomer() throws Exception {
        Customer updatedCustomer = Customer.builder()
                .givenName("Jane")
                .middleInitial("M")
                .surname("Johnson")
                .primaryEmail("jane.johnson@example.com")
                .contactNumber("555-123-4567")
                .build();

        mockMvc.perform(put("/api/customers/{id}", testCustomerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(testCustomerId.toString()))
                .andExpect(jsonPath("$.givenName").value("Jane"))
                .andExpect(jsonPath("$.middleInitial").value("M"))
                .andExpect(jsonPath("$.surname").value("Johnson"))
                .andExpect(jsonPath("$.primaryEmail").value("jane.johnson@example.com"));
    }

    /**
     * Test deleting a customer via the API.
     * Verifies that the customer is deleted and can no longer be retrieved.
     */
    @Test
    public void testDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/{id}", testCustomerId))
                .andExpect(status().isNoContent());

        // Verify customer is deleted
        mockMvc.perform(get("/api/customers/{id}", testCustomerId))
                .andExpect(status().isNotFound());
    }

    /**
     * Test creating a customer with an invalid email address.
     * Verifies that validation is working and a 400 Bad Request response is returned.
     */
    @Test
    public void testCreateCustomerWithInvalidEmail() throws Exception {
        Customer customer = Customer.builder()
                .givenName("Invalid")
                .surname("Email")
                .primaryEmail("not-an-email") // Invalid email format
                .contactNumber("123-456-7890")
                .build();

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());
    }
}
