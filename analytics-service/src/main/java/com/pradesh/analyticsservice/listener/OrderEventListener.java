package com.pradesh.analyticsservice.listener;

import com.pradesh.analyticsservice.model.OrderEvent;
import com.pradesh.analyticsservice.service.AnalyticsService;
import com.pradesh.common.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class OrderEventListener {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "${kafka.topics.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderCreatedEvent(OrderCreatedEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received order created event for order: {}", event.getOrderNumber());

            OrderEvent orderEvent = OrderEvent.builder()
                    .orderNumber(event.getOrderNumber())
                    .productId(event.getProductId())
                    .quantity(event.getQuantity())
                    .price(event.getTotalAmount())
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            analyticsService.processOrderEvent(orderEvent);

            acknowledgment.acknowledge();
            log.info("Order created event processed successfully for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Error processing order created event for order: {}", event.getOrderNumber(), e);
            // In a real application, you might want to implement a dead letter queue or retry mechanism here
            acknowledgment.acknowledge(); // Acknowledge to prevent redelivery
        }
    }
}
