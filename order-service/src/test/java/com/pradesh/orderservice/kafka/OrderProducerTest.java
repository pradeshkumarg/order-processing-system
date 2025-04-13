package com.pradesh.orderservice.kafka;

import com.pradesh.common.event.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @InjectMocks
    private OrderProducer orderProducer;

    private OrderCreatedEvent orderCreatedEvent;
    private CompletableFuture<SendResult<String, OrderCreatedEvent>> completableFuture;

    @BeforeEach
    void setUp() {
        orderCreatedEvent = OrderCreatedEvent.builder()
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .totalAmount(new BigDecimal("199.98"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        completableFuture = new CompletableFuture<>();
    }

    @Test
    void shouldSendOrderCreatedEvent() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(OrderCreatedEvent.class)))
                .thenReturn(completableFuture);

        // When
        orderProducer.sendOrderCreatedEvent(orderCreatedEvent);

        // Then
        verify(kafkaTemplate, times(1))
                .send("order-created", orderCreatedEvent.getOrderNumber(), orderCreatedEvent);
    }
}
