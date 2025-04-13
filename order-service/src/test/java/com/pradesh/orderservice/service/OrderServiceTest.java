package com.pradesh.orderservice.service;

import com.pradesh.common.event.OrderCreatedEvent;
import com.pradesh.orderservice.dto.OrderRequest;
import com.pradesh.orderservice.dto.OrderResponse;
import com.pradesh.orderservice.kafka.OrderProducer;
import com.pradesh.orderservice.model.Order;
import com.pradesh.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequest orderRequest;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setProductId("PROD-001");
        orderRequest.setQuantity(2);
        orderRequest.setUnitPrice(new BigDecimal("99.99"));

        savedOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .totalAmount(new BigDecimal("199.98"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateOrder() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderProducer).sendOrderCreatedEvent(any(OrderCreatedEvent.class));

        // When
        OrderResponse response = orderService.createOrder(orderRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(response.getProductId()).isEqualTo("PROD-001");
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalAmount()).isEqualTo(new BigDecimal("199.98"));
        assertThat(response.getStatus()).isEqualTo("PENDING");

        verify(orderRepository, times(1)).save(any(Order.class));
        
        ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(orderProducer, times(1)).sendOrderCreatedEvent(eventCaptor.capture());
        
        OrderCreatedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getOrderNumber()).isEqualTo("ORD-001");
        assertThat(capturedEvent.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedEvent.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldGetAllOrders() {
        // Given
        Order order2 = Order.builder()
                .id(2L)
                .orderNumber("ORD-002")
                .productId("PROD-002")
                .quantity(1)
                .totalAmount(new BigDecimal("49.99"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        
        when(orderRepository.findAll()).thenReturn(Arrays.asList(savedOrder, order2));

        // When
        List<OrderResponse> responses = orderService.getAllOrders();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getId()).isEqualTo(1L);
        assertThat(responses.get(1).getId()).isEqualTo(2L);
        
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void shouldGetOrderById() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        // When
        OrderResponse response = orderService.getOrderById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getOrderNumber()).isEqualTo("ORD-001");
        
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
        
        verify(orderRepository, times(1)).findById(999L);
    }
}
