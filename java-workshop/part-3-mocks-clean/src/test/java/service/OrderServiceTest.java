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
    @Mock private PricingService pricingService;
    @Mock private PaymentService paymentService;
    @Mock private ShippingService shippingService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(stockService, pricingService, paymentService, shippingService);
    }

    // ── Given helpers ─────────────────────────────────────────────────────────

    private OrderBuilder anOrder() {
        return OrderBuilder.anOrder()
            .withId("ORDER-001")
            .withCustomerId("CUST-001")
            .withItems(List.of(new OrderItem("PROD-001", 1, 20.0)));
    }

    private void givenPricingCalculates(double total) {
        when(pricingService.calculateTotal(any())).thenReturn(total);
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

    // ── Discount code ─────────────────────────────────────────────────────────
    // TODO: Implement the scenarios below, following the steps in the README.
    //       Follow the same pattern as the tests above:
    //       1. Add a @Mock DiscountCodeService field + inject it via the OrderService constructor
    //       2. Create given helpers (e.g. givenDiscountCodeIsValid, givenDiscountCodeIsAlreadyUsed)
    //       3. Call placeOrder
    //       4. Assert the result

    // --- Step 1: Guard clause — reject if discount code is already used ---

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

    // --- Step 2: Apply discount — calculate total with discount code ---

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

    // --- Step 3: Mark as used — after payment, mark the discount code as used ---
    // Update the test above to also verify that markAsUsed is called on DiscountCodeService.
    // Hint: verify(discountCodeService).markAsUsed(customerId, discountCode);

    // ── Existing tests (for reference) ─────────────────────────────────────────

    @Test
    void p3_orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenPricingCalculates(29.0);
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void p3_calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenPricingCalculates(29.0);
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(29.0, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void p3_shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenPricingCalculates(29.0);
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
        givenPricingCalculates(45.0);
        givenPaymentIsDeclinedFor(order);

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p3_shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenReservationSucceedsFor(order);
        givenPricingCalculates(45.0);
        givenPaymentIsDeclinedFor(order);

        orderService.placeOrder(order);

        verifyNoInteractions(shippingService);
    }

    @Test
    void p3_stockIsReleased_whenPaymentIsDeclined() {
        var order = anOrder().build();
        List<StockReservation> reservations = givenReservationSucceedsFor(order);
        givenPricingCalculates(45.0);
        givenPaymentIsDeclinedFor(order);

        orderService.placeOrder(order);

        verify(stockService).releaseStock(reservations);
    }
}
