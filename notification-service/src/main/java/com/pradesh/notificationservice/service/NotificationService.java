package com.pradesh.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendInventoryUpdateNotification(String orderNumber, String productId, Integer quantity, String status) {
        // In a real application, this would send an actual email or SMS
        log.info("Sending inventory update notification for order {}: Product - {}, Quantity - {}, Status - {}",
                orderNumber, productId, quantity, status);
        
        // Simulate notification delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Successfully sent inventory update notification for order {}", orderNumber);
    }

    public void sendInventoryFailureNotification(String orderNumber, String productId, Integer quantity, String reason) {
        // In a real application, this would send an actual email or SMS for failure
        log.error("Sending inventory failure notification for order {}: Product - {}, Quantity - {}, Reason - {}",
                orderNumber, productId, quantity, reason);
        
        // Simulate notification delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Successfully sent inventory failure notification for order {}", orderNumber);
    }
} 