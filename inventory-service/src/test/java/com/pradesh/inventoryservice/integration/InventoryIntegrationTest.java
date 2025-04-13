package com.pradesh.inventoryservice.integration;

import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;



import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = {"order-created", "inventory-updated", "inventory-failed"})
@ActiveProfiles("test")
public class InventoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    @AfterEach
    void tearDown() {
        // Use JDBC directly to avoid optimistic locking issues
        try {
            // Give a moment for any pending transactions to complete
            Thread.sleep(100);
            // Use a native query to delete all records
            inventoryRepository.deleteAllInBatch();
        } catch (Exception e) {
            // Log the exception but don't fail the test
            System.err.println("Error in tearDown: " + e.getMessage());
        }
    }

    @Test
    void shouldCreateInventoryAndReturnResponse() {
        // Given - a unique product ID for this test
        String productId = "TEST-PROD-" + System.currentTimeMillis();
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(productId);
        inventoryRequest.setQuantity(10);

        // When - call the API to create the inventory
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/inventory",
                inventoryRequest,
                String.class
        );

        // Then - verify the response status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // And verify the inventory was created in the database
        Inventory createdInventory = inventoryRepository.findByProductId(productId).orElse(null);
        assertThat(createdInventory).isNotNull();
        assertThat(createdInventory.getProductId()).isEqualTo(productId);
        assertThat(createdInventory.getQuantity()).isEqualTo(10);
    }

    @Test
    void shouldUpdateExistingInventory() {
        // Given - an existing inventory item
        String productId = "TEST-UPDATE-" + System.currentTimeMillis();
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(5)
                .build();
        inventoryRepository.save(inventory);

        // Create a request to update the inventory
        InventoryRequest updateRequest = new InventoryRequest();
        updateRequest.setProductId(productId);
        updateRequest.setQuantity(15); // New quantity

        // When - call the API to update the inventory
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/inventory",
                updateRequest,
                String.class
        );

        // Then - verify the response status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // And verify the inventory was updated in the database
        Inventory updatedInventory = inventoryRepository.findByProductId(productId).orElse(null);
        assertThat(updatedInventory).isNotNull();
        assertThat(updatedInventory.getProductId()).isEqualTo(productId);
        assertThat(updatedInventory.getQuantity()).isEqualTo(15); // Should be updated to 15
    }

    @Test
    void shouldGetInventoryByProductId() {
        // Given
        Inventory inventory = Inventory.builder()
                .productId("PROD-002")
                .quantity(5)
                .build();
        inventoryRepository.save(inventory);

        // When
        ResponseEntity<InventoryResponse> responseEntity = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/inventory/PROD-002",
                InventoryResponse.class
        );

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getProductId()).isEqualTo("PROD-002");
        assertThat(responseEntity.getBody().getQuantity()).isEqualTo(5);
        assertThat(responseEntity.getBody().isInStock()).isTrue();
    }
}
