package service;

import domain.RefundResult;
import gateway.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private GatewayPaymentService paymentService;

    @Test
    void refundPayment_returnsTrue_whenMoneyIsRefunded() {
        when(paymentGateway.refund("TXN-001")).thenReturn(RefundResult.REFUNDED);

        boolean result = paymentService.refundPayment("TXN-001");

        assertTrue(result);
        verify(paymentGateway).refund("TXN-001");
    }

    @Test
    void refundPayment_returnsFalse_whenAlreadyRefunded() {
        when(paymentGateway.refund("TXN-001")).thenReturn(RefundResult.ALREADY_REFUNDED);

        boolean result = paymentService.refundPayment("TXN-001");

        assertFalse(result);
        verify(paymentGateway).refund("TXN-001");
    }
}