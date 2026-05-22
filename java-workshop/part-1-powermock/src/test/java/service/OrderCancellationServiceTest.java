package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderCancellationServiceTest {

    @Test
    void p1_cancelOrder_succeeds_whenOrderExists() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.CONFIRMED,
            24.0,
            "TXN-789",
            "TRACK-ABC"
        );

        try (MockedConstruction<OrderRepository> mockedRepository = mockConstruction(OrderRepository.class,
                 (mock, ctx) -> when(mock.findById("ORDER-123")).thenReturn(order));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class,
                 (mock, context) -> when(mock.refundPayment("TXN-789")).thenReturn(true));
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class,
                 (mock, context) -> when(mock.cancelShipment("TRACK-ABC")).thenReturn(true))) {

            CancellationResult result = new OrderCancellationService().cancelOrder("ORDER-123");

            assertInstanceOf(CancellationResult.Success.class, result);
            verify(mockedPayment.constructed().getFirst()).refundPayment("TXN-789");
            verify(mockedStock.constructed().getFirst()).releaseStock(argThat((List<StockReservation> reservations) ->
                reservations.size() == 1
                    && reservations.getFirst().productId().equals("PROD-001")
                    && reservations.getFirst().quantity() == 2
                    && reservations.getFirst().reserved()
            ));
            verify(mockedShipping.constructed().getFirst()).cancelShipment("TRACK-ABC");
            verify(mockedRepository.constructed().getFirst()).updateStatus("ORDER-123", OrderStatus.CANCELLED);
        }
    }

    @Test
    void p1_cancelOrder_fails_whenOrderIsMissing() {
        try (MockedConstruction<OrderRepository> mockedRepository = mockConstruction(OrderRepository.class,
                 (mock, ctx) -> when(mock.findById("UNKNOWN")).thenReturn(null));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class);
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class)) {

            CancellationResult result = new OrderCancellationService().cancelOrder("UNKNOWN");

            assertInstanceOf(CancellationResult.Failure.class, result);
            assertEquals("Order not found", ((CancellationResult.Failure) result).reason());
            assertEquals(1, mockedPayment.constructed().size());
            assertEquals(1, mockedStock.constructed().size());
            assertEquals(1, mockedShipping.constructed().size());
            verifyNoInteractions(mockedPayment.constructed().getFirst());
            verifyNoInteractions(mockedStock.constructed().getFirst());
            verifyNoInteractions(mockedShipping.constructed().getFirst());
            verify(mockedRepository.constructed().getFirst()).findById("UNKNOWN");
            verifyNoMoreInteractions(mockedRepository.constructed().getFirst());
        }
    }

    @Test
    void p1_cancelOrder_fails_whenRefundFails() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.CONFIRMED,
            24.0,
            "TXN-789",
            "TRACK-ABC"
        );

        try (MockedConstruction<OrderRepository> mockedRepository = mockConstruction(OrderRepository.class,
                 (mock, ctx) -> when(mock.findById("ORDER-123")).thenReturn(order));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class,
                 (mock, context) -> when(mock.refundPayment("TXN-789")).thenReturn(false));
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class)) {

            CancellationResult result = new OrderCancellationService().cancelOrder("ORDER-123");

            assertInstanceOf(CancellationResult.Failure.class, result);
            assertEquals("Refund failed", ((CancellationResult.Failure) result).reason());
            verify(mockedPayment.constructed().getFirst()).refundPayment("TXN-789");
            assertEquals(1, mockedStock.constructed().size());
            assertEquals(1, mockedShipping.constructed().size());
            verify(mockedStock.constructed().getFirst(), never()).releaseStock(any());
            verify(mockedShipping.constructed().getFirst(), never()).cancelShipment(anyString());
            verify(mockedRepository.constructed().getFirst()).findById("ORDER-123");
            verifyNoMoreInteractions(mockedRepository.constructed().getFirst());
        }
    }
}