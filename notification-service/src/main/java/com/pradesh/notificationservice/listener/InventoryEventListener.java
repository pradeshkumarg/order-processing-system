package com.pradesh.notificationservice.listener;

import com.pradesh.notificationservice.model.InventoryFailedEvent;
import com.pradesh.notificationservice.model.InventoryUpdatedEvent;
import com.pradesh.notificationservice.service.NotificationService;
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

    private final NotificationService notificationService;

    @KafkaListener(
        id = "inventory-updated-listener",
        topics = "${kafka.topics.inventory-updated}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryUpdated(InventoryUpdatedEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received inventory update event for order: {}", event.getOrderNumber());

            notificationService.sendInventoryUpdateNotification(
                event.getOrderNumber(),
                event.getProductId(),
                event.getQuantity(),
                event.getStatus()
            );

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing inventory update event for order: {}", event.getOrderNumber(), e);
            // In a real application, you might want to implement a dead letter queue or retry mechanism here
        }
    }

    @KafkaListener(
        id = "inventory-failed-listener",
        topics = "${kafka.topics.inventory-failed}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleInventoryFailed(InventoryFailedEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received inventory failure event for order: {}", event.getOrderNumber());

            notificationService.sendInventoryFailureNotification(
                event.getOrderNumber(),
                event.getProductId(),
                event.getQuantity(),
                event.getReason()
            );

            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing inventory failure event for order: {}", event.getOrderNumber(), e);
            // In a real application, you might want to implement a dead letter queue or retry mechanism here
        }
    }
}