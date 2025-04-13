package com.pradesh.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Spy
    private NotificationService notificationService;

    @Test
    void sendInventoryUpdateNotification_ShouldLogSuccessfully() {
        // Given
        String orderNumber = "ORDER-123";
        String productId = "PROD-456";
        Integer quantity = 5;
        String status = "RESERVED";

        // When
        notificationService.sendInventoryUpdateNotification(orderNumber, productId, quantity, status);

        // Then
        verify(notificationService).sendInventoryUpdateNotification(orderNumber, productId, quantity, status);
    }

    @Test
    void sendInventoryFailureNotification_ShouldLogSuccessfully() {
        // Given
        String orderNumber = "ORDER-123";
        String productId = "PROD-456";
        Integer quantity = 5;
        String reason = "Insufficient stock";

        // When
        notificationService.sendInventoryFailureNotification(orderNumber, productId, quantity, reason);

        // Then
        verify(notificationService).sendInventoryFailureNotification(orderNumber, productId, quantity, reason);
    }
} 