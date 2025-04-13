package com.pradesh.notificationservice.listener;

import com.pradesh.notificationservice.model.InventoryFailedEvent;
import com.pradesh.notificationservice.model.InventoryUpdatedEvent;
import com.pradesh.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private InventoryEventListener inventoryEventListener;

    @BeforeEach
    void setUp() {
        reset(notificationService, acknowledgment);
    }

    @Test
    void handleInventoryUpdated_ShouldProcessSuccessfully() {
        // Given
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .orderNumber("ORDER-123")
                .productId("PROD-456")
                .quantity(5)
                .status("RESERVED")
                .updatedAt(LocalDateTime.now())
                .build();

        // When
        inventoryEventListener.handleInventoryUpdated(event, acknowledgment);

        // Then
        verify(notificationService).sendInventoryUpdateNotification(
                "ORDER-123",
                "PROD-456",
                5,
                "RESERVED"
        );
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleInventoryFailed_ShouldProcessSuccessfully() {
        // Given
        InventoryFailedEvent event = InventoryFailedEvent.builder()
                .orderNumber("ORDER-123")
                .productId("PROD-456")
                .quantity(5)
                .reason("Insufficient stock")
                .failedAt(LocalDateTime.now())
                .build();

        // When
        inventoryEventListener.handleInventoryFailed(event, acknowledgment);

        // Then
        verify(notificationService).sendInventoryFailureNotification(
                "ORDER-123",
                "PROD-456",
                5,
                "Insufficient stock"
        );
        verify(acknowledgment).acknowledge();
    }

    @Test
    void handleInventoryUpdated_ShouldHandleException() {
        // Given
        InventoryUpdatedEvent event = InventoryUpdatedEvent.builder()
                .orderNumber("ORDER-123")
                .productId("PROD-456")
                .quantity(5)
                .status("RESERVED")
                .updatedAt(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Test exception"))
                .when(notificationService)
                .sendInventoryUpdateNotification(any(), any(), any(), any());

        // When
        inventoryEventListener.handleInventoryUpdated(event, acknowledgment);

        // Then
        verify(notificationService).sendInventoryUpdateNotification(
                "ORDER-123",
                "PROD-456",
                5,
                "RESERVED"
        );
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    void handleInventoryFailed_ShouldHandleException() {
        // Given
        InventoryFailedEvent event = InventoryFailedEvent.builder()
                .orderNumber("ORDER-123")
                .productId("PROD-456")
                .quantity(5)
                .reason("Insufficient stock")
                .failedAt(LocalDateTime.now())
                .build();

        doThrow(new RuntimeException("Test exception"))
                .when(notificationService)
                .sendInventoryFailureNotification(any(), any(), any(), any());

        // When
        inventoryEventListener.handleInventoryFailed(event, acknowledgment);

        // Then
        verify(notificationService).sendInventoryFailureNotification(
                "ORDER-123",
                "PROD-456",
                5,
                "Insufficient stock"
        );
        verify(acknowledgment, never()).acknowledge();
    }
} 