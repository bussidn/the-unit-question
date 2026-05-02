package service;

import domain.PaymentResult;

public interface PaymentService {
    PaymentResult processPayment(String orderId, String customerId, double amount);

    boolean refundPayment(String transactionId);
}