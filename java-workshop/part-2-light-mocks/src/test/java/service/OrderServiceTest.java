package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.PaymentResult;
import domain.PaymentStatus;
import domain.ShippingConfirmation;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Test
    void createOrder_succeeds_whenAllStepsPass() {
        StockService stockService = mock(StockService.class);
        PricingService pricingService = mock(PricingService.class);
        PaymentService paymentService = mock(PaymentService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderService orderService = new OrderService(stockService, pricingService, paymentService, shippingService);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );
        var reservations = List.of(new StockReservation("PROD-001", 2, true));
        var shipment = new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25");

        when(stockService.checkAvailability(order.items())).thenReturn(true);
        when(pricingService.calculateTotal(order.items())).thenReturn(24.0);
        when(paymentService.processPayment(order.id(), order.customerId(), 24.0))
            .thenReturn(new PaymentResult(order.id(), PaymentStatus.SUCCESS, "TXN-789"));
        when(stockService.reserveStock(order.items())).thenReturn(reservations);
        when(shippingService.createShipment(order)).thenReturn(shipment);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Success.class, result);
        var success = (OrderResult.Success) result;
        assertEquals(OrderStatus.CONFIRMED, success.order().status());
        assertEquals(24.0, success.order().totalPrice());
        assertEquals(shipment, success.shippingConfirmation());

        InOrder inOrder = inOrder(stockService, pricingService, paymentService, shippingService);
        inOrder.verify(stockService).checkAvailability(order.items());
        inOrder.verify(pricingService).calculateTotal(order.items());
        inOrder.verify(paymentService).processPayment(order.id(), order.customerId(), 24.0);
        inOrder.verify(stockService).reserveStock(order.items());
        inOrder.verify(shippingService).createShipment(order);
    }

    @Test
    void createOrder_fails_whenStockIsUnavailable() {
        StockService stockService = mock(StockService.class);
        PricingService pricingService = mock(PricingService.class);
        PaymentService paymentService = mock(PaymentService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderService orderService = new OrderService(stockService, pricingService, paymentService, shippingService);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );
        var reservations = List.of(new StockReservation("PROD-001", 2, true));
        var shipment = new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25");

        when(stockService.checkAvailability(order.items())).thenReturn(false);
        when(pricingService.calculateTotal(order.items())).thenReturn(24.0);
        when(paymentService.processPayment(order.id(), order.customerId(), 24.0))
            .thenReturn(new PaymentResult(order.id(), PaymentStatus.SUCCESS, "TXN-789"));
        when(stockService.reserveStock(order.items())).thenReturn(reservations);
        when(shippingService.createShipment(order)).thenReturn(shipment);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());
        verify(stockService).checkAvailability(order.items());
        verifyNoInteractions(pricingService, paymentService, shippingService);
    }

    @Test
    void createOrder_failsAndRefunds_whenStockReservationFails() {
        StockService stockService = mock(StockService.class);
        PricingService pricingService = mock(PricingService.class);
        PaymentService paymentService = mock(PaymentService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderService orderService = new OrderService(stockService, pricingService, paymentService, shippingService);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );
        var reservations = List.of(new StockReservation("PROD-001", 2, false));
        var shipment = new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25");

        when(stockService.checkAvailability(order.items())).thenReturn(true);
        when(pricingService.calculateTotal(order.items())).thenReturn(24.0);
        when(paymentService.processPayment(order.id(), order.customerId(), 24.0))
            .thenReturn(new PaymentResult(order.id(), PaymentStatus.SUCCESS, "TXN-789"));
        when(stockService.reserveStock(order.items())).thenReturn(reservations);
        when(paymentService.refundPayment("TXN-789")).thenReturn(true);
        when(shippingService.createShipment(order)).thenReturn(shipment);

        OrderResult result = orderService.createOrder(order);

        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());
        verify(paymentService).refundPayment("TXN-789");
        verify(stockService).releaseStock(reservations);
        verifyNoInteractions(shippingService);
    }
}