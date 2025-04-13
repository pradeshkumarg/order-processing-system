package com.pradesh.inventoryservice.repository;

import com.pradesh.inventoryservice.model.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void shouldSaveInventory() {
        // Given
        Inventory inventory = Inventory.builder()
                .productId("PROD-001")
                .quantity(10)
                .lastUpdated(LocalDateTime.now())
                .build();

        // When
        Inventory savedInventory = inventoryRepository.save(inventory);

        // Then
        assertThat(savedInventory).isNotNull();
        assertThat(savedInventory.getId()).isNotNull();
        assertThat(savedInventory.getProductId()).isEqualTo("PROD-001");
        assertThat(savedInventory.getQuantity()).isEqualTo(10);
    }

    @Test
    public void shouldFindByProductId() {
        // Given
        Inventory inventory = Inventory.builder()
                .productId("PROD-002")
                .quantity(5)
                .lastUpdated(LocalDateTime.now())
                .build();
        inventoryRepository.save(inventory);

        // When
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId("PROD-002");

        // Then
        assertThat(foundInventory).isPresent();
        assertThat(foundInventory.get().getProductId()).isEqualTo("PROD-002");
        assertThat(foundInventory.get().getQuantity()).isEqualTo(5);
    }

    @Test
    public void shouldFindAllInventory() {
        // Given
        Inventory inventory1 = Inventory.builder()
                .productId("PROD-003")
                .quantity(15)
                .lastUpdated(LocalDateTime.now())
                .build();
        
        Inventory inventory2 = Inventory.builder()
                .productId("PROD-004")
                .quantity(20)
                .lastUpdated(LocalDateTime.now())
                .build();
        
        inventoryRepository.save(inventory1);
        inventoryRepository.save(inventory2);

        // When
        List<Inventory> inventoryList = inventoryRepository.findAll();

        // Then
        assertThat(inventoryList).isNotEmpty();
        assertThat(inventoryList.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void shouldNotFindByNonExistentProductId() {
        // When
        Optional<Inventory> foundInventory = inventoryRepository.findByProductId("NON-EXISTENT");

        // Then
        assertThat(foundInventory).isEmpty();
    }
}
