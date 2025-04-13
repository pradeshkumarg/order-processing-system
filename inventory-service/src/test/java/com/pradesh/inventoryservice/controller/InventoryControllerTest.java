package com.pradesh.inventoryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pradesh.inventoryservice.dto.InventoryRequest;
import com.pradesh.inventoryservice.dto.InventoryResponse;
import com.pradesh.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryRequest inventoryRequest;
    private InventoryResponse inventoryResponse;
    private List<InventoryResponse> inventoryResponses;

    @BeforeEach
    void setUp() {
        inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId("PROD-001");
        inventoryRequest.setQuantity(10);

        inventoryResponse = InventoryResponse.builder()
                .productId("PROD-001")
                .quantity(10)
                .inStock(true)
                .lastUpdated(LocalDateTime.now())
                .build();

        InventoryResponse inventoryResponse2 = InventoryResponse.builder()
                .productId("PROD-002")
                .quantity(5)
                .inStock(true)
                .lastUpdated(LocalDateTime.now())
                .build();

        inventoryResponses = Arrays.asList(inventoryResponse, inventoryResponse2);
    }

    @Test
    void shouldGetInventoryByProductId() throws Exception {
        when(inventoryService.getInventoryByProductId("PROD-001")).thenReturn(inventoryResponse);

        mockMvc.perform(get("/api/inventory/PROD-001")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value("PROD-001"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.inStock").value(true));
    }

    @Test
    void shouldHandleExceptionWhenProductNotFound() throws Exception {
        when(inventoryService.getInventoryByProductId("NON-EXISTENT"))
                .thenThrow(new RuntimeException("Inventory not found for product: NON-EXISTENT"));

        mockMvc.perform(get("/api/inventory/NON-EXISTENT")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllInventory() throws Exception {
        // Clear any previous mocks to avoid conflicts
        org.mockito.Mockito.reset(inventoryService);
        // Set up the mock for getAllInventory
        when(inventoryService.getAllInventory()).thenReturn(inventoryResponses);

        mockMvc.perform(get("/api/inventory")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].productId").value("PROD-001"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[1].productId").value("PROD-002"))
                .andExpect(jsonPath("$[1].quantity").value(5));
    }

    @Test
    void shouldReturnEmptyListWhenNoInventory() throws Exception {
        // Clear any previous mocks to avoid conflicts
        org.mockito.Mockito.reset(inventoryService);
        // Set up the mock for getAllInventory to return empty list
        when(inventoryService.getAllInventory()).thenReturn(List.of());

        mockMvc.perform(get("/api/inventory")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldCreateOrUpdateInventory() throws Exception {
        when(inventoryService.createOrUpdateInventory(any(InventoryRequest.class))).thenReturn(inventoryResponse);

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventoryRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.productId").value("PROD-001"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.inStock").value(true));
    }

    @Test
    void shouldHandleInvalidRequestBody() throws Exception {
        // Create an invalid request with null productId
        InventoryRequest invalidRequest = new InventoryRequest();
        invalidRequest.setProductId(null); // Invalid: productId is required
        invalidRequest.setQuantity(10);

        mockMvc.perform(post("/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
