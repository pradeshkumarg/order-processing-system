package com.pradesh.orderservice.service;

import com.pradesh.common.event.OrderCreatedEvent;
import com.pradesh.orderservice.dto.OrderRequest;
import com.pradesh.orderservice.dto.OrderResponse;
import com.pradesh.orderservice.kafka.OrderProducer;
import com.pradesh.orderservice.model.Order;
import com.pradesh.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        // Calculate total amount
        BigDecimal totalAmount = orderRequest.getUnitPrice()
                .multiply(BigDecimal.valueOf(orderRequest.getQuantity()));

        // Create order entity
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .productId(orderRequest.getProductId())
                .quantity(orderRequest.getQuantity())
                .totalAmount(totalAmount)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        // Save order to database
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with ID: {}", savedOrder.getId());

        // Create and send Kafka event
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderNumber(savedOrder.getOrderNumber())
                .productId(savedOrder.getProductId())
                .quantity(savedOrder.getQuantity())
                .totalAmount(savedOrder.getTotalAmount())
                .status(savedOrder.getStatus())
                .createdAt(savedOrder.getCreatedAt())
                .build();

        orderProducer.sendOrderCreatedEvent(orderCreatedEvent);

        // Return response DTO
        return mapToOrderResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    private String generateOrderNumber() {
        // Simple order number generation using UUID
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
