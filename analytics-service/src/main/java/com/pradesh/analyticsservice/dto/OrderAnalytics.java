package com.pradesh.analyticsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Analytics data for orders")
public class OrderAnalytics {
    
    @Schema(description = "Total number of orders", example = "150")
    private Long totalOrders;
    
    @Schema(description = "Number of completed orders", example = "120")
    private Long completedOrders;
    
    @Schema(description = "Number of failed orders", example = "30")
    private Long failedOrders;
    
    @Schema(description = "Success rate as a percentage", example = "80.0")
    private double successRate;
    
    @Schema(description = "Failure rate as a percentage", example = "20.0")
    private double failureRate;
    
    @Schema(description = "Number of successful inventory events", example = "120")
    private Long successfulInventoryEvents;
    
    @Schema(description = "Number of failed inventory events", example = "30")
    private Long failedInventoryEvents;
}
