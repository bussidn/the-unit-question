package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderStatus;
import domain.StockReservation;
import helper.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCancellationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private StockService stockService;

    @Mock
    private ShippingService shippingService;

    @InjectMocks
    private OrderCancellationService service;

    private Order confirmedOrder;

    @BeforeEach
    void setUp() {
        confirmedOrder = OrderBuilder.confirmed()
            .withId("ORDER-001")
            .withTransaction("TXN-123")
            .withTracking("TRACK-456")
            .build();
    }

    @Test
    void cancelOrder_succeeds_whenAllStepsPass() {
        givenOrderExists(confirmedOrder);
        givenRefundSucceeds();
        givenShipmentCancelled();

        CancellationResult result = service.cancelOrder(confirmedOrder.id());

        assertSuccess(result);
        verify(paymentService).refundPayment("TXN-123");
        verify(shippingService).cancelShipment("TRACK-456");
        verifyStockReleasedFor(confirmedOrder);
        verify(orderRepository).updateStatus("ORDER-001", OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_fails_whenRefundFails() {
        givenOrderExists(confirmedOrder);
        givenRefundFails();

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertFailure(result, "Refund failed");
        verify(paymentService).refundPayment("TXN-123");
        verify(orderRepository, never()).updateStatus(anyString(), any());
        verifyNoInteractions(shippingService, stockService);
    }

    @Test
    void cancelOrder_skipsShipmentCancellation_whenOrderHasNoTrackingNumber() {
        Order orderWithoutShipment = OrderBuilder.confirmed()
            .withId("ORDER-002")
            .withTransaction("TXN-456")
            .withoutTracking()
            .build();

        givenOrderExists(orderWithoutShipment);
        givenRefundSucceeds();

        CancellationResult result = service.cancelOrder(orderWithoutShipment.id());

        assertSuccess(result);
        verify(paymentService).refundPayment("TXN-456");
        verifyNoInteractions(shippingService);
        verifyStockReleasedFor(orderWithoutShipment);
        verify(orderRepository).updateStatus("ORDER-002", OrderStatus.CANCELLED);
    }

    private void givenOrderExists(Order order) {
        when(orderRepository.findById(order.id())).thenReturn(order);
    }

    private void givenRefundSucceeds() {
        when(paymentService.refundPayment(anyString())).thenReturn(true);
    }

    private void givenRefundFails() {
        when(paymentService.refundPayment(anyString())).thenReturn(false);
    }

    private void givenShipmentCancelled() {
        when(shippingService.cancelShipment(anyString())).thenReturn(true);
    }

    private void verifyStockReleasedFor(Order order) {
        List<StockReservation> expectedReservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), true))
            .toList();

        verify(stockService).releaseStock(expectedReservations);
    }

    private void assertSuccess(CancellationResult result) {
        assertEquals(new CancellationResult.Success(), result);
    }

    private void assertFailure(CancellationResult result, String reason) {
        assertEquals(new CancellationResult.Failure(reason), result);
    }
}