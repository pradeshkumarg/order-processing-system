package com.pradesh.inventoryservice.kafka;

import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import com.pradesh.common.event.OrderCreatedEvent;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class OrderConsumer {

    private final InventoryRepository inventoryRepository;
    private final InventoryProducer inventoryProducer;

    @KafkaListener(topics = "order-created", groupId = "inventory-group")
    @Transactional
    public void consumeOrderCreatedEvent(OrderCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Order event received for order: {}, product: {}, quantity: {}",
                event.getOrderNumber(), event.getProductId(), event.getQuantity());

        try {
            // Check if product exists in inventory
            Optional<Inventory> inventoryOptional = inventoryRepository.findByProductId(event.getProductId());

            if (inventoryOptional.isPresent()) {
                Inventory inventory = inventoryOptional.get();

                // Check if there's enough stock
                if (inventory.getQuantity() >= event.getQuantity()) {
                    // Update inventory
                    inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
                    inventory.setLastUpdated(LocalDateTime.now());
                    inventoryRepository.save(inventory);

                    // Send inventory updated event
                    InventoryUpdatedEvent updatedEvent = InventoryUpdatedEvent.builder()
                            .orderNumber(event.getOrderNumber())
                            .productId(event.getProductId())
                            .quantity(event.getQuantity())
                            .status("RESERVED")
                            .updatedAt(LocalDateTime.now())
                            .build();

                    inventoryProducer.sendInventoryUpdatedEvent(updatedEvent);
                    log.info("Inventory updated for product: {}, new quantity: {}",
                            event.getProductId(), inventory.getQuantity());

                    // Acknowledge successful processing
                    acknowledgment.acknowledge();
                } else {
                    // Not enough stock
                    sendInventoryFailedEvent(event, "Insufficient stock");
                    log.warn("Insufficient stock for product: {}, requested: {}, available: {}",
                            event.getProductId(), event.getQuantity(), inventory.getQuantity());

                    // Acknowledge the message even though we couldn't process it
                    // This is because the issue is with the business logic, not the message itself
                    acknowledgment.acknowledge();
                }
            } else {
                // Product not found in inventory
                sendInventoryFailedEvent(event, "Product not found in inventory");
                log.warn("Product not found in inventory: {}", event.getProductId());

                // Acknowledge the message
                acknowledgment.acknowledge();
            }
        } catch (Exception e) {
            // Handle any exceptions
            sendInventoryFailedEvent(event, "Error processing inventory: " + e.getMessage());
            log.error("Error processing inventory for order: {}", event.getOrderNumber(), e);

            // In case of exception, we still acknowledge the message to prevent redelivery
            // In a real-world scenario, you might want to implement a dead-letter queue instead
            acknowledgment.acknowledge();
        }
    }

    private void sendInventoryFailedEvent(OrderCreatedEvent orderEvent, String reason) {
        InventoryFailedEvent failedEvent = InventoryFailedEvent.builder()
                .orderNumber(orderEvent.getOrderNumber())
                .productId(orderEvent.getProductId())
                .quantity(orderEvent.getQuantity())
                .reason(reason)
                .failedAt(LocalDateTime.now())
                .build();

        inventoryProducer.sendInventoryFailedEvent(failedEvent);
    }
}
