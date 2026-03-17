package domain;

public record ShippingConfirmation(String orderId, String trackingNumber, String estimatedDelivery) {
}

