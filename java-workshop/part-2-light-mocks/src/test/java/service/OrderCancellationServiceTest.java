package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.StockReservation;
import org.junit.jupiter.api.Test;
import repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class OrderCancellationServiceTest {

    @Test
    void cancelOrder_refundsPayment_whenOrderExists() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentService paymentService = mock(PaymentService.class);
        StockService stockService = mock(StockService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderCancellationService service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        Order order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 2, 25.0)),
            OrderStatus.CONFIRMED,
            50.0,
            "TXN-123",
            null
        );

        when(orderRepository.findById("ORDER-001")).thenReturn(order);
        when(paymentService.refundPayment("TXN-123")).thenReturn(true);
        when(shippingService.cancelShipment("TRACK-456")).thenReturn(true);

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertEquals(new CancellationResult.Success(), result);
        verify(paymentService).refundPayment("TXN-123");
        verify(stockService).releaseStock(List.of(new StockReservation("PROD-001", 2, true)));
        verify(orderRepository).updateStatus("ORDER-001", OrderStatus.CANCELLED);
        verifyNoInteractions(shippingService);
    }

    @Test
    void cancelOrder_cancelsShipment_whenTrackingNumberExists() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentService paymentService = mock(PaymentService.class);
        StockService stockService = mock(StockService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderCancellationService service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        Order order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 2, 25.0)),
            OrderStatus.CONFIRMED,
            50.0,
            "TXN-123",
            "TRACK-456"
        );

        when(orderRepository.findById("ORDER-001")).thenReturn(order);
        when(paymentService.refundPayment("TXN-123")).thenReturn(true);
        when(shippingService.cancelShipment("TRACK-456")).thenReturn(true);

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertEquals(new CancellationResult.Success(), result);
        verify(paymentService).refundPayment("TXN-123");
        verify(stockService).releaseStock(List.of(new StockReservation("PROD-001", 2, true)));
        verify(shippingService).cancelShipment("TRACK-456");
        verify(orderRepository).updateStatus("ORDER-001", OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_fails_whenRefundFails() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentService paymentService = mock(PaymentService.class);
        StockService stockService = mock(StockService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderCancellationService service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        Order order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 2, 25.0)),
            OrderStatus.CONFIRMED,
            50.0,
            "TXN-123",
            "TRACK-456"
        );

        when(orderRepository.findById("ORDER-001")).thenReturn(order);
        when(paymentService.refundPayment("TXN-123")).thenReturn(false);
        when(shippingService.cancelShipment("TRACK-456")).thenReturn(true);

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertEquals(new CancellationResult.Failure("Refund failed"), result);
        verify(paymentService).refundPayment("TXN-123");
        verify(stockService, never()).releaseStock(any());
        verify(shippingService, never()).cancelShipment(anyString());
        verify(orderRepository, never()).updateStatus(anyString(), any());
    }

    @Test
    void cancelOrder_fails_whenOrderDoesNotExist() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        PaymentService paymentService = mock(PaymentService.class);
        StockService stockService = mock(StockService.class);
        ShippingService shippingService = mock(ShippingService.class);
        OrderCancellationService service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);

        Order order = new Order(
            "ORDER-001",
            "CUST-001",
            List.of(new OrderItem("PROD-001", 2, 25.0)),
            OrderStatus.CONFIRMED,
            50.0,
            "TXN-123",
            "TRACK-456"
        );

        when(orderRepository.findById("ORDER-001")).thenReturn(null);
        when(paymentService.refundPayment("TXN-123")).thenReturn(true);
        when(shippingService.cancelShipment("TRACK-456")).thenReturn(true);

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertEquals(new CancellationResult.Failure("Order not found"), result);
        verify(orderRepository).findById("ORDER-001");
        verifyNoInteractions(paymentService, stockService, shippingService);
    }
}