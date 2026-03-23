package service;

import domain.PaymentRequest;
import domain.PaymentResult;
import domain.PaymentStatus;
import gateway.PaymentGateway;

public class GatewayPaymentService implements PaymentService {
    private final PaymentGateway paymentGateway;

    public GatewayPaymentService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public PaymentResult processPayment(String orderId, String customerId, double amount) {
        if (amount <= 0) {
            return new PaymentResult(orderId, PaymentStatus.FAILED, null);
        }

        PaymentRequest request = new PaymentRequest(orderId, customerId, amount);
        PaymentResult gatewayResult = paymentGateway.process(request);
        return new PaymentResult(orderId, gatewayResult.status(), gatewayResult.transactionId());
    }

    @Override
    public boolean refundPayment(String transactionId) {
        return paymentGateway.refund(transactionId);
    }
}