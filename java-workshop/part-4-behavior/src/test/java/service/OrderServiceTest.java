package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.StockReservation;
import helper.InMemoryPaymentGateway;
import helper.InMemoryShippingGateway;
import helper.InMemoryStockRepository;
import helper.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private InMemoryPaymentGateway paymentGateway;
    private InMemoryShippingGateway shippingGateway;
    private InMemoryStockRepository stockRepository;
    private OrderService orderService;
    private StockService mockStockService; // set by givenStockReservationFails()

    @BeforeEach
    void setUp() {
        stockRepository = new InMemoryStockRepository();
        paymentGateway = new InMemoryPaymentGateway();
        shippingGateway = new InMemoryShippingGateway();

        orderService = new OrderService(
            new RepositoryStockService(stockRepository),
            new PricingService(),
            new GatewayPaymentService(paymentGateway),
            new GatewayShippingService(shippingGateway)
        );
    }

    // ── Given helpers ─────────────────────────────────────────────────────────

    private OrderBuilder anOrder() {
        return OrderBuilder.anOrder()
            .withId("ORDER-001")
            .withCustomerId("CUST-001")
            .withItems(List.of(new OrderItem("PROD-001", 1, 20.0)));
    }

    private void givenStockIsAvailableFor(Order order) {
        order.items().forEach(item -> stockRepository.withAvailableStock(item.productId(), item.quantity()));
    }

    private void givenPaymentIsDeclined() {
        paymentGateway.willDecline();
    }

    private List<StockReservation> givenStockReservationFailsFor(Order order) {
        var failedReservations = order.items().stream()
            .map(item -> new StockReservation(item.productId(), item.quantity(), false))
            .toList();
        mockStockService = mock(StockService.class);
        when(mockStockService.checkAvailability(any())).thenReturn(true);
        when(mockStockService.reserveStock(any())).thenReturn(failedReservations);
        orderService = new OrderService(mockStockService, new PricingService(),
            new GatewayPaymentService(paymentGateway), new GatewayShippingService(shippingGateway));
        return failedReservations;
    }

    // ── Nominal scenario ──────────────────────────────────────────────────────

    @Test
    void orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.createOrder(order);

        double realTotal = 29.0; // subtotal=20, tax=4 (20%), shipping=5 (below 100€ threshold)
        assertEquals(realTotal, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);

        OrderResult result = orderService.createOrder(order);

        assertEquals(shippingGateway.lastConfirmation(), ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void orderFails_whenStockIsUnavailable() {
        var order = anOrder().build();
        // stockRepository is empty: no stock added

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsNotCharged_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.createOrder(order);

        assertFalse(paymentGateway.wasCharged());
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        var order = anOrder().build();

        orderService.createOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void orderFails_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void shipmentIsNotCreated_whenPaymentIsDeclined() {
        var order = anOrder().build();
        givenStockIsAvailableFor(order);
        givenPaymentIsDeclined();

        orderService.createOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }

    // ── Stock reservation fails (race condition — tested with mock StockService)

    @Test
    void orderFails_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockReservationFailsFor(order);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsRefunded_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockReservationFailsFor(order);

        orderService.createOrder(order);

        assertTrue(paymentGateway.wasRefunded(paymentGateway.lastTransactionId()));
    }

    @Test
    void stockIsReleased_whenStockReservationFails() {
        var order = anOrder().build();
        var failedReservations = givenStockReservationFailsFor(order);

        orderService.createOrder(order);

        verify(mockStockService).releaseStock(failedReservations);
    }

    @Test
    void shipmentIsNotCreated_whenStockReservationFails() {
        var order = anOrder().build();
        givenStockReservationFailsFor(order);

        orderService.createOrder(order);

        assertFalse(shippingGateway.hasCreatedShipment());
    }
}
