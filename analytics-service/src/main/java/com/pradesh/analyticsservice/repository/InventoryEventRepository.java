package com.pradesh.analyticsservice.repository;

import com.pradesh.analyticsservice.model.InventoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryEventRepository extends JpaRepository<InventoryEvent, Long> {
    
    List<InventoryEvent> findByOrderNumber(String orderNumber);
    
    List<InventoryEvent> findByProductId(String productId);
    
    List<InventoryEvent> findByEventType(String eventType);
    
    List<InventoryEvent> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT COUNT(i) FROM InventoryEvent i WHERE i.eventType = 'UPDATED'")
    Long countSuccessfulInventoryEvents();
    
    @Query("SELECT COUNT(i) FROM InventoryEvent i WHERE i.eventType = 'FAILED'")
    Long countFailedInventoryEvents();
}
