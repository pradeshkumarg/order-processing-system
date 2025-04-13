package com.pradesh.analyticsservice.listener;

import com.pradesh.analyticsservice.model.InventoryEvent;
import com.pradesh.analyticsservice.service.AnalyticsService;
import com.pradesh.common.event.InventoryFailedEvent;
import com.pradesh.common.event.InventoryUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventListenerTest {

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private InventoryEventListener inventoryEventListener;

    @Captor
    private ArgumentCaptor<InventoryEvent> inventoryEventCaptor;

    private InventoryUpdatedEvent inventoryUpdatedEvent;
    private InventoryFailedEvent inventoryFailedEvent;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        inventoryUpdatedEvent = new InventoryUpdatedEvent();
        inventoryUpdatedEvent.setOrderNumber("ORD-001");
        inventoryUpdatedEvent.setProductId("PROD-001");
        inventoryUpdatedEvent.setQuantity(5);
        inventoryUpdatedEvent.setStatus("RESERVED");
        inventoryUpdatedEvent.setUpdatedAt(now);
        
        inventoryFailedEvent = new InventoryFailedEvent();
        inventoryFailedEvent.setOrderNumber("ORD-001");
        inventoryFailedEvent.setProductId("PROD-001");
        inventoryFailedEvent.setQuantity(5);
        inventoryFailedEvent.setReason("Out of stock");
        inventoryFailedEvent.setFailedAt(now);
    }

    @Test
    void handleInventoryUpdatedEvent_ShouldProcessInventoryEventAndAcknowledge() {
        // When
        inventoryEventListener.handleInventoryUpdatedEvent(inventoryUpdatedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processInventoryEvent(inventoryEventCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();

        InventoryEvent capturedEvent = inventoryEventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("ORD-001", capturedEvent.getOrderNumber());
        assertEquals("PROD-001", capturedEvent.getProductId());
        assertEquals(5, capturedEvent.getQuantity());
        assertEquals("UPDATED", capturedEvent.getEventType());
        assertEquals("RESERVED", capturedEvent.getStatus());
        assertEquals(now, capturedEvent.getTimestamp());
    }

    @Test
    void handleInventoryUpdatedEvent_WhenExceptionOccurs_ShouldAcknowledgeAndLogError() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(analyticsService).processInventoryEvent(any(InventoryEvent.class));

        // When
        inventoryEventListener.handleInventoryUpdatedEvent(inventoryUpdatedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processInventoryEvent(any(InventoryEvent.class));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void handleInventoryFailedEvent_ShouldProcessInventoryEventAndAcknowledge() {
        // When
        inventoryEventListener.handleInventoryFailedEvent(inventoryFailedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processInventoryEvent(inventoryEventCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();

        InventoryEvent capturedEvent = inventoryEventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals("ORD-001", capturedEvent.getOrderNumber());
        assertEquals("PROD-001", capturedEvent.getProductId());
        assertEquals(5, capturedEvent.getQuantity());
        assertEquals("FAILED", capturedEvent.getEventType());
        assertEquals("FAILED", capturedEvent.getStatus());
        assertEquals("Out of stock", capturedEvent.getReason());
        assertEquals(now, capturedEvent.getTimestamp());
    }

    @Test
    void handleInventoryFailedEvent_WhenExceptionOccurs_ShouldAcknowledgeAndLogError() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(analyticsService).processInventoryEvent(any(InventoryEvent.class));

        // When
        inventoryEventListener.handleInventoryFailedEvent(inventoryFailedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processInventoryEvent(any(InventoryEvent.class));
        verify(acknowledgment, times(1)).acknowledge();
    }
}
