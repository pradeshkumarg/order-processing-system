package com.pradesh.inventoryservice.kafka;

import com.pradesh.common.event.OrderCreatedEvent;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order-created", "inventory-updated", "inventory-failed"})
@ActiveProfiles("test")
public class KafkaConsumerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    private String productId;
    private int initialQuantity = 10;

    @BeforeEach
    void setUp() {
        // Create a unique product ID for each test
        productId = "TEST-KAFKA-" + System.currentTimeMillis();

        // Create inventory with initial quantity
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(initialQuantity)
                .lastUpdated(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);
    }

    @AfterEach
    void tearDown() {
        try {
            // Clean up after test
            inventoryRepository.deleteAllInBatch();
        } catch (Exception e) {
            System.err.println("Error in tearDown: " + e.getMessage());
        }
    }

    @Test
    void shouldProcessOrderCreatedEventAndUpdateInventory() {
        // Given - an order created event with our test product
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderNumber(UUID.randomUUID().toString())
                .productId(productId)
                .quantity(2) // Order 2 items
                .totalAmount(new BigDecimal("199.98"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        // When - send the event to Kafka
        kafkaTemplate.send("order-created", orderCreatedEvent);

        // Then - verify the inventory was updated (with retry for async processing)
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Inventory updatedInventory = inventoryRepository.findByProductId(productId).orElse(null);
            assertThat(updatedInventory).isNotNull();
            assertThat(updatedInventory.getQuantity()).isEqualTo(initialQuantity - 2);
        });
    }

    @Test
    void shouldSendInventoryFailedEventWhenInsufficientStock() {
        // Given - an order created event with quantity greater than available
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderNumber(UUID.randomUUID().toString())
                .productId(productId)
                .quantity(initialQuantity + 5) // Order more than available
                .totalAmount(new BigDecimal("499.95"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        // When - send the event to Kafka
        kafkaTemplate.send("order-created", orderCreatedEvent);

        // Then - verify the inventory was not updated (with retry for async processing)
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);
            assertThat(inventory).isNotNull();
            assertThat(inventory.getQuantity()).isEqualTo(initialQuantity); // Quantity should remain unchanged
        });

        // Note: In a more complete test, we would also verify that an InventoryFailedEvent was sent
        // This would require a test consumer for the inventory-failed topic
    }
}
