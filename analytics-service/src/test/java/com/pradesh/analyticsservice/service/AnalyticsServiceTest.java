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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderEventRepository orderEventRepository;

    @Mock
    private InventoryEventRepository inventoryEventRepository;

    @Mock
    private ProductAnalyticsRepository productAnalyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private OrderEvent orderEvent;
    private InventoryEvent inventoryEvent;
    private ProductAnalytics productAnalytics;

    @BeforeEach
    void setUp() {
        orderEvent = OrderEvent.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(5)
                .price(new BigDecimal("100.00"))
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        inventoryEvent = InventoryEvent.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .productId("PROD-001")
                .quantity(5)
                .eventType("UPDATED")
                .status("RESERVED")
                .timestamp(LocalDateTime.now())
                .build();

        productAnalytics = ProductAnalytics.builder()
                .productId("PROD-001")
                .totalOrdered(10)
                .totalFulfilled(8)
                .totalFailed(2)
                .totalRevenue(new BigDecimal("800.00"))
                .build();
    }

    @Test
    void processOrderEvent_ShouldSaveOrderEventAndUpdateProductAnalytics() {
        // Given
        when(productAnalyticsRepository.findById("PROD-001")).thenReturn(Optional.of(productAnalytics));
        when(productAnalyticsRepository.save(any(ProductAnalytics.class))).thenReturn(productAnalytics);
        when(orderEventRepository.save(any(OrderEvent.class))).thenReturn(orderEvent);

        // When
        analyticsService.processOrderEvent(orderEvent);

        // Then
        verify(orderEventRepository, times(1)).save(orderEvent);
        verify(productAnalyticsRepository, times(1)).findById("PROD-001");
        verify(productAnalyticsRepository, times(1)).save(any(ProductAnalytics.class));
    }

    @Test
    void processOrderEvent_ShouldCreateNewProductAnalyticsIfNotExists() {
        // Given
        when(productAnalyticsRepository.findById("PROD-001")).thenReturn(Optional.empty());
        when(productAnalyticsRepository.save(any(ProductAnalytics.class))).thenReturn(productAnalytics);
        when(orderEventRepository.save(any(OrderEvent.class))).thenReturn(orderEvent);

        // When
        analyticsService.processOrderEvent(orderEvent);

        // Then
        verify(orderEventRepository, times(1)).save(orderEvent);
        verify(productAnalyticsRepository, times(1)).findById("PROD-001");
        verify(productAnalyticsRepository, times(1)).save(any(ProductAnalytics.class));
    }

    @Test
    void processInventoryEvent_UpdatedEvent_ShouldUpdateOrderStatusAndProductAnalytics() {
        // Given
        when(inventoryEventRepository.save(any(InventoryEvent.class))).thenReturn(inventoryEvent);
        when(orderEventRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(orderEvent));
        when(orderEventRepository.save(any(OrderEvent.class))).thenReturn(orderEvent);
        when(productAnalyticsRepository.findById("PROD-001")).thenReturn(Optional.of(productAnalytics));
        when(productAnalyticsRepository.save(any(ProductAnalytics.class))).thenReturn(productAnalytics);

        // When
        analyticsService.processInventoryEvent(inventoryEvent);

        // Then
        verify(inventoryEventRepository, times(1)).save(inventoryEvent);
        verify(orderEventRepository, times(1)).findByOrderNumber("ORD-001");
        verify(orderEventRepository, times(1)).save(any(OrderEvent.class));
        verify(productAnalyticsRepository, times(1)).findById("PROD-001");
        verify(productAnalyticsRepository, times(1)).save(any(ProductAnalytics.class));
        
        assertEquals("COMPLETED", orderEvent.getStatus());
        assertNotNull(orderEvent.getUpdatedAt());
    }

    @Test
    void processInventoryEvent_FailedEvent_ShouldUpdateOrderStatusAndProductAnalytics() {
        // Given
        inventoryEvent.setEventType("FAILED");
        inventoryEvent.setStatus("FAILED");
        inventoryEvent.setReason("Out of stock");
        
        when(inventoryEventRepository.save(any(InventoryEvent.class))).thenReturn(inventoryEvent);
        when(orderEventRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.of(orderEvent));
        when(orderEventRepository.save(any(OrderEvent.class))).thenReturn(orderEvent);
        when(productAnalyticsRepository.findById("PROD-001")).thenReturn(Optional.of(productAnalytics));
        when(productAnalyticsRepository.save(any(ProductAnalytics.class))).thenReturn(productAnalytics);

        // When
        analyticsService.processInventoryEvent(inventoryEvent);

        // Then
        verify(inventoryEventRepository, times(1)).save(inventoryEvent);
        verify(orderEventRepository, times(1)).findByOrderNumber("ORD-001");
        verify(orderEventRepository, times(1)).save(any(OrderEvent.class));
        verify(productAnalyticsRepository, times(1)).findById("PROD-001");
        verify(productAnalyticsRepository, times(1)).save(any(ProductAnalytics.class));
        
        assertEquals("FAILED", orderEvent.getStatus());
        assertNotNull(orderEvent.getUpdatedAt());
    }

    @Test
    void processInventoryEvent_OrderNotFound_ShouldLogWarning() {
        // Given
        when(inventoryEventRepository.save(any(InventoryEvent.class))).thenReturn(inventoryEvent);
        when(orderEventRepository.findByOrderNumber("ORD-001")).thenReturn(Optional.empty());

        // When
        analyticsService.processInventoryEvent(inventoryEvent);

        // Then
        verify(inventoryEventRepository, times(1)).save(inventoryEvent);
        verify(orderEventRepository, times(1)).findByOrderNumber("ORD-001");
        verify(orderEventRepository, never()).save(any(OrderEvent.class));
        verify(productAnalyticsRepository, never()).findById(anyString());
        verify(productAnalyticsRepository, never()).save(any(ProductAnalytics.class));
    }

    @Test
    void getDashboardData_ShouldReturnDashboardData() {
        // Given
        when(orderEventRepository.countTotalOrders()).thenReturn(100L);
        when(orderEventRepository.countCompletedOrders()).thenReturn(80L);
        when(orderEventRepository.countFailedOrders()).thenReturn(20L);
        when(productAnalyticsRepository.calculateTotalRevenue()).thenReturn(new BigDecimal("10000.00"));
        when(productAnalyticsRepository.findTopOrderedProducts()).thenReturn(Arrays.asList(productAnalytics));

        // When
        DashboardData dashboardData = analyticsService.getDashboardData();

        // Then
        assertNotNull(dashboardData);
        assertEquals(100L, dashboardData.getTotalOrders());
        assertEquals(80L, dashboardData.getCompletedOrders());
        assertEquals(20L, dashboardData.getFailedOrders());
        assertEquals(new BigDecimal("10000.00"), dashboardData.getTotalRevenue());
        assertEquals(1, dashboardData.getTopProducts().size());
        
        verify(orderEventRepository, times(1)).countTotalOrders();
        verify(orderEventRepository, times(1)).countCompletedOrders();
        verify(orderEventRepository, times(1)).countFailedOrders();
        verify(productAnalyticsRepository, times(1)).calculateTotalRevenue();
        verify(productAnalyticsRepository, times(1)).findTopOrderedProducts();
    }

    @Test
    void getOrderAnalytics_ShouldReturnOrderAnalytics() {
        // Given
        when(orderEventRepository.countTotalOrders()).thenReturn(100L);
        when(orderEventRepository.countCompletedOrders()).thenReturn(80L);
        when(orderEventRepository.countFailedOrders()).thenReturn(20L);
        when(inventoryEventRepository.countSuccessfulInventoryEvents()).thenReturn(80L);
        when(inventoryEventRepository.countFailedInventoryEvents()).thenReturn(20L);

        // When
        OrderAnalytics orderAnalytics = analyticsService.getOrderAnalytics();

        // Then
        assertNotNull(orderAnalytics);
        assertEquals(100L, orderAnalytics.getTotalOrders());
        assertEquals(80L, orderAnalytics.getCompletedOrders());
        assertEquals(20L, orderAnalytics.getFailedOrders());
        assertEquals(80.0, orderAnalytics.getSuccessRate());
        assertEquals(20.0, orderAnalytics.getFailureRate());
        assertEquals(80L, orderAnalytics.getSuccessfulInventoryEvents());
        assertEquals(20L, orderAnalytics.getFailedInventoryEvents());
        
        verify(orderEventRepository, times(1)).countTotalOrders();
        verify(orderEventRepository, times(1)).countCompletedOrders();
        verify(orderEventRepository, times(1)).countFailedOrders();
        verify(inventoryEventRepository, times(1)).countSuccessfulInventoryEvents();
        verify(inventoryEventRepository, times(1)).countFailedInventoryEvents();
    }

    @Test
    void getProductAnalytics_ShouldReturnProductAnalyticsList() {
        // Given
        List<ProductAnalytics> productAnalyticsList = Arrays.asList(productAnalytics);
        when(productAnalyticsRepository.findAll()).thenReturn(productAnalyticsList);

        // When
        List<ProductAnalyticsDTO> result = analyticsService.getProductAnalytics();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PROD-001", result.get(0).getProductId());
        assertEquals(10, result.get(0).getTotalOrdered());
        assertEquals(8, result.get(0).getTotalFulfilled());
        assertEquals(2, result.get(0).getTotalFailed());
        assertEquals(new BigDecimal("800.00"), result.get(0).getTotalRevenue());
        assertEquals(80.0, result.get(0).getSuccessRate());
        
        verify(productAnalyticsRepository, times(1)).findAll();
    }
}
