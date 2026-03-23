package service;

import infrastructure.EmailGateway;

public class NotificationService {
    
    public void sendOrderConfirmation(String email, String orderId, double totalPrice) {
        String subject = "Order Confirmation";
        String body = "Your order " + orderId + " has been confirmed. Total: " + totalPrice + "€";
        EmailGateway.send(email, subject, body);
    }
}

