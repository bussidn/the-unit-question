package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderCancellationServiceTest {

    @Test
    void cancelOrder_succeeds_whenOrderExists() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.CONFIRMED,
            24.0,
            "TXN-789",
            "TRACK-ABC"
        );

        try (MockedStatic<OrderRepository> mockedRepository = mockStatic(OrderRepository.class);
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class,
                 (mock, context) -> when(mock.refundPayment("TXN-789")).thenReturn(true));
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class,
                 (mock, context) -> when(mock.cancelShipment("TRACK-ABC")).thenReturn(true))) {

            mockedRepository.when(() -> OrderRepository.findById("ORDER-123")).thenReturn(order);

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
            mockedRepository.verify(() -> OrderRepository.updateStatus("ORDER-123", OrderStatus.CANCELLED));
        }
    }

    @Test
    void cancelOrder_fails_whenOrderIsMissing() {
        try (MockedStatic<OrderRepository> mockedRepository = mockStatic(OrderRepository.class);
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class);
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class)) {

            mockedRepository.when(() -> OrderRepository.findById("UNKNOWN")).thenReturn(null);

            CancellationResult result = new OrderCancellationService().cancelOrder("UNKNOWN");

            assertInstanceOf(CancellationResult.Failure.class, result);
            assertEquals("Order not found", ((CancellationResult.Failure) result).reason());
            assertEquals(1, mockedPayment.constructed().size());
            assertEquals(1, mockedStock.constructed().size());
            assertEquals(1, mockedShipping.constructed().size());
            verifyNoInteractions(mockedPayment.constructed().getFirst());
            verifyNoInteractions(mockedStock.constructed().getFirst());
            verifyNoInteractions(mockedShipping.constructed().getFirst());
            mockedRepository.verify(() -> OrderRepository.findById("UNKNOWN"));
            mockedRepository.verifyNoMoreInteractions();
        }
    }

    @Test
    void cancelOrder_fails_whenRefundFails() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.CONFIRMED,
            24.0,
            "TXN-789",
            "TRACK-ABC"
        );

        try (MockedStatic<OrderRepository> mockedRepository = mockStatic(OrderRepository.class);
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class,
                 (mock, context) -> when(mock.refundPayment("TXN-789")).thenReturn(false));
             MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class)) {

            mockedRepository.when(() -> OrderRepository.findById("ORDER-123")).thenReturn(order);

            CancellationResult result = new OrderCancellationService().cancelOrder("ORDER-123");

            assertInstanceOf(CancellationResult.Failure.class, result);
            assertEquals("Refund failed", ((CancellationResult.Failure) result).reason());
            verify(mockedPayment.constructed().getFirst()).refundPayment("TXN-789");
            assertEquals(1, mockedStock.constructed().size());
            assertEquals(1, mockedShipping.constructed().size());
            verify(mockedStock.constructed().getFirst(), never()).releaseStock(any());
            verify(mockedShipping.constructed().getFirst(), never()).cancelShipment(anyString());
            mockedRepository.verify(() -> OrderRepository.findById("ORDER-123"));
            mockedRepository.verifyNoMoreInteractions();
        }
    }
}