package com.pradesh.notificationservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object containing notification details")
public class NotificationResponse {
    
    @Schema(description = "Unique identifier of the notification", example = "NOTIF-12345")
    private String id;
    
    @Schema(description = "Subject of the notification", example = "Order Status Update")
    private String subject;
    
    @Schema(description = "Message content of the notification", example = "Your order has been processed successfully")
    private String message;
    
    @Schema(description = "Channel used to send the notification (EMAIL, SMS, PUSH)", example = "EMAIL")
    private String channel;
    
    @Schema(description = "Timestamp when the notification was sent", example = "2023-04-15T14:30:00")
    private LocalDateTime sentAt;
}
