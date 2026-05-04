package service;

import domain.ShippingConfirmation;
import gateway.ShippingGateway;
import helper.OrderBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShippingGateway shippingGateway;

    @InjectMocks
    private GatewayShippingService shippingService;

    @Test
    void createShipment_returnsGatewayConfirmation() {
        var order = OrderBuilder.confirmed().withId("ORDER-001").build();
        var confirmation = new ShippingConfirmation("ORDER-001", "TRACK-123", "2025-12-25");
        when(shippingGateway.createShipment(any())).thenReturn(confirmation);

        var result = shippingService.createShipment(order);

        assertEquals(confirmation, result);
        verify(shippingGateway).createShipment(any());
    }

    @Test
    void cancelShipment_delegatesToGateway() {
        when(shippingGateway.cancelShipment(anyString())).thenReturn(true);

        assertTrue(shippingService.cancelShipment("TRACK-123"));
        verify(shippingGateway).cancelShipment("TRACK-123");
    }
}

