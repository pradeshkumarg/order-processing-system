package com.pradesh.analyticsservice.listener;

import com.pradesh.analyticsservice.model.InventoryEvent;
import com.pradesh.analyticsservice.service.AnalyticsService;
import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class InventoryEventListener {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${kafka.topics.inventory-updated}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryUpdatedEvent(InventoryUpdatedEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received inventory updated event for order: {}", event.getOrderNumber());
            
            InventoryEvent inventoryEvent = InventoryEvent.builder()
                    .orderNumber(event.getOrderNumber())
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .eventType("UPDATED")
                    .status(event.getStatus())
                    .timestamp(event.getUpdatedAt())
                    .build();
            
            analyticsService.processInventoryEvent(inventoryEvent);
            
            acknowledgment.acknowledge();
            log.info("Inventory updated event processed successfully for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Error processing inventory updated event for order: {}", event.getOrderNumber(), e);
            // In a real application, you might want to implement a dead letter queue or retry mechanism here
            acknowledgment.acknowledge(); // Acknowledge to prevent redelivery
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.inventory-failed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryFailedEvent(InventoryFailedEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received inventory failed event for order: {}", event.getOrderNumber());
            
            InventoryEvent inventoryEvent = InventoryEvent.builder()
                    .orderNumber(event.getOrderNumber())
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .eventType("FAILED")
                    .status("FAILED")
                    .reason(event.getReason())
                    .timestamp(event.getFailedAt())
                    .build();
            
            analyticsService.processInventoryEvent(inventoryEvent);
            
            acknowledgment.acknowledge();
            log.info("Inventory failed event processed successfully for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Error processing inventory failed event for order: {}", event.getOrderNumber(), e);
            // In a real application, you might want to implement a dead letter queue or retry mechanism here
            acknowledgment.acknowledge(); // Acknowledge to prevent redelivery
        }
    }
}
