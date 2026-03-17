package service;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;
import domain.ShippingConfirmation;
import infrastructure.ShippingApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * PowerMock style: We need to mock the constructor of ShippingApi
 * because ShippingService creates it internally with `new`
 */
class ShippingServiceTest {

    private ShippingService shippingService;
    private MockedConstruction<ShippingApi> mockedConstruction;

    @BeforeEach
    void setUp() {
        shippingService = new ShippingService();
    }

    @AfterEach
    void tearDown() {
        if (mockedConstruction != null) {
            mockedConstruction.close();
        }
    }

    @Test
    void createShipment_callsApiAndReturnsConfirmation() {
        // Given
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

        var expectedConfirmation = new ShippingConfirmation(
            "ORDER-123",
            "TRACK-ABC123",
            "2024-12-25"
        );

        mockedConstruction = mockConstruction(ShippingApi.class, (mock, context) -> {
            when(mock.createShipment(any())).thenReturn(expectedConfirmation);
        });

        // When
        ShippingConfirmation result = shippingService.createShipment(order);

        // Then
        assertEquals(expectedConfirmation, result);
        assertEquals(1, mockedConstruction.constructed().size());

        ShippingApi constructedApi = mockedConstruction.constructed().get(0);
        verify(constructedApi).createShipment(argThat(request ->
            request.orderId().equals("ORDER-123") &&
            request.customerId().equals("CUST-456") &&
            request.items().size() == 2
        ));
    }

    @Test
    void cancelShipment_callsApiAndReturnsResult() {
        // Given
        String trackingNumber = "TRACK-ABC123";

        mockedConstruction = mockConstruction(ShippingApi.class, (mock, context) -> {
            when(mock.cancelShipment(trackingNumber)).thenReturn(true);
        });

        // When
        boolean result = shippingService.cancelShipment(trackingNumber);

        // Then
        assertTrue(result);
        verify(mockedConstruction.constructed().get(0)).cancelShipment(trackingNumber);
    }
}

