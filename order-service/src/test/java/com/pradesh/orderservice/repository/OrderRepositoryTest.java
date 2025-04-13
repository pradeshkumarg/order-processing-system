package com.pradesh.orderservice.repository;

import com.pradesh.orderservice.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void shouldSaveOrder() {
        // Given
        Order order = Order.builder()
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .totalAmount(new BigDecimal("199.98"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        Order savedOrder = orderRepository.save(order);

        // Then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getOrderNumber()).isEqualTo("ORD-001");
    }

    @Test
    public void shouldFindOrderById() {
        // Given
        Order order = Order.builder()
                .orderNumber("ORD-002")
                .productId("PROD-002")
                .quantity(1)
                .totalAmount(new BigDecimal("99.99"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        Order savedOrder = orderRepository.save(order);

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getOrderNumber()).isEqualTo("ORD-002");
    }

    @Test
    public void shouldFindAllOrders() {
        // Given
        Order order1 = Order.builder()
                .orderNumber("ORD-003")
                .productId("PROD-003")
                .quantity(3)
                .totalAmount(new BigDecimal("299.97"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        
        Order order2 = Order.builder()
                .orderNumber("ORD-004")
                .productId("PROD-004")
                .quantity(1)
                .totalAmount(new BigDecimal("49.99"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        
        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        List<Order> orders = orderRepository.findAll();

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders.size()).isGreaterThanOrEqualTo(2);
    }
}
