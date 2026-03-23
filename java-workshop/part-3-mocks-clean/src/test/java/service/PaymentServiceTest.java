package service;

import domain.RefundResult;
import gateway.PaymentGateway;
import org.junit.jupiter.api.BeforeEach;
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

    private String transactionId;

    @BeforeEach
    void setUp() {
        transactionId = "TXN-001";
    }

    @Test
    void refundPayment_returnsTrue_whenMoneyIsRefunded() {
        givenGatewayRefundReturns(RefundResult.REFUNDED);

        boolean result = paymentService.refundPayment(transactionId);

        assertTrue(result);
        verifyRefundWasRequested();
    }

    @Test
    void refundPayment_returnsFalse_whenAlreadyRefunded() {
        givenGatewayRefundReturns(RefundResult.ALREADY_REFUNDED);

        boolean result = paymentService.refundPayment(transactionId);

        assertFalse(result);
        verifyRefundWasRequested();
    }

    @Test
    void refundPayment_returnsFalse_whenNothingNeedsToBeRefunded() {
        givenGatewayRefundReturns(RefundResult.NOTHING_TO_REFUND);

        boolean result = paymentService.refundPayment(transactionId);

        assertFalse(result);
        verifyRefundWasRequested();
    }

    private void givenGatewayRefundReturns(RefundResult refundResult) {
        when(paymentGateway.refund(transactionId)).thenReturn(refundResult);
    }

    private void verifyRefundWasRequested() {
        verify(paymentGateway).refund(transactionId);
    }
}