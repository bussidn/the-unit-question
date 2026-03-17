package domain;

import java.util.List;
import java.util.UUID;

public record Order(
    String id,
    String customerId,
    List<OrderItem> items,
    OrderStatus status,
    double totalPrice
) {
    public Order {
        items = List.copyOf(items);
    }

    public static Order create(String customerId, List<OrderItem> items) {
        return new Order(UUID.randomUUID().toString(), customerId, items, OrderStatus.PENDING, 0.0);
    }

    public Order confirm(double totalPrice) {
        return new Order(id, customerId, items, OrderStatus.CONFIRMED, totalPrice);
    }

    public Order reject() {
        return new Order(id, customerId, items, OrderStatus.REJECTED, totalPrice);
    }

    public Order backorder() {
        return new Order(id, customerId, items, OrderStatus.BACKORDER, totalPrice);
    }
}