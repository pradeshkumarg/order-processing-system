package com.pradesh.orderservice.service;

import com.pradesh.orderservice.dto.OrderRequest;
import com.pradesh.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
}
