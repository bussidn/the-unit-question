package gateway;

import domain.ShippingConfirmation;
import domain.ShippingRequest;

public interface ShippingGateway {
    ShippingConfirmation createShipment(ShippingRequest request);

    boolean cancelShipment(String trackingNumber);
}

