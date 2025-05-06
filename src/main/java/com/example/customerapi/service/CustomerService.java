
package com.example.customerapi.service;

import com.example.customerapi.exception.ResourceNotFoundException;
import com.example.customerapi.model.Customer;
import com.example.customerapi.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing Customer entities.
 * Provides business logic for CRUD operations on customers.
 */
@Service
public class CustomerService {

    private final CustomerRepository repository;

    /**
     * Constructor for dependency injection of the CustomerRepository.
     *
     * @param repository The customer repository to be used by this service
     */
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new customer in the database.
     *
     * @param customer The customer object to create
     * @return The created customer with generated ID
     */
    public Customer createCustomer(Customer customer) {
        return repository.save(customer);
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return A list of all customers
     */
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    /**
     * Retrieves a paginated list of customers from the database.
     *
     * @param pageable Pagination information including page number, size, and sorting
     * @return A page of customers
     */
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return repository.findAll(pageable);
    }

    /**
     * Retrieves a specific customer by ID.
     *
     * @param id The UUID of the customer to retrieve
     * @return An Optional containing the customer if found, or empty if not found
     */
    public Optional<Customer> getCustomerById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Deletes a customer by ID.
     * If the customer doesn't exist, throws a ResourceNotFoundException.
     *
     * @param id The UUID of the customer to delete
     * @throws ResourceNotFoundException if the customer with the given ID is not found
     */
    public void deleteCustomer(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        repository.deleteById(id);
    }

    /**
     * Updates an existing customer with new information.
     *
     * @param id The UUID of the customer to update
     * @param newCustomer The updated customer data
     * @return The updated customer
     * @throws RuntimeException if the customer with the given ID is not found
     */
    public Customer updateCustomer(UUID id, Customer newCustomer) {
        return repository.findById(id).map(c -> {
            // Update all fields from the new customer
            c.setGivenName(newCustomer.getGivenName());
            c.setMiddleInitial(newCustomer.getMiddleInitial());
            c.setSurname(newCustomer.getSurname());
            c.setPrimaryEmail(newCustomer.getPrimaryEmail());
            c.setContactNumber(newCustomer.getContactNumber());
            // Save and return the updated customer
            return repository.save(c);
        }).orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }
}
