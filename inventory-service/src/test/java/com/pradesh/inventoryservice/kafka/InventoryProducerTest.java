package com.pradesh.inventoryservice.kafka;

import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private InventoryProducer inventoryProducer;

    private InventoryUpdatedEvent updatedEvent;
    private InventoryFailedEvent failedEvent;
    private CompletableFuture<SendResult<String, Object>> completableFuture;

    @BeforeEach
    void setUp() {
        updatedEvent = InventoryUpdatedEvent.builder()
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .status("RESERVED")
                .updatedAt(LocalDateTime.now())
                .build();

        failedEvent = InventoryFailedEvent.builder()
                .orderNumber("ORD-002")
                .productId("PROD-002")
                .quantity(3)
                .reason("Insufficient stock")
                .failedAt(LocalDateTime.now())
                .build();

        completableFuture = new CompletableFuture<>();
    }

    @Test
    void shouldSendInventoryUpdatedEvent() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(InventoryUpdatedEvent.class)))
                .thenReturn(completableFuture);

        // When
        inventoryProducer.sendInventoryUpdatedEvent(updatedEvent);

        // Then
        verify(kafkaTemplate, times(1))
                .send("inventory-updated", updatedEvent.getOrderNumber(), updatedEvent);
    }

    @Test
    void shouldSendInventoryFailedEvent() {
        // Given
        when(kafkaTemplate.send(anyString(), anyString(), any(InventoryFailedEvent.class)))
                .thenReturn(completableFuture);

        // When
        inventoryProducer.sendInventoryFailedEvent(failedEvent);

        // Then
        verify(kafkaTemplate, times(1))
                .send("inventory-failed", failedEvent.getOrderNumber(), failedEvent);
    }
}
