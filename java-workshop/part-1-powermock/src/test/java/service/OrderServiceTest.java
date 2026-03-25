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

    // ========== SUCCESS SCENARIOS ==========

    @Test
    void orderIsConfirmed_whenAllStepsSucceed() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 2, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(24.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Success.class, result);
            assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
        }
    }

    @Test
    void calculatedPriceIsSentToPayment() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("ITEM-A", 3, 15.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-A", 3, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(order.items())).thenReturn(45.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), eq(45.0)))
                     .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.SUCCESS, "TXN-001")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-001", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedPayment.constructed().getFirst()).processPayment(any(), any(), eq(45.0));
        }
    }

    @Test
    void customerIdIsUsedForPayment() {
        var order = new Order(
            "ORDER-002",
            "CUSTOMER-JOHN-DOE",
            List.of(new OrderItem("ITEM-B", 1, 20.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-B", 1, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(20.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), eq("CUSTOMER-JOHN-DOE"), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-002", PaymentStatus.SUCCESS, "TXN-002")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-002", "TRACK-002", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedPayment.constructed().getFirst()).processPayment(any(), eq("CUSTOMER-JOHN-DOE"), anyDouble());
        }
    }

    @Test
    void transactionIdIsStoredInConfirmedOrder() {
        var order = new Order(
            "ORDER-003",
            "CUST-003",
            List.of(new OrderItem("ITEM-C", 1, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-C", 1, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(10.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-003", PaymentStatus.SUCCESS, "TRANSACTION-XYZ-789")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-003", "TRACK-003", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            var success = (OrderResult.Success) result;
            assertEquals("TRANSACTION-XYZ-789", success.order().transactionId());
        }
    }

    @Test
    void trackingNumberIsStoredInConfirmedOrder() {
        var order = new Order(
            "ORDER-004",
            "CUST-004",
            List.of(new OrderItem("ITEM-D", 2, 5.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-D", 2, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(10.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-004", PaymentStatus.SUCCESS, "TXN-004")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-004", "TRACKING-NUMBER-ABC123", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            var success = (OrderResult.Success) result;
            assertEquals("TRACKING-NUMBER-ABC123", success.order().trackingNumber());
        }
    }

    @Test
    void confirmedOrderIsSavedToDatabase() {
        var order = new Order(
            "ORDER-005",
            "CUST-005",
            List.of(new OrderItem("ITEM-E", 1, 100.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-E", 1, true)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-005", PaymentStatus.SUCCESS, "TXN-005")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-005", "TRACK-005", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            mockedDatabase.verify(() -> Database.saveOrder(argThat((Order saved) ->
                saved.id().equals("ORDER-005")
                    && saved.status() == OrderStatus.CONFIRMED
                    && saved.transactionId() != null
                    && saved.trackingNumber() != null
            )));
        }
    }

    @Test
    void allOrderItemsAreCheckedForAvailability() {
        var items = List.of(
            new OrderItem("PROD-A", 2, 10.0),
            new OrderItem("PROD-B", 1, 20.0),
            new OrderItem("PROD-C", 5, 5.0)
        );
        var order = new Order(
            "ORDER-006",
            "CUST-006",
            items,
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(items)).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(
                    new StockReservation("PROD-A", 2, true),
                    new StockReservation("PROD-B", 1, true),
                    new StockReservation("PROD-C", 5, true)
                ));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(65.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-006", PaymentStatus.SUCCESS, "TXN-006")));
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-006", "TRACK-006", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedStock.constructed().getFirst()).checkAvailability(items);
        }
    }

    // ========== STOCK UNAVAILABLE ==========

    @Test
    void orderFails_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-100",
            "CUST-100",
            List.of(new OrderItem("OUT-OF-STOCK", 10, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(false));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
        }
    }

    @Test
    void paymentIsNotProcessed_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-101",
            "CUST-101",
            List.of(new OrderItem("OUT-OF-STOCK", 5, 20.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(false));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verifyNoInteractions(mockedPayment.constructed().getFirst());
        }
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-102",
            "CUST-102",
            List.of(new OrderItem("OUT-OF-STOCK", 3, 30.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(false));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verifyNoInteractions(mockedShipping.constructed().getFirst());
        }
    }

    @Test
    void orderIsNotSaved_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-103",
            "CUST-103",
            List.of(new OrderItem("OUT-OF-STOCK", 1, 50.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(false));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            mockedDatabase.verifyNoInteractions();
        }
    }

    // ========== PAYMENT FAILURE ==========

    @Test
    void orderFails_whenPaymentIsDeclined() {
        var order = new Order(
            "ORDER-200",
            "CUST-200",
            List.of(new OrderItem("PROD-OK", 1, 100.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(true));
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-200", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
        }
    }

    @Test
    void stockIsNotReserved_whenPaymentFails() {
        var order = new Order(
            "ORDER-201",
            "CUST-201",
            List.of(new OrderItem("PROD-OK", 2, 50.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(true));
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-201", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedStock.constructed().getFirst(), never()).reserveStock(any());
        }
    }

    @Test
    void shipmentIsNotCreated_whenPaymentFails() {
        var order = new Order(
            "ORDER-202",
            "CUST-202",
            List.of(new OrderItem("PROD-OK", 1, 75.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(true));
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(75.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-202", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verifyNoInteractions(mockedShipping.constructed().getFirst());
        }
    }

    @Test
    void orderIsNotSaved_whenPaymentFails() {
        var order = new Order(
            "ORDER-203",
            "CUST-203",
            List.of(new OrderItem("PROD-OK", 1, 25.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.checkAvailability(any())).thenReturn(true));
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(25.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-203", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            mockedDatabase.verifyNoInteractions();
        }
    }

    // ========== STOCK RESERVATION FAILURE ==========

    @Test
    void orderFails_whenStockReservationFails() {
        var order = new Order(
            "ORDER-300",
            "CUST-300",
            List.of(new OrderItem("PROD-RACE", 1, 80.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-RACE", 1, false)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(80.0));
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-300", PaymentStatus.SUCCESS, "TXN-300")));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().createOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
        }
    }

    @Test
    void paymentIsRefunded_whenStockReservationFails() {
        var order = new Order(
            "ORDER-301",
            "CUST-301",
            List.of(new OrderItem("PROD-RACE", 2, 40.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-RACE", 2, false)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(80.0));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) -> {
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-301", PaymentStatus.SUCCESS, "TXN-TO-REFUND"));
                 when(mock.refundPayment("TXN-TO-REFUND")).thenReturn(true);
             });
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedPayment.constructed().getFirst()).refundPayment("TXN-TO-REFUND");
        }
    }

    @Test
    void stockIsReleased_whenReservationFails() {
        var order = new Order(
            "ORDER-302",
            "CUST-302",
            List.of(new OrderItem("PROD-RACE", 3, 30.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );
        var failedReservations = List.of(new StockReservation("PROD-RACE", 3, false));

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(failedReservations);
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(90.0));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) -> {
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-302", PaymentStatus.SUCCESS, "TXN-302"));
                 when(mock.refundPayment(any())).thenReturn(true);
             });
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verify(mockedStock.constructed().getFirst()).releaseStock(failedReservations);
        }
    }

    @Test
    void shipmentIsNotCreated_whenReservationFails() {
        var order = new Order(
            "ORDER-303",
            "CUST-303",
            List.of(new OrderItem("PROD-RACE", 1, 60.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-RACE", 1, false)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(60.0));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) -> {
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-303", PaymentStatus.SUCCESS, "TXN-303"));
                 when(mock.refundPayment(any())).thenReturn(true);
             });
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            verifyNoInteractions(mockedShipping.constructed().getFirst());
        }
    }

    @Test
    void orderIsNotSaved_whenReservationFails() {
        var order = new Order(
            "ORDER-304",
            "CUST-304",
            List.of(new OrderItem("PROD-RACE", 4, 20.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) -> {
                when(mock.checkAvailability(any())).thenReturn(true);
                when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-RACE", 4, false)));
            });
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(80.0));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) -> {
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-304", PaymentStatus.SUCCESS, "TXN-304"));
                 when(mock.refundPayment(any())).thenReturn(true);
             });
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().createOrder(order);

            mockedDatabase.verifyNoInteractions();
        }
    }
}