package service;

import domain.Order;
import domain.ShippingConfirmation;
import domain.ShippingItem;
import domain.ShippingRequest;
import gateway.ShippingGateway;

import java.util.stream.Collectors;

public class GatewayShippingService implements ShippingService {
    private final ShippingGateway shippingGateway;

    public GatewayShippingService(ShippingGateway shippingGateway) {
        this.shippingGateway = shippingGateway;
    }

    @Override
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

    @Override
    public boolean cancelShipment(String trackingNumber) {
        return shippingGateway.cancelShipment(trackingNumber);
    }
}