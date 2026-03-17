package infrastructure;

import domain.ShippingConfirmation;
import domain.ShippingRequest;
import java.util.UUID;

/**
 * External API client - instantiated directly in services (bad practice)
 * Requires PowerMock to mock constructor.
 */
public class ShippingApi {
    private final String baseUrl = "https://shipping.example.com";

    public ShippingConfirmation createShipment(ShippingRequest request) {
        System.out.println("Calling shipping API at " + baseUrl);
        return new ShippingConfirmation(
                request.orderId(),
                "TRACK-" + UUID.randomUUID().toString().substring(0, 8),
                "2024-12-25"
        );
    }

    public boolean cancelShipment(String trackingNumber) {
        System.out.println("Cancelling shipment via " + baseUrl + " for " + trackingNumber);
        return true;
    }
}