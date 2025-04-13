package com.pradesh.inventoryservice.service;

import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    InventoryResponse getInventoryByProductId(String productId);
    List<InventoryResponse> getAllInventory();
    InventoryResponse createOrUpdateInventory(InventoryRequest inventoryRequest);
}
