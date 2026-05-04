package domain;

public record PaymentRequest(String orderId, String customerId, double amount) {
}