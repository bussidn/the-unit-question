package service;

import domain.*;
import infrastructure.Database;
import infrastructure.PaymentGateway;
import infrastructure.ShippingApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * THE POWERMOCK NIGHTMARE
 * 
 * To test OrderService in isolation, we need to mock:
 * - Database static methods
 * - PaymentGateway static methods
 * - StockService constructor
 * - PricingService constructor
 * - PaymentService constructor
 * - ShippingService constructor
 * - ShippingApi constructor (used inside ShippingService)
 * 
 * This is madness. But this is what "unit testing" means
 * when you take "isolation" to its extreme.
 */
class OrderServiceTest {

    private OrderService orderService;
    private MockedStatic<Database> mockedDatabase;
    private MockedStatic<PaymentGateway> mockedPaymentGateway;
    private MockedConstruction<ShippingApi> mockedShippingApi;

    @BeforeEach
    void setUp() {
        mockedDatabase = mockStatic(Database.class);
        mockedPaymentGateway = mockStatic(PaymentGateway.class);
        orderService = new OrderService();
    }

    @AfterEach
    void tearDown() {
        mockedDatabase.close();
        mockedPaymentGateway.close();
        if (mockedShippingApi != null) {
            mockedShippingApi.close();
        }
    }

    @Test
    void createOrder_succeeds_whenAllStepsPass() {
        // Given
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0
        );

        // Mock Database for stock checks and updates
        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 100));

        // Mock PaymentGateway - use anyDouble() since we don't mock PricingService
        mockedPaymentGateway.when(() -> PaymentGateway.processPayment(eq("CUST-456"), anyDouble()))
            .thenReturn(new PaymentResult("", PaymentStatus.SUCCESS, "TXN-789"));

        // Mock ShippingApi constructor
        mockedShippingApi = mockConstruction(ShippingApi.class, (mock, context) -> {
            when(mock.createShipment(any()))
                .thenReturn(new ShippingConfirmation("ORDER-123", "TRACK-ABC", "2024-12-25"));
        });

        // When
        OrderResult result = orderService.createOrder(order);

        // Then
        assertInstanceOf(OrderResult.Success.class, result);
        var success = (OrderResult.Success) result;
        assertEquals(OrderStatus.CONFIRMED, success.order().status());
    }

    @Test
    void createOrder_fails_whenStockIsUnavailable() {
        // Given
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0
        );

        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 0)); // Out of stock!

        // When
        OrderResult result = orderService.createOrder(order);

        // Then
        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Insufficient stock", ((OrderResult.Failure) result).reason());

        // Verify payment was never called
        mockedPaymentGateway.verifyNoInteractions();
    }

    @Test
    void createOrder_failsAndRefunds_whenStockReservationFails() {
        // Given
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(new OrderItem("PROD-001", 2, 10.0)),
            OrderStatus.PENDING,
            0.0
        );

        // First call: availability check passes, second call: reservation fails
        mockedDatabase.when(() -> Database.getStock("PROD-001"))
            .thenReturn(new Stock("PROD-001", 100))  // availability check
            .thenReturn(new Stock("PROD-001", 0));   // reservation fails

        mockedPaymentGateway.when(() -> PaymentGateway.processPayment(anyString(), anyDouble()))
            .thenReturn(new PaymentResult("", PaymentStatus.SUCCESS, "TXN-789"));
        mockedPaymentGateway.when(() -> PaymentGateway.refund("TXN-789"))
            .thenReturn(true);

        // When
        OrderResult result = orderService.createOrder(order);

        // Then
        assertInstanceOf(OrderResult.Failure.class, result);
        assertEquals("Could not reserve stock", ((OrderResult.Failure) result).reason());

        // Verify refund was called
        mockedPaymentGateway.verify(() -> PaymentGateway.refund("TXN-789"));
    }
}

