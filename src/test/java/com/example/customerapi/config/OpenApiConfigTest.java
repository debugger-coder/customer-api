package com.example.customerapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the OpenApiConfig class.
 */
public class OpenApiConfigTest {

    @Test
    public void testCustomerApiOpenAPI() {
        // Arrange
        OpenApiConfig config = new OpenApiConfig();

        // Act
        OpenAPI openAPI = config.customerApiOpenAPI();

        // Assert
        assertNotNull(openAPI);
        
        // Check info
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Customer API", info.getTitle());
        assertEquals("1.0.0", info.getVersion());
        
        // Check contact
        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals("API Support", contact.getName());
        assertEquals("support@example.com", contact.getEmail());
        
        // Check servers
        assertNotNull(openAPI.getServers());
        assertEquals(2, openAPI.getServers().size());
        
        Server localServer = openAPI.getServers().get(0);
        assertEquals("http://localhost:8080", localServer.getUrl());
        assertEquals("Local development server", localServer.getDescription());
        
        Server prodServer = openAPI.getServers().get(1);
        assertEquals("https://api.example.com", prodServer.getUrl());
        assertEquals("Production server", prodServer.getDescription());
    }
}
