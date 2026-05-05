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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    private void givenStockIsAvailable() {
        when(stockService.checkAvailability(any())).thenReturn(true);
    }

    private void givenStockIsUnavailable() {
        when(stockService.checkAvailability(any())).thenReturn(false);
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

    // ── Nominal scenario ──────────────────────────────────────────────────────

    @Test
    void orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(29.0);
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(29.0);
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        givenShipmentCreatedFor(order);

        OrderResult result = orderService.createOrder(order);

        assertEquals(29.0, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(29.0);
        givenPaymentSucceedsFor(order);
        givenReservationSucceedsFor(order);
        ShippingConfirmation shipment = givenShipmentCreatedFor(order);

        OrderResult result = orderService.createOrder(order);

        assertEquals(shipment, ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void orderFails_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenStockIsUnavailable();

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsNotCharged_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenStockIsUnavailable();

        orderService.createOrder(order);

        verifyNoInteractions(paymentService);
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = anOrder().build();
        givenStockIsUnavailable();

        orderService.createOrder(order);

        verifyNoInteractions(shippingService);
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void orderFails_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(45.0);
        givenPaymentIsDeclinedFor(order);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(45.0);
        givenPaymentIsDeclinedFor(order);

        orderService.createOrder(order);

        verifyNoInteractions(shippingService);
    }

    // ── Stock reservation fails (race condition) ──────────────────────────────

    @Test
    void orderFails_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(18.0);
        givenPaymentSucceedsFor(order);
        givenReservationFailsFor(order);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsRefunded_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(18.0);
        var txnId = givenPaymentSucceedsFor(order);
        givenReservationFailsFor(order);

        orderService.createOrder(order);

        verify(paymentService).refundPayment(txnId);
    }

    @Test
    void stockIsReleased_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(18.0);
        givenPaymentSucceedsFor(order);
        List<StockReservation> reservations = givenReservationFailsFor(order);

        orderService.createOrder(order);

        verify(stockService).releaseStock(reservations);
    }

    @Test
    void shipmentIsNotCreated_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockIsAvailable();
        givenPricingCalculates(18.0);
        givenPaymentSucceedsFor(order);
        givenReservationFailsFor(order);

        orderService.createOrder(order);

        verifyNoInteractions(shippingService);
    }
}
