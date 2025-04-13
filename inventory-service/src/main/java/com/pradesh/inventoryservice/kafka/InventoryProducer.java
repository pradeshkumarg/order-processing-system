package com.pradesh.inventoryservice.kafka;

import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class InventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendInventoryUpdatedEvent(InventoryUpdatedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("inventory-updated", event.getOrderNumber(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Inventory updated event sent successfully for order: {}",
                        event.getOrderNumber());
                log.debug("Partition: {}, Offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send inventory updated event for order: {}",
                        event.getOrderNumber(), ex);
            }
        });
    }

    public void sendInventoryFailedEvent(InventoryFailedEvent event) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send("inventory-failed", event.getOrderNumber(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Inventory failed event sent successfully for order: {}",
                        event.getOrderNumber());
                log.debug("Partition: {}, Offset: {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send inventory failed event for order: {}",
                        event.getOrderNumber(), ex);
            }
        });
    }
}
