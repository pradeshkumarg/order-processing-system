package com.pradesh.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing inventory information")
public class InventoryResponse {
    @Schema(description = "Unique identifier for the product", example = "PROD-001")
    private String productId;

    @Schema(description = "Quantity of the product in inventory", example = "10")
    private Integer quantity;

    @Schema(description = "Flag indicating if the product is in stock", example = "true")
    private boolean inStock;

    @Schema(description = "Timestamp of the last inventory update", example = "2023-04-13T10:15:30")
    private LocalDateTime lastUpdated;
}
