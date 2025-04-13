package com.pradesh.analyticsservice.repository;

import com.pradesh.analyticsservice.model.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {
    
    Optional<OrderEvent> findByOrderNumber(String orderNumber);
    
    List<OrderEvent> findByProductId(String productId);
    
    List<OrderEvent> findByStatus(String status);
    
    List<OrderEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(o) FROM OrderEvent o")
    Long countTotalOrders();
    
    @Query("SELECT COUNT(o) FROM OrderEvent o WHERE o.status = 'COMPLETED'")
    Long countCompletedOrders();
    
    @Query("SELECT COUNT(o) FROM OrderEvent o WHERE o.status = 'FAILED'")
    Long countFailedOrders();
}
