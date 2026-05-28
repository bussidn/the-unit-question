package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.OrderRepository;

import java.util.List;

import static helper.PowerMock.invokePrivate;
import static helper.PowerMock.spyOn;
import static helper.PowerMock.whenPrivate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * THE MOCKCONSTRUCTION APPROACH — Unit = The Method
 * <p>
 * Here, "unit" means the method itself: {@code placeOrder()}.
 * We isolate it from all collaborators — StockService, PaymentService, ShippingService,
 * and the static PricingService — without touching the OrderService instance itself.
 * <p>
 * To achieve this isolation, we use:
 * <ul>
 *   <li>{@code mockConstruction()} — to intercept every {@code new} call inside {@code placeOrder()}</li>
 *   <li>{@code mockStatic()} — to stub the static call to {@code PricingService.calculateTotal()}</li>
 * </ul>
 * <p>
 * Because all three services are constructed at the top of {@code placeOrder()}, they are
 * always present in {@code constructed()} — even when their methods are never called.
 * Use {@code verify(..., never())} to assert that a method was not invoked.
 */
@SuppressWarnings("unused")
class OrderServiceTest {

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.4. Open a try-with-resources block with mockConstruction() for each service
    //      and mockStatic(PricingService.class). Configure the mocks in the initializer lambdas.
    //      Hint for DiscountCodeService:
    //        mockConstruction(DiscountCodeService.class, (mock, ctx) ->
    //            when(mock.checkDiscountCode(any(), any())).thenReturn(true/false));
    // 1.5. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p1_orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered

        fail("TODO: implement scenario 'discount code already used'");
    }
    // ✅ 1.6. Run: ./gradlew test — then back to README for Step 2
    // ─── END OF STEP 1 ──────────────────────────────────────────────────────



    // --- Step 2: Apply discount to the price ---
    // 2.2. Use mockStatic for PricingService.calculateTotal(items, discountCode).
    //      Verify the payment amount matches the discounted total.
    // 2.3. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p1_orderIsConfirmed_whenDiscountCodeIsValid() {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed

        fail("TODO: implement scenario 'order with a valid discount code'");
    }

    // ✅ 2.4. Run: ./gradlew test — then back to README for Step 3
    // ─── END OF STEP 2 ──────────────────────────────────────────────────────



    // --- Step 3: Mark as used after payment ---
    // 3.2. Update the test above to also verify that markAsUsed is called on DiscountCodeService.
    //      Hint: verify(constructed.get(0)).markAsUsed(customerId, discountCode);
    // 3.3. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p1_discountCodeIsMarkedAsUsed_afterSuccessfulPayment() {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used

        fail("TODO: implement scenario 'discount code marked as used after payment'");
    }
    // ✅ 3.4. Run: ./gradlew test ✅
    // ─── END OF STEP 3 ──────────────────────────────────────────────────────



    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

    // ========== VALIDATION (PowerMock.whenPrivate demo) ================================

    @Test
    void p1_orderIsRejected_whenValidationFails() {
        var order = new Order(
            "ORDER-999",
            "CUST-999",
            List.of(new OrderItem("PROD-001", 1, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class);
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(false);

            OrderResult result = spy.placeOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Invalid order", ((OrderResult.Failure) result).reason());
            verify(mockedStock.constructed().getFirst(), never()).reserveStock(any());
            verify(mockedPayment.constructed().getFirst(), never()).processPayment(any(), any(), anyDouble());
            verify(mockedRepo.constructed().getFirst(), never()).save(any());
        }
    }

    // ========== EXISTING TESTS (for reference) ========================================

    @Test
    void p1_orderIsConfirmed_whenAllStepsSucceed() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 2, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-123", PaymentStatus.SUCCESS, "TXN-789")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(24.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            OrderResult result = spy.placeOrder(order);

            assertInstanceOf(OrderResult.Success.class, result);
            assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
            verify(mockedRepo.constructed().getFirst()).save(any());
        }
    }

    @Test
    void p1_calculatedPriceIsSentToPayment() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("ITEM-A", 3, 15.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-A", 3, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.SUCCESS, "TXN-001")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-001", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(45.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedPayment.constructed().getFirst()).processPayment(any(), any(), eq(45.0));
        }
    }

    @Test
    void p1_customerIdIsUsedForPayment() {
        var order = new Order(
            "ORDER-002",
            "CUSTOMER-JOHN-DOE",
            List.of(new OrderItem("ITEM-B", 1, 20.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-B", 1, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-002", PaymentStatus.SUCCESS, "TXN-002")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-002", "TRACK-002", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(20.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedPayment.constructed().getFirst()).processPayment(any(), eq("CUSTOMER-JOHN-DOE"), anyDouble());
        }
    }

    @Test
    void p1_transactionIdIsStoredInConfirmedOrder() {
        var order = new Order(
            "ORDER-003",
            "CUST-003",
            List.of(new OrderItem("ITEM-C", 1, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-C", 1, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-003", PaymentStatus.SUCCESS, "TRANSACTION-XYZ-789")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-003", "TRACK-003", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(10.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            OrderResult result = spy.placeOrder(order);

            var success = (OrderResult.Success) result;
            assertEquals("TRANSACTION-XYZ-789", success.order().transactionId());
        }
    }

    @Test
    void p1_trackingNumberIsStoredInConfirmedOrder() {
        var order = new Order(
            "ORDER-004",
            "CUST-004",
            List.of(new OrderItem("ITEM-D", 2, 5.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("ITEM-D", 2, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-004", PaymentStatus.SUCCESS, "TXN-004")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-004", "TRACKING-NUMBER-ABC123", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(10.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            OrderResult result = spy.placeOrder(order);

            var success = (OrderResult.Success) result;
            assertEquals("TRACKING-NUMBER-ABC123", success.order().trackingNumber());
        }
    }

    @Test
    void p1_allOrderItemsAreCheckedForAvailability() {
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

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(
                     new StockReservation("PROD-A", 2, true),
                     new StockReservation("PROD-B", 1, true),
                     new StockReservation("PROD-C", 5, true)
                 )));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-006", PaymentStatus.SUCCESS, "TXN-006")));
             var mockedShipping = mockConstruction(ShippingService.class, (mock, ctx) ->
                 when(mock.createShipment(any()))
                     .thenReturn(new ShippingConfirmation("ORDER-006", "TRACK-006", "2024-12-25")));
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(65.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedStock.constructed().getFirst()).reserveStock(items);
        }
    }

    // ========== STOCK UNAVAILABLE ==========

    @Test
    void p1_orderFails_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-100",
            "CUST-100",
            List.of(new OrderItem("OUT-OF-STOCK", 10, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 10, false))));
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            OrderResult result = spy.placeOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
            verify(mockedRepo.constructed().getFirst(), never()).save(any());
        }
    }

    @Test
    void p1_paymentIsNotProcessed_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-101",
            "CUST-101",
            List.of(new OrderItem("OUT-OF-STOCK", 5, 20.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 5, false))));
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedPayment.constructed().getFirst(), never()).processPayment(any(), any(), anyDouble());
        }
    }

    @Test
    void p1_shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = new Order(
            "ORDER-102",
            "CUST-102",
            List.of(new OrderItem("OUT-OF-STOCK", 3, 30.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("OUT-OF-STOCK", 3, false))));
             var mockedPayment = mockConstruction(PaymentService.class);
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedShipping.constructed().getFirst(), never()).createShipment(any());
        }
    }

    // ========== PAYMENT FAILURE ==========

    @Test
    void p1_orderFails_whenPaymentIsDeclined() {
        var order = new Order(
            "ORDER-200",
            "CUST-200",
            List.of(new OrderItem("PROD-OK", 1, 100.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 1, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-200", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(100.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            OrderResult result = spy.placeOrder(order);

            assertInstanceOf(OrderResult.Failure.class, result);
            assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
            verify(mockedRepo.constructed().getFirst(), never()).save(any());
        }
    }

    @Test
    void p1_stockIsReleased_whenPaymentFails() {
        var order = new Order(
            "ORDER-201",
            "CUST-201",
            List.of(new OrderItem("PROD-OK", 2, 50.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 2, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-201", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(100.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedStock.constructed().getFirst()).releaseStock(any());
        }
    }

    @Test
    void p1_shipmentIsNotCreated_whenPaymentFails() {
        var order = new Order(
            "ORDER-202",
            "CUST-202",
            List.of(new OrderItem("PROD-OK", 1, 75.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        try (var mockedStock = mockConstruction(StockService.class, (mock, ctx) ->
                 when(mock.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-OK", 1, true))));
             var mockedPayment = mockConstruction(PaymentService.class, (mock, ctx) ->
                 when(mock.processPayment(any(), any(), anyDouble()))
                     .thenReturn(new PaymentResult("ORDER-202", PaymentStatus.FAILED, null)));
             var mockedShipping = mockConstruction(ShippingService.class);
             var mockedPricing = mockStatic(PricingService.class);
             var mockedRepo = mockConstruction(OrderRepository.class)) {

            mockedPricing.when(() -> PricingService.calculateTotal(any())).thenReturn(75.0);

            OrderService spy = spyOn(new OrderService());
            whenPrivate(spy, "isValid").thenReturn(true);
            spy.placeOrder(order);

            verify(mockedShipping.constructed().getFirst(), never()).createShipment(any());
        }
    }

    // ========== VALIDATION (isValid tested in isolation) ================================

    @Test
    void p1_isValid_returnsTrue_whenOrderIsPendingWithItems() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 1, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        boolean result = invokePrivate(new OrderService(), "isValid", order);

        assertTrue(result);
    }

    @Test
    void p1_isValid_returnsFalse_whenOrderIsAlreadyConfirmed() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 1, 10.0)),
            OrderStatus.CONFIRMED,
            50.0,
            "TXN-001",
            "TRACK-001"
        );

        boolean result = invokePrivate(new OrderService(), "isValid", order);

        assertFalse(result);
    }

    @Test
    void p1_isValid_returnsFalse_whenOrderHasNoItems() {
        var order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );

        boolean result = invokePrivate(new OrderService(), "isValid", order);

        assertFalse(result);
    }
}
