package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.ShippingConfirmation;
import gateway.ShippingGateway;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class ShippingServiceTest {

    @Test
    void createShipment_callsGatewayAndReturnsConfirmation() {
        ShippingGateway shippingGateway = mock(ShippingGateway.class);
        GatewayShippingService shippingService = new GatewayShippingService(shippingGateway);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(
                new OrderItem("PROD-001", 2, 10.0),
                new OrderItem("PROD-002", 1, 20.0)
            ),
            OrderStatus.PENDING,
            0.0,
            null,
            null
        );
        var expectedConfirmation = new ShippingConfirmation("ORDER-123", "TRACK-ABC123", "2024-12-25");

        when(shippingGateway.createShipment(any())).thenReturn(expectedConfirmation);

        ShippingConfirmation result = shippingService.createShipment(order);

        assertEquals(expectedConfirmation, result);
        verify(shippingGateway).createShipment(argThat(request ->
            request.orderId().equals("ORDER-123")
                && request.customerId().equals("CUST-456")
                && request.items().size() == 2
        ));
    }

    @Test
    void cancelShipment_callsGatewayAndReturnsResult() {
        ShippingGateway shippingGateway = mock(ShippingGateway.class);
        GatewayShippingService shippingService = new GatewayShippingService(shippingGateway);

        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(
                new OrderItem("PROD-001", 2, 10.0),
                new OrderItem("PROD-002", 1, 20.0)
            ),
            OrderStatus.PENDING,
            0.0,
            "TXN-789",
            "TRACK-ABC123"
        );

        when(shippingGateway.createShipment(any())).thenReturn(
            new ShippingConfirmation(order.id(), order.trackingNumber(), "2024-12-25")
        );
        when(shippingGateway.cancelShipment("TRACK-ABC123")).thenReturn(true);

        assertTrue(shippingService.cancelShipment("TRACK-ABC123"));
        verify(shippingGateway).cancelShipment("TRACK-ABC123");
    }
}