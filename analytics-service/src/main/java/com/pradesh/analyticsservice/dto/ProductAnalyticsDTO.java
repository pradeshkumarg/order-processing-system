package com.pradesh.analyticsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Analytics data for a product")
public class ProductAnalyticsDTO {
    
    @Schema(description = "Product ID", example = "PROD-001")
    private String productId;
    
    @Schema(description = "Total quantity ordered", example = "50")
    private Integer totalOrdered;
    
    @Schema(description = "Total quantity fulfilled", example = "45")
    private Integer totalFulfilled;
    
    @Schema(description = "Total quantity failed", example = "5")
    private Integer totalFailed;
    
    @Schema(description = "Total revenue from this product", example = "4500.00")
    private BigDecimal totalRevenue;
    
    @Schema(description = "Success rate as a percentage", example = "90.0")
    private double successRate;
}
