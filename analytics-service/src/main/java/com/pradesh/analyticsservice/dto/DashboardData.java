package com.pradesh.analyticsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard data containing key metrics")
public class DashboardData {
    
    @Schema(description = "Total number of orders", example = "150")
    private Long totalOrders;
    
    @Schema(description = "Number of completed orders", example = "120")
    private Long completedOrders;
    
    @Schema(description = "Number of failed orders", example = "30")
    private Long failedOrders;
    
    @Schema(description = "Total revenue from all orders", example = "15000.50")
    private BigDecimal totalRevenue;
    
    @Schema(description = "List of top products by order volume")
    private List<ProductAnalyticsDTO> topProducts;
}
