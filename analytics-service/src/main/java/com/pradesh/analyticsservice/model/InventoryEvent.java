package com.pradesh.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String orderNumber;
    
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private String eventType; // UPDATED or FAILED
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private String reason; // For failed events
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
}
