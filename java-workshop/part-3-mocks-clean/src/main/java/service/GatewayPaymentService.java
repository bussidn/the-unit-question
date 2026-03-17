package service;

import domain.RefundResult;
import gateway.PaymentGateway;

public class GatewayPaymentService implements PaymentService {
    private final PaymentGateway paymentGateway;

    public GatewayPaymentService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        RefundResult result = paymentGateway.refund(transactionId);
        return switch (result) {
            case REFUNDED -> true;
            case ALREADY_REFUNDED, NOTHING_TO_REFUND -> false;
            case FAILED -> throw new IllegalStateException("Refund failed");
        };
    }
}