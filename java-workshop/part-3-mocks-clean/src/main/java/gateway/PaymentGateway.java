package gateway;

import domain.RefundResult;

public interface PaymentGateway {
    RefundResult refund(String transactionId);
}