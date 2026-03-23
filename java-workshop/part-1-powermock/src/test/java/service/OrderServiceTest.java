package service;

import domain.*;
import infrastructure.Database;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * THE POWERMOCK NIGHTMARE
 * 
 * To test OrderService in isolation, we need to mock:
 * - StockService constructor
 * - PricingService constructor
 * - PaymentService constructor
 * - ShippingService constructor
 * - Database static methods
 * 
 * This is madness. But this is what "unit testing" means
 * when you take "isolation" to its extreme.
 */
class OrderServiceTest {

    @Test
    void createOrder_succeeds_whenAllStepsPass() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class, (mock, context) -> {
                when(mock.checkAvailability(order.items())).thenReturn(true);
                when(mock.reserveStock(order.items())).thenReturn(List.of(
                    new StockReservation("PROD-001", 2, true)
                ));
            });
             MockedConstruction<PricingService> mockedPricing = mockConstruction(PricingService.class,
                 (mock, context) -> when(mock.calculateTotal(order.items())).thenReturn(24.0));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class, (mock, context) ->
                 when(mock.processPayment("ORDER-123", "CUST-456", 24.0))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")));
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class, (mock, context) ->
                 when(mock.createShipment(order)).thenReturn(
                     new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")
                 ));
             MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Success.class, result);
            var success = (OrderResult.Success) result;
            assertEquals(OrderStatus.CONFIRMED, success.order().status());
            assertEquals("TXN-789", success.order().transactionId());
            assertEquals("TRACK-ABC", success.order().trackingNumber());

            verify(mockedStock.constructed().getFirst()).checkAvailability(order.items());
            verify(mockedPricing.constructed().getFirst()).calculateTotal(order.items());
            verify(mockedPayment.constructed().getFirst()).processPayment("ORDER-123", "CUST-456", 24.0);
            verify(mockedStock.constructed().getFirst()).reserveStock(order.items());
            verify(mockedShipping.constructed().getFirst()).createShipment(order);
            mockedDatabase.verify(() -> Database.saveOrder(argThat((Order savedOrder) ->
                savedOrder.id().equals("ORDER-123")
                    && savedOrder.status() == OrderStatus.CONFIRMED
                    && "TXN-789".equals(savedOrder.transactionId())
                    && "TRACK-ABC".equals(savedOrder.trackingNumber())
            )));
        }
    }

    @Test
    void createOrder_fails_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class,
                (mock, context) -> when(mock.checkAvailability(order.items())).thenReturn(false));
             MockedConstruction<PricingService> mockedPricing = mockConstruction(PricingService.class);
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class);
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class);
             MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
            verify(mockedStock.constructed().getFirst()).checkAvailability(order.items());
            assertEquals(1, mockedPricing.constructed().size());
            assertEquals(1, mockedPayment.constructed().size());
            assertEquals(1, mockedShipping.constructed().size());
            verifyNoInteractions(mockedPricing.constructed().getFirst());
            verifyNoInteractions(mockedPayment.constructed().getFirst());
            verifyNoInteractions(mockedShipping.constructed().getFirst());
            mockedDatabase.verifyNoInteractions();
        }
    }

    @Test
    void createOrder_fails_whenPaymentFails() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class,
                (mock, context) -> when(mock.checkAvailability(order.items())).thenReturn(true));
             MockedConstruction<PricingService> mockedPricing = mockConstruction(PricingService.class,
                 (mock, context) -> when(mock.calculateTotal(order.items())).thenReturn(24.0));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class, (mock, context) ->
                 when(mock.processPayment("ORDER-123", "CUST-456", 24.0))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.FAILED, null)));
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class);
             MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
            verify(mockedStock.constructed().getFirst()).checkAvailability(order.items());
            verify(mockedPricing.constructed().getFirst()).calculateTotal(order.items());
            verify(mockedPayment.constructed().getFirst()).processPayment("ORDER-123", "CUST-456", 24.0);
            verify(mockedStock.constructed().getFirst(), never()).reserveStock(any());
            assertEquals(1, mockedShipping.constructed().size());
            verifyNoInteractions(mockedShipping.constructed().getFirst());
            mockedDatabase.verifyNoInteractions();
        }
    }

    @Test
    void createOrder_failsAndRefunds_whenStockReservationFails() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        var reservations = List.of(new StockReservation("PROD-001", 2, false));

        try (MockedConstruction<StockService> mockedStock = mockConstruction(StockService.class, (mock, context) -> {
                when(mock.checkAvailability(order.items())).thenReturn(true);
                when(mock.reserveStock(order.items())).thenReturn(reservations);
            });
             MockedConstruction<PricingService> mockedPricing = mockConstruction(PricingService.class,
                 (mock, context) -> when(mock.calculateTotal(order.items())).thenReturn(24.0));
             MockedConstruction<PaymentService> mockedPayment = mockConstruction(PaymentService.class, (mock, context) -> {
                 when(mock.processPayment("ORDER-123", "CUST-456", 24.0))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789"));
                 when(mock.refundPayment("TXN-789")).thenReturn(true);
             });
             MockedConstruction<ShippingService> mockedShipping = mockConstruction(ShippingService.class);
             MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
            verify(mockedPayment.constructed().getFirst()).refundPayment("TXN-789");
            verify(mockedStock.constructed().getFirst()).releaseStock(reservations);
            assertEquals(1, mockedShipping.constructed().size());
            verifyNoInteractions(mockedShipping.constructed().getFirst());
            mockedDatabase.verifyNoInteractions();
        }
    }
}