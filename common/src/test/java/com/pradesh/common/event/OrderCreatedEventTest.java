package com.pradesh.common.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderCreatedEventTest {

    @Test
    public void testOrderCreatedEventBuilder() {
        // Given
        String orderNumber = "ORD-001";
        String productId = "PROD-001";
        Integer quantity = 2;
        BigDecimal totalAmount = new BigDecimal("199.98");
        String status = "PENDING";
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderNumber(orderNumber)
                .productId(productId)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status(status)
                .createdAt(createdAt)
                .build();

        // Then
        assertNotNull(event);
        assertEquals(orderNumber, event.getOrderNumber());
        assertEquals(productId, event.getProductId());
        assertEquals(quantity, event.getQuantity());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals(status, event.getStatus());
        assertEquals(createdAt, event.getCreatedAt());
    }

    @Test
    public void testOrderCreatedEventGettersAndSetters() {
        // Given
        OrderCreatedEvent event = new OrderCreatedEvent();
        String orderNumber = "ORD-002";
        String productId = "PROD-002";
        Integer quantity = 3;
        BigDecimal totalAmount = new BigDecimal("299.97");
        String status = "PENDING";
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        event.setOrderNumber(orderNumber);
        event.setProductId(productId);
        event.setQuantity(quantity);
        event.setTotalAmount(totalAmount);
        event.setStatus(status);
        event.setCreatedAt(createdAt);

        // Then
        assertEquals(orderNumber, event.getOrderNumber());
        assertEquals(productId, event.getProductId());
        assertEquals(quantity, event.getQuantity());
        assertEquals(totalAmount, event.getTotalAmount());
        assertEquals(status, event.getStatus());
        assertEquals(createdAt, event.getCreatedAt());
    }
}
