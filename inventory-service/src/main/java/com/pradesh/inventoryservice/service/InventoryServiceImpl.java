package com.pradesh.inventoryservice.service;

import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public InventoryResponse getInventoryByProductId(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        
        return mapToInventoryResponse(inventory);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        
        return inventoryList.stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse createOrUpdateInventory(InventoryRequest inventoryRequest) {
        // Check if inventory exists
        Inventory inventory = inventoryRepository.findByProductId(inventoryRequest.getProductId())
                .orElse(Inventory.builder()
                        .productId(inventoryRequest.getProductId())
                        .quantity(0)
                        .build());
        
        // Update inventory
        inventory.setQuantity(inventoryRequest.getQuantity());
        inventory.setLastUpdated(LocalDateTime.now());
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Inventory created/updated for product: {}, quantity: {}", 
                savedInventory.getProductId(), savedInventory.getQuantity());
        
        return mapToInventoryResponse(savedInventory);
    }
    
    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .inStock(inventory.getQuantity() > 0)
                .lastUpdated(inventory.getLastUpdated())
                .build();
    }
}
