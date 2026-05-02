package gateway;

import domain.PaymentRequest;
import domain.PaymentResult;

public interface PaymentGateway {
    PaymentResult process(PaymentRequest request);

    boolean refund(String transactionId);
}