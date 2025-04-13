package com.pradesh.orderservice.integration;

import com.pradesh.orderservice.dto.OrderRequest;
import com.pradesh.orderservice.dto.OrderResponse;
import com.pradesh.orderservice.model.Order;
import com.pradesh.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = {"order-created"})
@ActiveProfiles("test")
public class OrderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateOrderAndReturnResponse() {
        // Given
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("PROD-001");
        orderRequest.setQuantity(2);
        orderRequest.setUnitPrice(new BigDecimal("99.99"));

        // When
        ResponseEntity<OrderResponse> responseEntity = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/orders",
                orderRequest,
                OrderResponse.class
        );

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getProductId()).isEqualTo("PROD-001");
        assertThat(responseEntity.getBody().getQuantity()).isEqualTo(2);
        assertThat(responseEntity.getBody().getTotalAmount()).isEqualTo(new BigDecimal("199.98"));
        assertThat(responseEntity.getBody().getStatus()).isEqualTo("PENDING");

        // Verify order was saved to database
        Order savedOrder = orderRepository.findById(responseEntity.getBody().getId()).orElse(null);
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getProductId()).isEqualTo("PROD-001");
        assertThat(savedOrder.getQuantity()).isEqualTo(2);
    }
}
