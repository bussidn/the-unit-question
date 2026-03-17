package infrastructure;

import domain.PaymentResult;
import domain.PaymentStatus;
import java.util.UUID;

/**
 * Static methods - requires PowerMock to mock.
 */
public final class PaymentGateway {
    private PaymentGateway() {
    }

    public static PaymentResult processPayment(String customerId, double amount) {
        if (amount > 0) {
            return new PaymentResult("", PaymentStatus.SUCCESS, UUID.randomUUID().toString());
        }
        return new PaymentResult("", PaymentStatus.FAILED, null);
    }

    public static boolean refund(String transactionId) {
        return true;
    }
}