package com.pradesh.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pradesh.orderservice.dto.OrderRequest;
import com.pradesh.orderservice.dto.OrderResponse;
import com.pradesh.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private List<OrderResponse> orderResponses;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest();
        orderRequest.setProductId("PROD-001");
        orderRequest.setQuantity(2);
        orderRequest.setUnitPrice(new BigDecimal("99.99"));

        orderResponse = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(2)
                .totalAmount(new BigDecimal("199.98"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        OrderResponse orderResponse2 = OrderResponse.builder()
                .id(2L)
                .orderNumber("ORD-002")
                .productId("PROD-002")
                .quantity(1)
                .totalAmount(new BigDecimal("49.99"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        orderResponses = Arrays.asList(orderResponse, orderResponse2);
    }

    @Test
    void shouldCreateOrder() throws Exception {
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-001")))
                .andExpect(jsonPath("$.productId", is("PROD-001")))
                .andExpect(jsonPath("$.quantity", is(2)))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(orderResponses);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].orderNumber", is("ORD-001")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].orderNumber", is("ORD-002")));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.orderNumber", is("ORD-001")))
                .andExpect(jsonPath("$.productId", is("PROD-001")));
    }
}
