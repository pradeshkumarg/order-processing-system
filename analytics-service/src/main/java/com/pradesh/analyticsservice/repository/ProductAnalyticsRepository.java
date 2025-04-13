package com.pradesh.analyticsservice.repository;

import com.pradesh.analyticsservice.model.ProductAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductAnalyticsRepository extends JpaRepository<ProductAnalytics, String> {
    
    @Query("SELECT SUM(p.totalRevenue) FROM ProductAnalytics p")
    BigDecimal calculateTotalRevenue();
    
    @Query("SELECT p FROM ProductAnalytics p ORDER BY p.totalOrdered DESC")
    List<ProductAnalytics> findTopOrderedProducts();
    
    @Query("SELECT p FROM ProductAnalytics p ORDER BY p.totalRevenue DESC")
    List<ProductAnalytics> findTopRevenueProducts();
    
    @Query("SELECT p FROM ProductAnalytics p ORDER BY p.totalFailed DESC")
    List<ProductAnalytics> findMostFailedProducts();
}
