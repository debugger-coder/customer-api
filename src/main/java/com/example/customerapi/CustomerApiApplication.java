package com.example.customerapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Customer API.
 * This is the entry point for the Spring Boot application.
 */
@SpringBootApplication
public class CustomerApiApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomerApiApplication.class, args);
    }
}
