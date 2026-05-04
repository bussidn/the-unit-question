package service;

import domain.PaymentRequest;
import domain.PaymentResult;
import domain.PaymentStatus;
import gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    private GatewayPaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new GatewayPaymentService(paymentGateway);
    }

    @Test
    void processPayment_returnsSuccess_whenGatewayAcceptsPayment() {
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.SUCCESS, "TXN-123"));

        var result = paymentService.processPayment("ORDER-001", "CUST-001", 29.0);

        assertEquals(PaymentStatus.SUCCESS, result.status());
        assertEquals("TXN-123", result.transactionId());
    }

    @Test
    void processPayment_returnsFailed_whenAmountIsZero() {
        var result = paymentService.processPayment("ORDER-001", "CUST-001", 0.0);

        assertEquals(PaymentStatus.FAILED, result.status());
        verifyNoInteractions(paymentGateway);
    }

    @Test
    void refundPayment_returnsTrue_whenGatewayConfirmsRefund() {
        when(paymentGateway.refund("TXN-123")).thenReturn(true);

        assertTrue(paymentService.refundPayment("TXN-123"));
    }

    @Test
    void refundPayment_returnsFalse_whenGatewayDeclines() {
        when(paymentGateway.refund("TXN-123")).thenReturn(false);

        assertFalse(paymentService.refundPayment("TXN-123"));
    }
}

