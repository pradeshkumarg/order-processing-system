package com.pradesh.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_analytics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAnalytics {
    
    @Id
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private Integer totalOrdered;
    
    @Column(nullable = false)
    private Integer totalFulfilled;
    
    @Column(nullable = false)
    private Integer totalFailed;
    
    @Column(nullable = false)
    private BigDecimal totalRevenue;
    
    // Method to update analytics when a new order is created
    public void updateWithNewOrder(Integer quantity, BigDecimal price) {
        this.totalOrdered += quantity;
        // Revenue is only added when order is fulfilled
    }
    
    // Method to update analytics when inventory is updated (order fulfilled)
    public void updateWithFulfilledOrder(Integer quantity, BigDecimal price) {
        this.totalFulfilled += quantity;
        this.totalRevenue = this.totalRevenue.add(price.multiply(new BigDecimal(quantity)));
    }
    
    // Method to update analytics when inventory fails
    public void updateWithFailedOrder(Integer quantity) {
        this.totalFailed += quantity;
    }
}
