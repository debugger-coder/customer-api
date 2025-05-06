
package com.example.customerapi.repository;

import com.example.customerapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Customer entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 * Uses UUID as the ID type for Customer entities.
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    /**
     * Finds a customer by their primary email address.
     *
     * @param email The email address to search for
     * @return An Optional containing the customer if found, or empty if not found
     */
    Optional<Customer> findByPrimaryEmail(String email);
}
