package domain;

import java.util.List;
import java.util.UUID;

public record Order(
    String id,
    String customerId,
    List<OrderItem> items,
    OrderStatus status,
    double totalPrice,
    String transactionId,
    String trackingNumber
) {
    public Order {
        items = List.copyOf(items);
    }

    public static Order create(String customerId, List<OrderItem> items) {
        return new Order(UUID.randomUUID().toString(), customerId, items, OrderStatus.PENDING, 0.0, null, null);
    }

    public Order confirm(double totalPrice) {
        return new Order(id, customerId, items, OrderStatus.CONFIRMED, totalPrice, transactionId, trackingNumber);
    }

    public Order reject() {
        return new Order(id, customerId, items, OrderStatus.REJECTED, totalPrice, transactionId, trackingNumber);
    }

    public Order backorder() {
        return new Order(id, customerId, items, OrderStatus.BACKORDER, totalPrice, transactionId, trackingNumber);
    }
}