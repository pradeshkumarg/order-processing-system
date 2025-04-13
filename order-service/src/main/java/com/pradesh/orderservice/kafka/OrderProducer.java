package com.pradesh.orderservice.kafka;

import com.pradesh.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        CompletableFuture<SendResult<String, OrderCreatedEvent>> future = 
                kafkaTemplate.send("order-created", event.getOrderNumber(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order event sent successfully for order number: {}", 
                        event.getOrderNumber());
                log.debug("Partition: {}, Offset: {}", 
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send order event for order number: {}", 
                        event.getOrderNumber(), ex);
            }
        });
    }
}
