package com.pradesh.notificationservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending a notification")
public class NotificationRequest {
    
    @Schema(description = "Order number related to the notification", example = "ORD-12345")
    private String orderNumber;
    
    @Schema(description = "Product ID related to the notification", example = "PROD-001")
    private String productId;
    
    @Schema(description = "Quantity of the product", example = "5")
    private Integer quantity;
    
    @Schema(description = "Subject of the notification", example = "Order Status Update")
    private String subject;
    
    @Schema(description = "Message content of the notification", example = "Your order has been processed successfully")
    private String message;
    
    @Schema(description = "Channel to send the notification (EMAIL, SMS, PUSH)", example = "EMAIL")
    private String channel;
}
