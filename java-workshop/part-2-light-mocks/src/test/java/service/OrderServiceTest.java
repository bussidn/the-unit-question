package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static domain.PaymentStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    private Order anOrder() {
        return new Order("ORDER-001", "CUST-001", List.of(new OrderItem("PROD-001", 1, 20.0)), OrderStatus.PENDING, 0.0, null, null);
    }

    // ── Nominal scenario ──────────────────────────────────────────────────────

    @Test
    void orderIsConfirmed_whenAllStepsSucceed() {
        var order = anOrder();
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(shippingService.createShipment(any())).thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        assertEquals(OrderStatus.CONFIRMED, ((OrderResult.Success) result).order().status());
    }

    @Test
    void calculatedPriceIsStoredInOrder_whenAllStepsSucceed() {
        var order = anOrder();
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(shippingService.createShipment(any())).thenReturn(new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25"));

        OrderResult result = orderService.createOrder(order);

        assertEquals(29.0, ((OrderResult.Success) result).order().totalPrice());
    }

    @Test
    void shippingConfirmationIsReturned_whenAllStepsSucceed() {
        var order = anOrder();
        var shipment = new ShippingConfirmation("ORDER-001", "TRACK-456", "2024-12-25");
        when(pricingService.calculateTotal(any())).thenReturn(29.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", SUCCESS, "TXN-123"));
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(shippingService.createShipment(any())).thenReturn(shipment);

        OrderResult result = orderService.createOrder(order);

        assertEquals(shipment, ((OrderResult.Success) result).shippingConfirmation());
    }

    // ── Stock unavailable ─────────────────────────────────────────────────────

    @Test
    void orderFails_whenStockIsUnavailable() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
    }

    @Test
    void paymentIsNotCharged_whenStockIsUnavailable() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        orderService.createOrder(anOrder());

        verifyNoInteractions(paymentService);
    }

    @Test
    void shipmentIsNotCreated_whenStockIsUnavailable() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, false)));

        orderService.createOrder(anOrder());

        verifyNoInteractions(shippingService);
    }

    // ── Payment declined ──────────────────────────────────────────────────────

    @Test
    void orderFails_whenPaymentIsDeclined() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        OrderResult result = orderService.createOrder(anOrder());

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Payment failed", ((OrderResult.Failure) result).reason());
    }

    @Test
    void shipmentIsNotCreated_whenPaymentIsDeclined() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        orderService.createOrder(anOrder());

        verifyNoInteractions(shippingService);
    }

    @Test
    void stockIsReleased_whenPaymentIsDeclined() {
        when(stockService.reserveStock(any())).thenReturn(List.of(new StockReservation("PROD-001", 1, true)));
        when(pricingService.calculateTotal(any())).thenReturn(45.0);
        when(paymentService.processPayment(anyString(), anyString(), anyDouble()))
            .thenReturn(new PaymentResult("ORDER-001", PaymentStatus.FAILED, null));

        orderService.createOrder(anOrder());

        verify(stockService).releaseStock(any());
    }
}
