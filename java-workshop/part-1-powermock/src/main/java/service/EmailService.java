package service;

import infrastructure.SmtpConnection;

/**
 * Service for sending emails.
 * 
 * NOTE: This service requires a real SMTP connection at construction time.
 * It cannot be instantiated without proper infrastructure.
 */
public class EmailService {
    private final SmtpConnection smtpConnection;

    public EmailService() {
        // Requires real SMTP infrastructure - will fail in tests without mocking
        this.smtpConnection = SmtpConnection.connect();
    }

    public void sendOrderConfirmation(String email, String orderId, double totalAmount) {
        String subject = "Order " + orderId + " confirmed";
        String body = "Your order " + orderId + " for " + totalAmount + "€ has been confirmed.";
        smtpConnection.send(email, subject, body);
    }
}

