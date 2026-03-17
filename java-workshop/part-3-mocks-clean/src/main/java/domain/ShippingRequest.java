package domain;

import java.util.List;

public record ShippingRequest(String orderId, String customerId, List<ShippingItem> items) {
    public ShippingRequest {
        items = List.copyOf(items);
    }
}