package com.pradesh.inventoryservice.controller;

import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;
import com.pradesh.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Inventory API", description = "API endpoints for inventory management")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get inventory by product ID", description = "Returns inventory information for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory found",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class)) }),
        @ApiResponse(responseCode = "404", description = "Inventory not found", content = @Content)
    })
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponse> getInventory(
            @Parameter(description = "Product ID to retrieve inventory for", required = true)
            @PathVariable String productId) {
        log.info("Received request to get inventory for product: {}", productId);
        try {
            InventoryResponse inventoryResponse = inventoryService.getInventoryByProductId(productId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(inventoryResponse);
        } catch (RuntimeException ex) {
            log.error("Error getting inventory for product {}: {}", productId, ex.getMessage());
            throw ex; // Re-throw to be handled by GlobalExceptionHandler
        }
    }

    @Operation(summary = "Get all inventory", description = "Returns a list of all inventory items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory list retrieved successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class)) })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        log.info("Received request to get all inventory");
        List<InventoryResponse> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(inventoryList);
    }

    @Operation(summary = "Create or update inventory", description = "Creates a new inventory item or updates an existing one")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inventory created or updated successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InventoryResponse> createOrUpdateInventory(
            @Parameter(description = "Inventory request object", required = true)
            @Valid @RequestBody InventoryRequest inventoryRequest) {
        log.info("Received request to create/update inventory for product: {}", inventoryRequest.getProductId());
        InventoryResponse inventoryResponse = inventoryService.createOrUpdateInventory(inventoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(inventoryResponse);
    }
}
