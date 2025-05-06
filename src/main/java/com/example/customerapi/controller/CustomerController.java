
package com.example.customerapi.controller;

import com.example.customerapi.model.Customer;
import com.example.customerapi.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Customer resources.
 * Provides endpoints for CRUD operations on customers.
 *
 * All endpoints are prefixed with "/api/customers".
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer API", description = "API for managing customer information")
public class CustomerController {

    private final CustomerService service;

    /**
     * Constructor for dependency injection of the CustomerService.
     *
     * @param service The customer service to be used by this controller
     */
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /**
     * Creates a new customer.
     *
     * @param customer The customer object to create, validated using Bean Validation
     * @return ResponseEntity containing the created customer with HTTP 200 OK status
     */
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer created successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<Customer> createCustomer(
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Customer information", required = true,
                content = @Content(schema = @Schema(implementation = Customer.class)))
            Customer customer) {
        return ResponseEntity.ok(service.createCustomer(customer));
    }

    /**
     * Retrieves all customers.
     *
     * @return ResponseEntity containing a list of all customers with HTTP 200 OK status
     */
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of customers retrieved successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)))
    })
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(service.getAllCustomers());
    }

    /**
     * Retrieves a specific customer by ID.
     *
     * @param id The UUID of the customer to retrieve
     * @return ResponseEntity containing the customer if found with HTTP 200 OK status,
     *         or HTTP 404 Not Found if the customer doesn't exist
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public ResponseEntity<Customer> getCustomerById(
            @Parameter(description = "ID of the customer to retrieve",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return service.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing customer.
     *
     * @param id The UUID of the customer to update
     * @param customer The updated customer data, validated using Bean Validation
     * @return ResponseEntity containing the updated customer with HTTP 200 OK status
     * @throws RuntimeException if the customer with the given ID is not found
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an existing customer with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public ResponseEntity<Customer> updateCustomer(
            @Parameter(description = "ID of the customer to update",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated customer information", required = true,
                content = @Content(schema = @Schema(implementation = Customer.class)))
            Customer customer) {
        return ResponseEntity.ok(service.updateCustomer(id, customer));
    }

    /**
     * Deletes a customer by ID.
     *
     * @param id The UUID of the customer to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "ID of the customer to delete",
                    required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
