package service;

import domain.PaymentResult;
import domain.PaymentStatus;
import infrastructure.PaymentGateway;

/**
 * Payment service with hardcoded PaymentGateway dependency (static calls).
 */
public class PaymentService {

    public PaymentResult processPayment(String orderId, String customerId, double amount) {
        if (amount <= 0) {
            return new PaymentResult(orderId, PaymentStatus.FAILED, null);
        }

        PaymentResult gatewayResult = PaymentGateway.processPayment(customerId, amount);
        return new PaymentResult(orderId, gatewayResult.status(), gatewayResult.transactionId());
    }

    public boolean refundPayment(String transactionId) {
        return PaymentGateway.refund(transactionId);
    }
}