package com.pradesh.analyticsservice.listener;

import com.pradesh.analyticsservice.model.OrderEvent;
import com.pradesh.analyticsservice.service.AnalyticsService;
import com.pradesh.common.event.OrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private OrderEventListener orderEventListener;

    @Captor
    private ArgumentCaptor<OrderEvent> orderEventCaptor;

    private OrderCreatedEvent orderCreatedEvent;

    @BeforeEach
    void setUp() {
        orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.setOrderNumber("ORD-001");
        orderCreatedEvent.setProductId("PROD-001");
        orderCreatedEvent.setQuantity(5);
        orderCreatedEvent.setTotalAmount(new BigDecimal("100.00"));
    }

    @Test
    void handleOrderCreatedEvent_ShouldProcessOrderEventAndAcknowledge() {
        // When
        orderEventListener.handleOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processOrderEvent(orderEventCaptor.capture());
        verify(acknowledgment, times(1)).acknowledge();

        OrderEvent capturedOrderEvent = orderEventCaptor.getValue();
        assertNotNull(capturedOrderEvent);
        assertEquals("ORD-001", capturedOrderEvent.getOrderNumber());
        assertEquals("PROD-001", capturedOrderEvent.getProductId());
        assertEquals(5, capturedOrderEvent.getQuantity());
        assertEquals(new BigDecimal("100.00"), capturedOrderEvent.getPrice());
        assertEquals("PENDING", capturedOrderEvent.getStatus());
        assertNotNull(capturedOrderEvent.getCreatedAt());
    }

    @Test
    void handleOrderCreatedEvent_WhenExceptionOccurs_ShouldAcknowledgeAndLogError() {
        // Given
        doThrow(new RuntimeException("Test exception")).when(analyticsService).processOrderEvent(any(OrderEvent.class));

        // When
        orderEventListener.handleOrderCreatedEvent(orderCreatedEvent, acknowledgment);

        // Then
        verify(analyticsService, times(1)).processOrderEvent(any(OrderEvent.class));
        verify(acknowledgment, times(1)).acknowledge();
    }
}
