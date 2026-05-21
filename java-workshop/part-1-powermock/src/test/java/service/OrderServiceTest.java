package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import infrastructure.Database;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * THE POWERMOCK NIGHTMARE
 * <p>
 * To test OrderService in isolation, we need to mock:
 * - StockService constructor
 * - PricingService constructor
 * - PaymentService constructor
 * - ShippingService constructor
 * - Database static methods
 * <p>
 * This is madness. But this is what "unit testing" means
 * when you take "isolation" to its extreme.
 */
class OrderServiceTest {

    // ========== DISCOUNT CODE ==========
    // TODO: Implement the scenarios below, following the steps in the README.
    //       Follow the same pattern as the tests above:
    //       1. Build the order
    //       2. Open a try-with-resources block with mockConstruction / mockStatic for each service
    //       3. Call placeOrder
    //       4. Assert the result
    //
    //       You will also need to add a mockConstruction for DiscountCodeService.
    //       Hint:
    //         var mockedDiscount = mockConstruction(DiscountCodeService.class,
    //             (mock, ctx) -> when(mock.checkDiscountCode(any(), any())).thenReturn(...));

    // --- Step 1: Guard clause — reject if discount code is already used ---

    @Disabled
    @Test
    void orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered

        fail("TODO: implement scenario 'discount code already used'");
    }

    // --- Step 2: Apply discount — calculate total with discount code ---

    @Disabled
    @Test
    void orderIsConfirmed_whenDiscountCodeIsValid() {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed

        fail("TODO: implement scenario 'order with a valid discount code'");
    }

    // --- Step 3: Mark as used — after payment, mark the discount code as used ---
    // Update the test above to also verify that markAsUsed is called on DiscountCodeService.
    // Hint: verify(mockedDiscount.constructed().getFirst()).markAsUsed(customerId, discountCode);

    // ========== EXISTING TESTS (for reference) ========================================

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

        try (// stock is available for all items
             var _mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 2, true))));
             // total price is 24.0
             var _mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(24.0));
             // payment succeeds
             var _mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")));
             // shipment is created
             var _mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")));
             var _mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var _mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-A", 3, true))));
             // total price is 45.0
             var _mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(order.items())).thenReturn(45.0));
             // payment succeeds when charged 45.0
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), eq(45.0)))
                     .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.SUCCESS, "TXN-001")));
             // shipment is created
             var _mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-001", "2024-12-25")));
             var _mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var _mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-B", 1, true))));
             // total price is 20.0
             var _mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(20.0));
             // payment succeeds for this specific customer
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), eq("CUSTOMER-JOHN-DOE"), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-002", PaymentStatus.SUCCESS, "TXN-002")));
             // shipment is created
             var _mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-002", "TRACK-002", "2024-12-25")));
             var _mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var _mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-C", 1, true))));
             // total price is 10.0
             var _mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(10.0));
             // payment succeeds with a specific transaction id
             var _mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-003", PaymentStatus.SUCCESS, "TRANSACTION-XYZ-789")));
             // shipment is created
             var _mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-003", "TRACK-003", "2024-12-25")));
             var _mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-D", 2, true))));
             // total price is 10.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(10.0));
             // payment succeeds
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-004", PaymentStatus.SUCCESS, "TXN-004")));
             // shipment is created with a specific tracking number
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-004", "TRACKING-NUMBER-ABC123", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-E", 1, true))));
             // total price is 100.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             // payment succeeds
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-005", PaymentStatus.SUCCESS, "TXN-005")));
             // shipment is created
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-005", "TRACK-005", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is available for all 3 products
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(
                     new StockReservation("PROD-A", 2, true),
                     new StockReservation("PROD-B", 1, true),
                     new StockReservation("PROD-C", 5, true)
                 )));
             // total price is 65.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(65.0));
             // payment succeeds
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-006", PaymentStatus.SUCCESS, "TXN-006")));
             // shipment is created
             var mockedShipping = mockConstruction(ShippingService.class,
                 (mock, ctx) -> when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-006", "TRACK-006", "2024-12-25")));
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

            verify(mockedStock.constructed().getFirst()).reserveStock(items);
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

        try (// stock is NOT available
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 10, false))));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().placeOrder(order);

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

        try (// stock is NOT available
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 5, false))));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is NOT available
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 3, false))));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is NOT available
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 1, false))));
             var mockedPricing = mockConstruction(PricingService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 1, true))));
             // total price is 100.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             // payment is declined
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-200", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            OrderResult result = new OrderService().placeOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
        }
    }

    @Test
    void stockIsReleased_whenPaymentFails() {
        var order = new Order(
            "ORDER-201",
            "CUST-201",
            List.of(new OrderItem("PROD-OK", 2, 50.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                when(mock.reserveStock(any()))
                        .thenReturn(List.of(new StockReservation("PROD-OK", 2, true))));
             // total price is 100.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(100.0));
             // payment is declined
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-201", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

            verify(mockedStock.constructed().getFirst()).releaseStock(any());
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

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 1, true))));
             // total price is 75.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(75.0));
             // payment is declined
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-202", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

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

        try (// stock is available for all items
             var mockedStock = mockConstruction(StockService.class,
                 (mock, ctx) -> when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 1, true))));
             // total price is 25.0
             var mockedPricing = mockConstruction(PricingService.class,
                 (mock, ctx) -> when(mock.calculateTotal(any())).thenReturn(25.0));
             // payment is declined
             var mockedPayment = mockConstruction(PaymentService.class,
                 (mock, ctx) -> when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-203", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedDatabase = mockStatic(Database.class)) {

            new OrderService().placeOrder(order);

            mockedDatabase.verifyNoInteractions();
        }
    }

}
