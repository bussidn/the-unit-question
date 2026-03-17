package service;

public interface PaymentService {
    boolean refundPayment(String transactionId);
}