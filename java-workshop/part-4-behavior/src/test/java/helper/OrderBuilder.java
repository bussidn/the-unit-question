package helper;

import domain.Order;
import domain.OrderItem;
import domain.OrderStatus;

import java.util.List;

public final class OrderBuilder {
    private String id = "ORDER-DEFAULT";
    private String customerId = "CUST-001";
    private List<OrderItem> items = List.of(new OrderItem("PROD-001", 1, 20.0));
    private OrderStatus status = OrderStatus.PENDING;
    private double totalPrice = 20.0;
    private String transactionId;
    private String trackingNumber;

    private OrderBuilder() {
    }

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public static OrderBuilder confirmed() {
        return anOrder().withStatus(OrderStatus.CONFIRMED);
    }

    public OrderBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public OrderBuilder withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderBuilder withItems(List<OrderItem> items) {
        this.items = List.copyOf(items);
        return this;
    }

    public OrderBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder withTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
        return this;
    }

    public OrderBuilder withTransaction(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public OrderBuilder withoutTransaction() {
        this.transactionId = null;
        return this;
    }

    public OrderBuilder withTracking(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        return this;
    }

    public OrderBuilder withoutTracking() {
        this.trackingNumber = null;
        return this;
    }

    public Order build() {
        return new Order(id, customerId, items, status, totalPrice, transactionId, trackingNumber);
    }
}