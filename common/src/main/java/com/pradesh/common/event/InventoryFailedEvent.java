package com.pradesh.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryFailedEvent {
    private String orderNumber;
    private String productId;
    private Integer quantity;
    private String reason;
    private LocalDateTime failedAt;
}
