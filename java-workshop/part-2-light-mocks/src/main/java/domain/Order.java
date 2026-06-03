package domain;

import java.util.List;
public record Order(
    String id,
    String customerId,
    List<OrderItem> items,
    OrderStatus status,
    double totalPrice,
    String transactionId,
    String trackingNumber,
    DiscountCode discountCode
) {
    public Order {
        items = List.copyOf(items);
    }

    public Order(
        String id,
        String customerId,
        List<OrderItem> items,
        OrderStatus status,
        double totalPrice,
        String transactionId,
        String trackingNumber
    ) {
        this(id, customerId, items, status, totalPrice, transactionId, trackingNumber, null);
    }

    public Order confirm(double totalPrice) {
        return new Order(id, customerId, items, OrderStatus.CONFIRMED, totalPrice, transactionId, trackingNumber, discountCode);
    }

    public Order reject() {
        return new Order(id, customerId, items, OrderStatus.REJECTED, totalPrice, transactionId, trackingNumber, discountCode);
    }
}
