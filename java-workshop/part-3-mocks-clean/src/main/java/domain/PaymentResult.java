package domain;

public record PaymentResult(String orderId, PaymentStatus status, String transactionId) {
}