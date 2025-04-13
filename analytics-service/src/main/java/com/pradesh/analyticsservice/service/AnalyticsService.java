package com.pradesh.analyticsservice.service;

import com.pradesh.analyticsservice.dto.DashboardData;
import com.pradesh.analyticsservice.dto.OrderAnalytics;
import com.pradesh.analyticsservice.dto.ProductAnalyticsDTO;
import com.pradesh.analyticsservice.model.InventoryEvent;
import com.pradesh.analyticsservice.model.OrderEvent;
import com.pradesh.analyticsservice.model.ProductAnalytics;
import com.pradesh.analyticsservice.repository.InventoryEventRepository;
import com.pradesh.analyticsservice.repository.OrderEventRepository;
import com.pradesh.analyticsservice.repository.ProductAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final OrderEventRepository orderEventRepository;
    private final InventoryEventRepository inventoryEventRepository;
    private final ProductAnalyticsRepository productAnalyticsRepository;

    @Transactional
    public void processOrderEvent(OrderEvent orderEvent) {
        log.info("Processing order event for order: {}", orderEvent.getOrderNumber());
        
        // Save the order event
        orderEventRepository.save(orderEvent);
        
        // Update product analytics
        String productId = orderEvent.getProductId();
        ProductAnalytics productAnalytics = productAnalyticsRepository.findById(productId)
                .orElse(ProductAnalytics.builder()
                        .productId(productId)
                        .totalOrdered(0)
                        .totalFulfilled(0)
                        .totalFailed(0)
                        .totalRevenue(BigDecimal.ZERO)
                        .build());
        
        productAnalytics.updateWithNewOrder(orderEvent.getQuantity(), orderEvent.getPrice());
        productAnalyticsRepository.save(productAnalytics);
        
        log.info("Order event processed successfully for order: {}", orderEvent.getOrderNumber());
    }
    
    @Transactional
    public void processInventoryEvent(InventoryEvent inventoryEvent) {
        log.info("Processing inventory event for order: {}, type: {}", 
                inventoryEvent.getOrderNumber(), inventoryEvent.getEventType());
        
        // Save the inventory event
        inventoryEventRepository.save(inventoryEvent);
        
        // Update order status
        Optional<OrderEvent> orderEventOptional = orderEventRepository.findByOrderNumber(inventoryEvent.getOrderNumber());
        if (orderEventOptional.isPresent()) {
            OrderEvent orderEvent = orderEventOptional.get();
            
            if ("UPDATED".equals(inventoryEvent.getEventType())) {
                orderEvent.setStatus("COMPLETED");
                orderEvent.setUpdatedAt(LocalDateTime.now());
                orderEventRepository.save(orderEvent);
                
                // Update product analytics for fulfilled order
                updateProductAnalyticsForFulfilledOrder(orderEvent);
            } else if ("FAILED".equals(inventoryEvent.getEventType())) {
                orderEvent.setStatus("FAILED");
                orderEvent.setUpdatedAt(LocalDateTime.now());
                orderEventRepository.save(orderEvent);
                
                // Update product analytics for failed order
                updateProductAnalyticsForFailedOrder(orderEvent);
            }
        } else {
            log.warn("Order not found for inventory event: {}", inventoryEvent.getOrderNumber());
        }
        
        log.info("Inventory event processed successfully for order: {}", inventoryEvent.getOrderNumber());
    }
    
    private void updateProductAnalyticsForFulfilledOrder(OrderEvent orderEvent) {
        ProductAnalytics productAnalytics = productAnalyticsRepository.findById(orderEvent.getProductId())
                .orElseThrow(() -> new RuntimeException("Product analytics not found for product: " + orderEvent.getProductId()));
        
        productAnalytics.updateWithFulfilledOrder(orderEvent.getQuantity(), orderEvent.getPrice());
        productAnalyticsRepository.save(productAnalytics);
    }
    
    private void updateProductAnalyticsForFailedOrder(OrderEvent orderEvent) {
        ProductAnalytics productAnalytics = productAnalyticsRepository.findById(orderEvent.getProductId())
                .orElseThrow(() -> new RuntimeException("Product analytics not found for product: " + orderEvent.getProductId()));
        
        productAnalytics.updateWithFailedOrder(orderEvent.getQuantity());
        productAnalyticsRepository.save(productAnalytics);
    }
    
    public DashboardData getDashboardData() {
        log.info("Retrieving dashboard data");
        
        Long totalOrders = orderEventRepository.countTotalOrders();
        Long completedOrders = orderEventRepository.countCompletedOrders();
        Long failedOrders = orderEventRepository.countFailedOrders();
        BigDecimal totalRevenue = productAnalyticsRepository.calculateTotalRevenue();
        
        List<ProductAnalyticsDTO> topProducts = productAnalyticsRepository.findTopOrderedProducts().stream()
                .limit(5)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return DashboardData.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .failedOrders(failedOrders)
                .totalRevenue(totalRevenue)
                .topProducts(topProducts)
                .build();
    }
    
    public OrderAnalytics getOrderAnalytics() {
        log.info("Retrieving order analytics");
        
        Long totalOrders = orderEventRepository.countTotalOrders();
        Long completedOrders = orderEventRepository.countCompletedOrders();
        Long failedOrders = orderEventRepository.countFailedOrders();
        
        Long successfulInventoryEvents = inventoryEventRepository.countSuccessfulInventoryEvents();
        Long failedInventoryEvents = inventoryEventRepository.countFailedInventoryEvents();
        
        return OrderAnalytics.builder()
                .totalOrders(totalOrders)
                .completedOrders(completedOrders)
                .failedOrders(failedOrders)
                .successRate(calculateSuccessRate(completedOrders, totalOrders))
                .failureRate(calculateFailureRate(failedOrders, totalOrders))
                .successfulInventoryEvents(successfulInventoryEvents)
                .failedInventoryEvents(failedInventoryEvents)
                .build();
    }
    
    public List<ProductAnalyticsDTO> getProductAnalytics() {
        log.info("Retrieving product analytics");
        
        return productAnalyticsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private ProductAnalyticsDTO convertToDTO(ProductAnalytics productAnalytics) {
        return ProductAnalyticsDTO.builder()
                .productId(productAnalytics.getProductId())
                .totalOrdered(productAnalytics.getTotalOrdered())
                .totalFulfilled(productAnalytics.getTotalFulfilled())
                .totalFailed(productAnalytics.getTotalFailed())
                .totalRevenue(productAnalytics.getTotalRevenue())
                .successRate(calculateSuccessRate(productAnalytics.getTotalFulfilled(), productAnalytics.getTotalOrdered()))
                .build();
    }
    
    private double calculateSuccessRate(long completed, long total) {
        return total > 0 ? (double) completed / total * 100 : 0;
    }
    
    private double calculateFailureRate(long failed, long total) {
        return total > 0 ? (double) failed / total * 100 : 0;
    }
}
