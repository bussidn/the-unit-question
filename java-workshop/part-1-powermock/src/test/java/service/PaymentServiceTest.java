package service;

import domain.PaymentResult;
import domain.PaymentStatus;
import infrastructure.PaymentGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PowerMock style: We need to mock the PaymentGateway static methods
 */
class PaymentServiceTest {

    private PaymentService paymentService;
    private MockedStatic<PaymentGateway> mockedGateway;

    @BeforeEach
    void setUp() {
        mockedGateway = mockStatic(PaymentGateway.class);
        paymentService = new PaymentService();
    }

    @AfterEach
    void tearDown() {
        mockedGateway.close();
    }

    @Test
    void processPayment_returnsSuccess_whenGatewaySucceeds() {
        // Given
        String orderId = "ORDER-123";
        String customerId = "CUST-456";
        double amount = 100.0;

        mockedGateway.when(() -> PaymentGateway.processPayment(customerId, amount))
            .thenReturn(new PaymentResult("", PaymentStatus.SUCCESS, "TXN-789"));

        // When
        PaymentResult result = paymentService.processPayment(orderId, customerId, amount);

        // Then
        assertEquals(PaymentStatus.SUCCESS, result.status());
        assertEquals(orderId, result.orderId());
        assertEquals("TXN-789", result.transactionId());

        mockedGateway.verify(() -> PaymentGateway.processPayment(customerId, amount), times(1));
    }

    @Test
    void processPayment_returnsFailure_forZeroAmount_withoutCallingGateway() {
        // Given
        String orderId = "ORDER-123";
        String customerId = "CUST-456";
        double amount = 0.0;

        // When
        PaymentResult result = paymentService.processPayment(orderId, customerId, amount);

        // Then
        assertEquals(PaymentStatus.FAILED, result.status());
        mockedGateway.verifyNoInteractions();
    }

    @Test
    void processPayment_returnsFailure_whenGatewayFails() {
        // Given
        String orderId = "ORDER-123";
        String customerId = "CUST-456";
        double amount = 100.0;

        mockedGateway.when(() -> PaymentGateway.processPayment(customerId, amount))
            .thenReturn(new PaymentResult("", PaymentStatus.FAILED, null));

        // When
        PaymentResult result = paymentService.processPayment(orderId, customerId, amount);

        // Then
        assertEquals(PaymentStatus.FAILED, result.status());
    }

    @Test
    void refundPayment_returnsTrue_whenGatewayRefundSucceeds() {
        String transactionId = "TXN-789";

        mockedGateway.when(() -> PaymentGateway.refund(transactionId)).thenReturn(true);

        boolean result = paymentService.refundPayment(transactionId);

        assertTrue(result);
        mockedGateway.verify(() -> PaymentGateway.refund(transactionId), times(1));
    }

    @Test
    void refundPayment_returnsFalse_whenPaymentWasAlreadyRefunded() {
        String transactionId = "TXN-789";

        mockedGateway.when(() -> PaymentGateway.refund(transactionId)).thenReturn(false);

        boolean result = paymentService.refundPayment(transactionId);

        assertFalse(result);
        mockedGateway.verify(() -> PaymentGateway.refund(transactionId), times(1));
    }
}