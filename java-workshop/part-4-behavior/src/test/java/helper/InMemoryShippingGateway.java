package helper;

import domain.ShippingConfirmation;
import domain.ShippingRequest;
import gateway.ShippingGateway;

public class InMemoryShippingGateway implements ShippingGateway {

    private ShippingConfirmation lastConfirmation;

    @Override
    public ShippingConfirmation createShipment(ShippingRequest request) {
        lastConfirmation = new ShippingConfirmation(
            request.orderId(),
            "TRACK-" + request.orderId(),
            "2024-12-25"
        );
        return lastConfirmation;
    }

    @Override
    public boolean cancelShipment(String trackingNumber) {
        return true;
    }

    public boolean hasCreatedShipment() {
        return lastConfirmation != null;
    }

    public ShippingConfirmation lastConfirmation() {
        return lastConfirmation;
    }
}

