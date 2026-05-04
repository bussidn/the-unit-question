package service;

import domain.CancellationResult;
import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import gateway.PaymentGateway;
import gateway.ShippingGateway;
import helper.InMemoryOrderRepository;
import helper.InMemoryStockRepository;
import helper.OrderBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderCancellationServiceTest {

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private ShippingGateway shippingGateway;

    private InMemoryOrderRepository orderRepository;
    private InMemoryStockRepository stockRepository;
    private OrderCancellationService service;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        stockRepository = new InMemoryStockRepository();

        StockService stockService = new RepositoryStockService(stockRepository);
        PaymentService paymentService = new GatewayPaymentService(paymentGateway);
        ShippingService shippingService = new GatewayShippingService(shippingGateway);

        service = new OrderCancellationService(orderRepository, paymentService, stockService, shippingService);
    }

    @Test
    void cancelOrder_succeeds_whenPaymentRefundedAndShipmentCancelled() {
        Order order = OrderBuilder.confirmed()
            .withId("ORDER-001")
            .withTransaction("TXN-123")
            .withTracking("TRACK-456")
            .withItems(List.of(new OrderItem("PROD-001", 2, 25.0)))
            .build();

        givenOrderExists(order);
        givenStockAvailable("PROD-001", 10);
        givenRefundSucceeds();
        givenShipmentCancelled();

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertSuccess(result);
        verify(paymentGateway).refund("TXN-123");
        verify(shippingGateway).cancelShipment("TRACK-456");
        assertEquals(OrderStatus.CANCELLED, orderRepository.findById("ORDER-001").status());
    }

    @Test
    void cancelOrder_succeeds_whenNoShipmentExists() {
        Order order = OrderBuilder.confirmed()
            .withId("ORDER-002")
            .withTransaction("TXN-456")
            .withoutTracking()
            .withItems(List.of(new OrderItem("PROD-001", 1, 30.0)))
            .build();

        givenOrderExists(order);
        givenStockAvailable("PROD-001", 5);
        givenRefundSucceeds();

        CancellationResult result = service.cancelOrder("ORDER-002");

        assertSuccess(result);
        verify(paymentGateway).refund("TXN-456");
        assertEquals(OrderStatus.CANCELLED, orderRepository.findById("ORDER-002").status());
    }

    @Test
    void cancelOrder_fails_whenRefundFails() {
        Order order = OrderBuilder.confirmed()
            .withId("ORDER-001")
            .withTransaction("TXN-123")
            .withoutTracking()
            .withItems(List.of(new OrderItem("PROD-001", 1, 30.0)))
            .build();

        givenOrderExists(order);
        givenRefundFails();

        CancellationResult result = service.cancelOrder("ORDER-001");

        assertFailure(result, "Refund failed");
        assertEquals(OrderStatus.CONFIRMED, orderRepository.findById("ORDER-001").status());
    }

    @Test
    void cancelOrder_fails_whenOrderDoesNotExist() {
        CancellationResult result = service.cancelOrder("ORDER-UNKNOWN");

        assertFailure(result, "Order not found");
    }

    private void givenOrderExists(Order order) {
        orderRepository.with(order);
    }

    private void givenStockAvailable(String productId, int quantity) {
        stockRepository.withAvailableStock(productId, quantity);
    }

    private void givenRefundSucceeds() {
        when(paymentGateway.refund(anyString())).thenReturn(true);
    }

    private void givenRefundFails() {
        when(paymentGateway.refund(anyString())).thenReturn(false);
    }

    private void givenShipmentCancelled() {
        when(shippingGateway.cancelShipment(anyString())).thenReturn(true);
    }

    private void assertSuccess(CancellationResult result) {
        assertEquals(new CancellationResult.Success(), result);
    }

    private void assertFailure(CancellationResult result, String reason) {
        assertEquals(new CancellationResult.Failure(reason), result);
    }
}

