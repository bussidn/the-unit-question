package service;

import domain.Order;
import domain.ShippingConfirmation;
import domain.ShippingItem;
import domain.ShippingRequest;
import gateway.ShippingGateway;

import java.util.stream.Collectors;

public class ShippingService {
    private final ShippingGateway shippingGateway;

    public ShippingService(ShippingGateway shippingGateway) {
        this.shippingGateway = shippingGateway;
    }

    public ShippingConfirmation createShipment(Order order) {
        ShippingRequest request = new ShippingRequest(
            order.id(),
            order.customerId(),
            order.items().stream()
                .map(item -> new ShippingItem(item.productId(), item.quantity()))
                .collect(Collectors.toList())
        );
        return shippingGateway.createShipment(request);
    }

    public boolean cancelShipment(String trackingNumber) {
        return shippingGateway.cancelShipment(trackingNumber);
    }
}