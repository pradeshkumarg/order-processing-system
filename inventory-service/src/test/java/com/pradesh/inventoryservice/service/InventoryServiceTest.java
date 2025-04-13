package com.pradesh.inventoryservice.service;

import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;
import com.pradesh.inventoryservice.model.Inventory;
import com.pradesh.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory inventory;
    private InventoryRequest inventoryRequest;

    @BeforeEach
    void setUp() {
        inventory = Inventory.builder()
                .id(1L)
                .productId("PROD-001")
                .quantity(10)
                .lastUpdated(LocalDateTime.now())
                .build();

        inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId("PROD-001");
        inventoryRequest.setQuantity(10);
    }

    @Test
    void shouldGetInventoryByProductId() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.of(inventory));

        // When
        InventoryResponse response = inventoryService.getInventoryByProductId("PROD-001");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo("PROD-001");
        assertThat(response.getQuantity()).isEqualTo(10);
        assertThat(response.isInStock()).isTrue();
        assertThat(response.getLastUpdated()).isNotNull();

        verify(inventoryRepository, times(1)).findByProductId("PROD-001");
    }

    @Test
    void shouldReturnInventoryWithZeroQuantityAsNotInStock() {
        // Given
        Inventory zeroInventory = Inventory.builder()
                .id(3L)
                .productId("PROD-003")
                .quantity(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(inventoryRepository.findByProductId("PROD-003")).thenReturn(Optional.of(zeroInventory));

        // When
        InventoryResponse response = inventoryService.getInventoryByProductId("PROD-003");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo("PROD-003");
        assertThat(response.getQuantity()).isEqualTo(0);
        assertThat(response.isInStock()).isFalse(); // Should be false when quantity is 0

        verify(inventoryRepository, times(1)).findByProductId("PROD-003");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(inventoryRepository.findByProductId("NON-EXISTENT")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> inventoryService.getInventoryByProductId("NON-EXISTENT"));

        verify(inventoryRepository, times(1)).findByProductId("NON-EXISTENT");
    }

    @Test
    void shouldGetAllInventory() {
        // Given
        Inventory inventory2 = Inventory.builder()
                .id(2L)
                .productId("PROD-002")
                .quantity(5)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory, inventory2));

        // When
        List<InventoryResponse> responses = inventoryService.getAllInventory();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getProductId()).isEqualTo("PROD-001");
        assertThat(responses.get(1).getProductId()).isEqualTo("PROD-002");

        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateNewInventory() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // When
        InventoryResponse response = inventoryService.createOrUpdateInventory(inventoryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo("PROD-001");
        assertThat(response.getQuantity()).isEqualTo(10);
        assertThat(response.isInStock()).isTrue();

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(1)).save(inventoryCaptor.capture());

        Inventory capturedInventory = inventoryCaptor.getValue();
        assertThat(capturedInventory.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedInventory.getQuantity()).isEqualTo(10);
        assertThat(capturedInventory.getLastUpdated()).isNotNull();
    }

    @Test
    void shouldCreateNewInventoryWithZeroQuantity() {
        // Given
        inventoryRequest.setQuantity(0);

        Inventory zeroInventory = Inventory.builder()
                .id(1L)
                .productId("PROD-001")
                .quantity(0)
                .lastUpdated(LocalDateTime.now())
                .build();

        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(zeroInventory);

        // When
        InventoryResponse response = inventoryService.createOrUpdateInventory(inventoryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo("PROD-001");
        assertThat(response.getQuantity()).isEqualTo(0);
        assertThat(response.isInStock()).isFalse(); // Should be false when quantity is 0

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(1)).save(inventoryCaptor.capture());

        Inventory capturedInventory = inventoryCaptor.getValue();
        assertThat(capturedInventory.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedInventory.getQuantity()).isEqualTo(0);
    }

    @Test
    void shouldUpdateExistingInventory() {
        // Given
        when(inventoryRepository.findByProductId("PROD-001")).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        // Update quantity
        inventoryRequest.setQuantity(15);

        // When
        InventoryResponse response = inventoryService.createOrUpdateInventory(inventoryRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo("PROD-001");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(inventoryRepository, times(1)).save(inventoryCaptor.capture());

        Inventory capturedInventory = inventoryCaptor.getValue();
        assertThat(capturedInventory.getProductId()).isEqualTo("PROD-001");
        assertThat(capturedInventory.getQuantity()).isEqualTo(15); // Updated quantity
    }
}
