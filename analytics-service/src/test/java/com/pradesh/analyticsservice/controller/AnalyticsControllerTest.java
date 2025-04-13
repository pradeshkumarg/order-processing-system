package com.pradesh.analyticsservice.controller;

import com.pradesh.analyticsservice.dto.DashboardData;
import com.pradesh.analyticsservice.dto.OrderAnalytics;
import com.pradesh.analyticsservice.dto.ProductAnalyticsDTO;
import com.pradesh.analyticsservice.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    private MockMvc mockMvc;
    private DashboardData dashboardData;
    private OrderAnalytics orderAnalytics;
    private List<ProductAnalyticsDTO> productAnalyticsList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController).build();

        ProductAnalyticsDTO productAnalyticsDTO = ProductAnalyticsDTO.builder()
                .productId("PROD-001")
                .totalOrdered(10)
                .totalFulfilled(8)
                .totalFailed(2)
                .totalRevenue(new BigDecimal("800.00"))
                .successRate(80.0)
                .build();

        dashboardData = DashboardData.builder()
                .totalOrders(100L)
                .completedOrders(80L)
                .failedOrders(20L)
                .totalRevenue(new BigDecimal("10000.00"))
                .topProducts(Arrays.asList(productAnalyticsDTO))
                .build();

        orderAnalytics = OrderAnalytics.builder()
                .totalOrders(100L)
                .completedOrders(80L)
                .failedOrders(20L)
                .successRate(80.0)
                .failureRate(20.0)
                .successfulInventoryEvents(80L)
                .failedInventoryEvents(20L)
                .build();

        productAnalyticsList = Arrays.asList(productAnalyticsDTO);
    }

    @Test
    void getDashboardData_ShouldReturnDashboardData() throws Exception {
        // Given
        when(analyticsService.getDashboardData()).thenReturn(dashboardData);

        // When & Then
        mockMvc.perform(get("/api/analytics/dashboard")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalOrders").value(100))
                .andExpect(jsonPath("$.completedOrders").value(80))
                .andExpect(jsonPath("$.failedOrders").value(20))
                .andExpect(jsonPath("$.totalRevenue").value(10000.00))
                .andExpect(jsonPath("$.topProducts[0].productId").value("PROD-001"));
    }

    @Test
    void getOrderAnalytics_ShouldReturnOrderAnalytics() throws Exception {
        // Given
        when(analyticsService.getOrderAnalytics()).thenReturn(orderAnalytics);

        // When & Then
        mockMvc.perform(get("/api/analytics/orders")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalOrders").value(100))
                .andExpect(jsonPath("$.completedOrders").value(80))
                .andExpect(jsonPath("$.failedOrders").value(20))
                .andExpect(jsonPath("$.successRate").value(80.0))
                .andExpect(jsonPath("$.failureRate").value(20.0))
                .andExpect(jsonPath("$.successfulInventoryEvents").value(80))
                .andExpect(jsonPath("$.failedInventoryEvents").value(20));
    }

    @Test
    void getProductAnalytics_ShouldReturnProductAnalyticsList() throws Exception {
        // Given
        when(analyticsService.getProductAnalytics()).thenReturn(productAnalyticsList);

        // When & Then
        mockMvc.perform(get("/api/analytics/products")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value("PROD-001"))
                .andExpect(jsonPath("$[0].totalOrdered").value(10))
                .andExpect(jsonPath("$[0].totalFulfilled").value(8))
                .andExpect(jsonPath("$[0].totalFailed").value(2))
                .andExpect(jsonPath("$[0].totalRevenue").value(800.00))
                .andExpect(jsonPath("$[0].successRate").value(80.0));
    }
}
