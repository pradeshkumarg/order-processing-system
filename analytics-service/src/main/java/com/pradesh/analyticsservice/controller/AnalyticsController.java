package com.pradesh.analyticsservice.controller;

import com.pradesh.analyticsservice.dto.DashboardData;
import com.pradesh.analyticsservice.dto.OrderAnalytics;
import com.pradesh.analyticsservice.dto.ProductAnalyticsDTO;
import com.pradesh.analyticsservice.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics Controller", description = "API for retrieving analytics data")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Operation(summary = "Get dashboard data", description = "Returns key metrics for the dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardData.class)) })
    })
    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardData> getDashboardData() {
        log.info("Received request to get dashboard data");
        DashboardData dashboardData = analyticsService.getDashboardData();
        return ResponseEntity.ok(dashboardData);
    }

    @Operation(summary = "Get order analytics", description = "Returns analytics data for orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order analytics retrieved successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = OrderAnalytics.class)) })
    })
    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderAnalytics> getOrderAnalytics() {
        log.info("Received request to get order analytics");
        OrderAnalytics orderAnalytics = analyticsService.getOrderAnalytics();
        return ResponseEntity.ok(orderAnalytics);
    }

    @Operation(summary = "Get product analytics", description = "Returns analytics data for products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product analytics retrieved successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ProductAnalyticsDTO.class)) })
    })
    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductAnalyticsDTO>> getProductAnalytics() {
        log.info("Received request to get product analytics");
        List<ProductAnalyticsDTO> productAnalytics = analyticsService.getProductAnalytics();
        return ResponseEntity.ok(productAnalytics);
    }
}
