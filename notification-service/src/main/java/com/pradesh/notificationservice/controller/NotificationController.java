package com.pradesh.notificationservice.controller;

import com.pradesh.notificationservice.model.NotificationRequest;
import com.pradesh.notificationservice.model.NotificationResponse;
import com.pradesh.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Controller", description = "API for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications", description = "Returns a list of all notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class)) })
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        log.info("Received request to get all notifications");
        // In a real application, this would retrieve notifications from a database
        return ResponseEntity.ok(List.of(
            new NotificationResponse("1", "Order Update", "Your order has been processed", "EMAIL", LocalDateTime.now()),
            new NotificationResponse("2", "Inventory Alert", "Low stock for product XYZ", "SMS", LocalDateTime.now().minusDays(1))
        ));
    }

    @Operation(summary = "Send a test notification", description = "Sends a test notification with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification sent successfully",
                content = { @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationResponse.class)) }),
        @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> sendTestNotification(
            @Parameter(description = "Notification request details", required = true)
            @RequestBody NotificationRequest request) {
        log.info("Received request to send test notification: {}", request);

        // Simulate sending a notification
        notificationService.sendInventoryUpdateNotification(
            request.getOrderNumber(),
            request.getProductId(),
            request.getQuantity(),
            "TEST"
        );

        NotificationResponse response = new NotificationResponse(
            "test-" + System.currentTimeMillis(),
            request.getSubject(),
            request.getMessage(),
            request.getChannel(),
            LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}
