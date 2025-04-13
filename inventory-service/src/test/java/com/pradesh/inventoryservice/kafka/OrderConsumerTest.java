package com.pradesh.inventoryservice.kafka;

import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import com.pradesh.common.event.OrderCreatedEvent;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderConsumerTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryProducer inventoryProducer;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private OrderCreatedEvent orderCreatedEvent;
    private Inventory inventory;

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

        inventory = Inventory.builder()
                .id(1L)
                .productId("PROD-001")
                .quantity(10)
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldUpdateInventoryAndSendUpdatedEvent() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        doNothing().when(inventoryProducer).sendInventoryUpdatedEvent(any(InventoryUpdatedEvent.class));

        // When
        orderConsumer.consumeOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(1)).save(inventoryCaptor.capture());

        Inventory capturedInventory = inventoryCaptor.getValue();
        assertThat(capturedInventory.getQuantity()).isEqualTo(8); // 10 - 2

        ArgumentCaptor<InventoryUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryUpdatedEvent.class);
        verify(inventoryProducer, times(1)).sendInventoryUpdatedEvent(eventCaptor.capture());

        InventoryUpdatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(capturedEvent.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedEvent.getQuantity()).isEqualTo(2);
        assertThat(capturedEvent.getStatus()).isEqualTo("RESERVED");

        // Verify acknowledgment was called
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void shouldSendFailedEventWhenInsufficientStock() {
        // Given
        inventory.setQuantity(1); // Only 1 in stock, but order is for 2
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.of(inventory));
        doNothing().when(inventoryProducer).sendInventoryFailedEvent(any(InventoryFailedEvent.class));

        // When
        orderConsumer.consumeOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        verify(inventoryRepository, never()).save(any(Inventory.class));

        ArgumentCaptor<InventoryFailedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryFailedEvent.class);
        verify(inventoryProducer, times(1)).sendInventoryFailedEvent(eventCaptor.capture());

        InventoryFailedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(capturedEvent.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedEvent.getReason()).isEqualTo("Insufficient stock");

        // Verify acknowledgment was called
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void shouldSendFailedEventWhenProductNotFound() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.empty());
        doNothing().when(inventoryProducer).sendInventoryFailedEvent(any(InventoryFailedEvent.class));

        // When
        orderConsumer.consumeOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        verify(inventoryRepository, never()).save(any(Inventory.class));

        ArgumentCaptor<InventoryFailedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryFailedEvent.class);
        verify(inventoryProducer, times(1)).sendInventoryFailedEvent(eventCaptor.capture());

        InventoryFailedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(capturedEvent.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedEvent.getReason()).isEqualTo("Product not found in inventory");

        // Verify acknowledgment was called
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void shouldHandleExceptionAndSendFailedEvent() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenThrow(new RuntimeException("Database error"));
        doNothing().when(inventoryProducer).sendInventoryFailedEvent(any(InventoryFailedEvent.class));

        // When
        orderConsumer.consumeOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        verify(inventoryRepository, never()).save(any(Inventory.class));

        ArgumentCaptor<InventoryFailedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryFailedEvent.class);
        verify(inventoryProducer, times(1)).sendInventoryFailedEvent(eventCaptor.capture());

        InventoryFailedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(capturedEvent.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedEvent.getReason()).startsWith("Error processing inventory");

        // Verify acknowledgment was called
        verify(acknowledgment, times(1)).acknowledge();
    }
}
