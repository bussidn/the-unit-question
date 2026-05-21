package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static domain.PaymentStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    private StockService stockService;
    private PricingService pricingService;
    private PaymentService paymentService;
    private ShippingService shippingService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        stockService = mock(StockService.class);
        pricingService = mock(PricingService.class);
        paymentService = mock(PaymentService.class);
        shippingService = mock(ShippingService.class);
        orderService = new OrderService(stockService, pricingService, paymentService, shippingService);
    }

    // ── Discount code ─────────────────────────────────────────────────────────
    // TODO: Implement the scenarios below, following the steps in the README.
    //       Follow the same pattern as the tests above:
    //       1. Add a DiscountCodeService mock field + inject it via the OrderService constructor
    //       2. Stub it with when(discountCodeService.checkDiscountCode(...)).thenReturn(...)
    //       3. Call placeOrder
    //       4. Assert the result

    // --- Step 1: Guard clause — reject if discount code is already used ---

    @Disabled
    @Test
    void p2_orderIsRejected_whenDiscountCodeIsAlreadyUsed() {
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
    void p2_orderIsConfirmed_whenDiscountCodeIsValid() {
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
    void p2_orderIsConfirmed_whenAllStepsSucceed() {
        var order = new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null);
        // total price is 29.0
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        // payment succeeds
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // shipment is created
        when(shippingService.createShipment(any())).thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = orderService.placeOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void p2_calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null);
        // total price is 29.0
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        // payment succeeds
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // shipment is created
        when(shippingService.createShipment(any())).thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = orderService.placeOrder(order);

        assertEquals(29.0, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void p2_shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null);
        var shipment = new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25");
        // total price is 29.0
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        // payment succeeds
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // shipment is created with the expected confirmation
        when(shippingService.createShipment(any())).thenReturn(shipment);

        OrderResult result = orderService.placeOrder(order);

        assertEquals(shipment, ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void p2_orderFails_whenStockIsUnavailable() {
        // stock is NOT available
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        OrderResult result = orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p2_paymentIsNotCharged_whenStockIsUnavailable() {
        // stock is NOT available
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        verifyNoInteractions(paymentService);
    }

    @Test
    void p2_shipmentIsNotCreated_whenStockIsUnavailable() {
        // stock is NOT available
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        verifyNoInteractions(shippingService);
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void p2_orderFails_whenPaymentIsDeclined() {
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // total price is 45.0
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        // payment is declined
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        OrderResult result = orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void p2_shipmentIsNotCreated_whenPaymentIsDeclined() {
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // total price is 45.0
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        // payment is declined
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        verifyNoInteractions(shippingService);
    }

    @Test
    void p2_stockIsReleased_whenPaymentIsDeclined() {
        // stock is available for all items
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        // total price is 45.0
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        // payment is declined
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        orderService.placeOrder(new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null));

        verify(stockService).releaseStock(any());
    }
}
