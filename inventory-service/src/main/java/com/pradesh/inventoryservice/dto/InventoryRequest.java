package com.pradesh.inventoryservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating or updating inventory")
public class InventoryRequest {
    @NotBlank(message = "Product ID is required")
    @Schema(description = "Unique identifier for the product", example = "PROD-001", required = true)
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    @Schema(description = "Quantity of the product in inventory", example = "10", required = true, minimum = "0")
    private Integer quantity;
}
