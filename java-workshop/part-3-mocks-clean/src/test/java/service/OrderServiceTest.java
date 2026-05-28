package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import helper.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private StockService stockService;
    @Mock private PaymentService paymentService;
    @Mock private ShippingService shippingService;
    @Mock private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, stockService, new PricingService(), paymentService, shippingService);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // YOUR EXERCISE — See README for full instructions
    // ══════════════════════════════════════════════════════════════════════════

    // --- Step 1: Reject already-used discount codes ---
    // 1.4. Add a @Mock DiscountCodeService field and inject it via the OrderService constructor.
    //      Create a given helper (e.g. givenDiscountCodeIsAlreadyUsed).
    // 1.5. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p3_orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
        // GIVEN an order with discount code SUMMER20
        // AND this code has already been used by this customer

        // WHEN the customer places the order

        // THEN the order is rejected with reason "Discount code already used"
        // AND no payment is triggered

        fail("TODO: implement scenario 'discount code already used'");
    }
    // ✅ 1.6. Run: ./gradlew test — then back to README for Step 2

    // --- Step 2: Apply discount to the price ---
    // 2.2. Create a given helper for a valid discount code (e.g. givenDiscountCodeIsValid).
    //      Wire all given helpers needed for a successful order (stock, payment, shipping).
    // 2.3. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p3_orderIsConfirmed_whenDiscountCodeIsValid() {
        // GIVEN an order with 2 items at €55 each (subtotal: €110)
        // AND discount code SUMMER20 (-20%)
        // AND the code has not yet been used by this customer

        // WHEN the customer places the order

        // THEN payment is processed for €105.60
        // AND the order is confirmed

        fail("TODO: implement scenario 'order with a valid discount code'");
    }

    // ✅ 2.4. Run: ./gradlew test — then back to README for Step 3

    // --- Step 3: Mark as used after payment ---
    // 3.2. Update the test above to also verify that markAsUsed is called on DiscountCodeService.
    //      Hint: verify(discountCodeService).markAsUsed(customerId, discountCode);
    // 3.3. Fill in the GIVEN/WHEN/THEN below

    @Disabled
    @Test
    void p3_discountCodeIsMarkedAsUsed_afterSuccessfulPayment() {
        // GIVEN an order with a valid discount code
        // AND payment succeeds

        // WHEN the customer places the order

        // THEN the discount code is marked as used

        fail("TODO: implement scenario 'discount code marked as used after payment'");
    }
    // ✅ 3.4. Run: ./gradlew test ✅

    // ══════════════════════════════════════════════════════════════════════════
    // REFERENCE TESTS — existing tests and helpers, for inspiration
    // ══════════════════════════════════════════════════════════════════════════

    // ── Given helpers ─────────────────────────────────────────────────────────

    private OrderBuilder anOrder() {
        return OrderBuilder.anOrder()
            .withId("ORDER-001")
            .withCustomerId("CUST-001")
            .withItems(List.of(new OrderItem("PROD-001", 1, 20.0)));
    }

    private String givenPaymentSucceedsFor(Order order) {
        var txnId = "TXN-" + order.id();
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult(order.id(), PaymentStatus.SUCCESS, txnId));
        return txnId;
    }

    private void givenPaymentIsDeclinedFor(Order order) {
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult(order.id(), PaymentStatus.FAILED, null));
    }

    private List<StockReservation> givenReservationSucceedsFor(Order order) {
        var reservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), true))
            .toList();
        when(stockService.reserveStock(any())).thenReturn(reservations);
        return reservations;
    }

    private List<StockReservation> givenReservationFailsFor(Order order) {
        var reservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), false))
            .toList();
        when(stockService.reserveStock(any())).thenReturn(reservations);
        return reservations;
    }

    private ShippingConfirmation givenShipmentCreatedFor(Order order) {
        var shipment = new ShippingConfirmation(order.id(), "TRACK-456", "2024-12-25");
        when(shippingService.createShipment(any(Order.class))).thenReturn(shipment);
        return shipment;
    }

    // ── Validation ────────────────────────────────────────────────────────────

    @Test
    void p3_orderIsRejected_whenOrderIsAlreadyConfirmed() {
        var order = anOrder().withStatus(OrderStatus.CONFIRMED).build();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Invalid order", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p3_orderIsRejected_whenOrderHasNoItems() {
        var order = anOrder().withItems(List.of()).build();

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Invalid order", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p3_noStockIsReserved_whenOrderIsInvalid() {
        var order = anOrder().withItems(List.of()).build();

        orderService.placeOrder(order);

        verifyNoInteractions(stockService);
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    void p3_orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void p3_calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        // 1 x €20.00 → subtotal €20 + tax 20% (€4) + shipping €5 = €29.00
        var order = anOrder().build();
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(29.0, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void p3_shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        ShippingConfirmation shipment = givenShipmentCreatedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(shipment, ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void p3_orderFails_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenReservationFailsFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p3_paymentIsNotCharged_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenReservationFailsFor(order);

        orderService.placeOrder(order);

        verifyNoInteractions(paymentService);
    }

    @Test
    void p3_shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenReservationFailsFor(order);

        orderService.placeOrder(order);

        verifyNoInteractions(shippingService);
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void p3_orderFails_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenReservationSucceedsFor(order);
        givenPaymentIsDeclinedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p3_shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenReservationSucceedsFor(order);
        givenPaymentIsDeclinedFor(order);

        orderService.placeOrder(order);

        verifyNoInteractions(shippingService);
    }

    @Test
    void p3_stockIsReleased_whenPaymentIsDeclined() {
        var order = anOrder().build();
        List<StockReservation> reservations = givenReservationSucceedsFor(order);
        givenPaymentIsDeclinedFor(order);

        orderService.placeOrder(order);

        verify(stockService).releaseStock(reservations);
    }
}
