package service;

import domain.PaymentRequest;
import domain.PaymentResult;
import domain.PaymentStatus;
import gateway.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_returnsSuccess_whenGatewaySucceeds() {
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResult("", PaymentStatus.SUCCESS, "TXN-789"));

        PaymentResult result = paymentService.processPayment("ORDER-123", "CUST-456", 100.0);

        assertEquals(PaymentStatus.SUCCESS, result.status());
        assertEquals("ORDER-123", result.orderId());
        assertEquals("TXN-789", result.transactionId());
        verify(paymentGateway).process(argThat(request ->
            request.orderId().equals("ORDER-123")
                && request.customerId().equals("CUST-456")
                && request.amount() == 100.0
        ));
    }

    @Test
    void processPayment_returnsFailure_forZeroAmount_withoutCallingGateway() {
        PaymentResult result = paymentService.processPayment("ORDER-123", "CUST-456", 0.0);

        assertEquals(PaymentStatus.FAILED, result.status());
        assertEquals("ORDER-123", result.orderId());
        verifyNoInteractions(paymentGateway);
    }

    @Test
    void processPayment_returnsFailure_whenGatewayFails() {
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResult("", PaymentStatus.FAILED, null));

        PaymentResult result = paymentService.processPayment("ORDER-123", "CUST-456", 100.0);

        assertEquals(PaymentStatus.FAILED, result.status());
        assertNull(result.transactionId());
    }

    @Test
    void refundPayment_delegatesToGateway() {
        when(paymentGateway.refund("TXN-123")).thenReturn(true);

        assertTrue(paymentService.refundPayment("TXN-123"));
        verify(paymentGateway).refund("TXN-123");
    }
}