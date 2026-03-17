package service;

import domain.Order;
import domain.ShippingConfirmation;
import domain.ShippingItem;
import domain.ShippingRequest;
import infrastructure.ShippingApi;

import java.util.stream.Collectors;

/**
 * Creates ShippingApi with new - requires PowerMock to mock constructor.
 */
public class ShippingService {

    public ShippingConfirmation createShipment(Order order) {
        ShippingApi api = new ShippingApi();
        ShippingRequest request = new ShippingRequest(
            order.id(),
            order.customerId(),
            order.items().stream()
                .map(item -> new ShippingItem(item.productId(), item.quantity()))
                .collect(Collectors.toList())
        );
        return api.createShipment(request);
    }

    public boolean cancelShipment(String trackingNumber) {
        ShippingApi api = new ShippingApi();
        return api.cancelShipment(trackingNumber);
    }
}