package com.example.customerapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Tests for the CustomerApiApplication class.
 * This test ensures that the Spring context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
public class CustomerApiApplicationTest {

    @Test
    public void contextLoads() {
        // This test will fail if the application context cannot start
    }

    @Test
    public void testMainMethod() {
        // This test simply calls the main method to increase coverage
        CustomerApiApplication.main(new String[]{});
    }
}
