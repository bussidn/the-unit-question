package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.ShippingConfirmation;
import gateway.ShippingGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShippingGateway shippingGateway;

    @InjectMocks
    private ShippingService shippingService;

    @Test
    void createShipment_callsGatewayAndReturnsConfirmation() {
        var order = new Order(
            "ORDER-123",
            "CUST-456",
            List.of(
                new OrderItem("PROD-001", 2, 10.0),
                new OrderItem("PROD-002", 1, 20.0)
            ),
            OrderStatus.PENDING,
            0.0
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
        when(shippingGateway.cancelShipment("TRACK-ABC123")).thenReturn(true);

        assertTrue(shippingService.cancelShipment("TRACK-ABC123"));
        verify(shippingGateway).cancelShipment("TRACK-ABC123");
    }
}