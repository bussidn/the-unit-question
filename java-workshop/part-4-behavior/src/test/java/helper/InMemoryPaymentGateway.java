package helper;

import domain.PaymentRequest;
import domain.PaymentResult;
import domain.PaymentStatus;
import gateway.PaymentGateway;

import java.util.ArrayList;
import java.util.List;

public class InMemoryPaymentGateway implements PaymentGateway {

    private boolean willDecline = false;
    private PaymentResult lastResult;
    private final List<String> refunds = new ArrayList<>();

    public void willDecline() {
        this.willDecline = true;
    }

    @Override
    public PaymentResult process(PaymentRequest request) {
        if (willDecline) {
            lastResult = new PaymentResult(request.orderId(), PaymentStatus.FAILED, null);
        } else {
            lastResult = new PaymentResult(request.orderId(), PaymentStatus.SUCCESS, "TXN-" + request.orderId());
        }
        return lastResult;
    }

    @Override
    public boolean refund(String transactionId) {
        refunds.add(transactionId);
        return true;
    }

    public boolean wasCharged() {
        return lastResult != null && lastResult.status() == PaymentStatus.SUCCESS;
    }

    public String lastTransactionId() {
        return lastResult != null ? lastResult.transactionId() : null;
    }

    public boolean wasRefunded(String transactionId) {
        return refunds.contains(transactionId);
    }
}

