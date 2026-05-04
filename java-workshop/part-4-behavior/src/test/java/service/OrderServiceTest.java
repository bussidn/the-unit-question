package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentRequest;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.ShippingRequest;
import domain.StockReservation;
import gateway.PaymentGateway;
import gateway.ShippingGateway;
import helper.InMemoryStockRepository;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private PaymentGateway paymentGateway;
    @Mock private ShippingGateway shippingGateway;

    private InMemoryStockRepository stockRepository;
    private OrderService orderService;
    private StockService mockStockService; // set by givenStockReservationFails()

    @BeforeEach
    void setUp() {
        stockRepository = new InMemoryStockRepository();

        StockService stockService = new RepositoryStockService(stockRepository);
        PricingService pricingService = new PricingService();
        PaymentService paymentService = new GatewayPaymentService(paymentGateway);
        ShippingService shippingService = new GatewayShippingService(shippingGateway);

        orderService = new OrderService(stockService, pricingService, paymentService, shippingService);
    }

    // ── Given helpers ─────────────────────────────────────────────────────────

    private Order anOrder() {
        return OrderBuilder.anOrder()
            .withId("ORDER-001")
            .withCustomerId("CUST-001")
            .withItems(List.of(new OrderItem("PROD-001", 1, 20.0)))
            .build();
    }

    private void givenStockIsAvailable() {
        stockRepository.withAvailableStock("PROD-001", 1);
    }

    private String givenPaymentSucceeds() {
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.SUCCESS, "TXN-123"));
        return "TXN-123";
    }

    private void givenPaymentIsDeclined() {
        when(paymentGateway.process(any(PaymentRequest.class)))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));
    }

    private ShippingConfirmation givenShipmentCreated() {
        var shipment = new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25");
        when(shippingGateway.createShipment(any(ShippingRequest.class))).thenReturn(shipment);
        return shipment;
    }

    private List<StockReservation> givenStockReservationFails() {
        var failedReservations = List.of(new StockReservation("PROD-001", 1, false));
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
        givenStockIsAvailable();
        givenPaymentSucceeds();
        givenShipmentCreated();

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        givenStockIsAvailable();
        givenPaymentSucceeds();
        givenShipmentCreated();

        OrderResult result = orderService.createOrder(anOrder());

        double realTotal = 29.0; // subtotal=20, tax=4 (20%), shipping=5 (below 100€ threshold)
        assertEquals(realTotal, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void shippingConfirmationIsReturned_whenAllStepsSucceed() {
        givenStockIsAvailable();
        givenPaymentSucceeds();
        ShippingConfirmation shipment = givenShipmentCreated();

        OrderResult result = orderService.createOrder(anOrder());

        assertEquals(shipment, ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void orderFails_whenStockIsUnavailable() {
        // stockRepository is empty: no stock added

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsNotCharged_whenStockIsUnavailable() {
        orderService.createOrder(anOrder());

        verifyNoInteractions(paymentGateway);
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        orderService.createOrder(anOrder());

        verifyNoInteractions(shippingGateway);
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void orderFails_whenPaymentIsDeclined() {
        givenStockIsAvailable();
        givenPaymentIsDeclined();

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void shipmentIsNotCreated_whenPaymentIsDeclined() {
        givenStockIsAvailable();
        givenPaymentIsDeclined();

        orderService.createOrder(anOrder());

        verifyNoInteractions(shippingGateway);
    }

    // ── Stock reservation fails (race condition — tested with mock StockService)

    @Test
    void orderFails_whenStockReservationFails() {
        givenStockReservationFails();
        givenPaymentSucceeds();

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsRefunded_whenStockReservationFails() {
        givenStockReservationFails();
        var txnId = givenPaymentSucceeds();

        orderService.createOrder(anOrder());

        verify(paymentGateway).refund(txnId);
    }

    @Test
    void stockIsReleased_whenStockReservationFails() {
        var failedReservations = givenStockReservationFails();
        givenPaymentSucceeds();

        orderService.createOrder(anOrder());

        verify(mockStockService).releaseStock(failedReservations);
    }

    @Test
    void shipmentIsNotCreated_whenStockReservationFails() {
        givenStockReservationFails();
        givenPaymentSucceeds();

        orderService.createOrder(anOrder());

        verifyNoInteractions(shippingGateway);
    }
}
