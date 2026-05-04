package helper;

import domain.Order;
import domain.OrderStatus;
import repository.OrderRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<String, Order> orders = new HashMap<>();

    public InMemoryOrderRepository with(Order order) {
        orders.put(order.id(), order);
        return this;
    }

    @Override
    public Order findById(String orderId) {
        return orders.get(orderId);
    }

    @Override
    public void updateStatus(String orderId, OrderStatus status) {
        Order order = orders.get(orderId);
        if (order != null) {
            orders.put(orderId, new Order(
                order.id(), order.customerId(), order.items(),
                status, order.totalPrice(), order.transactionId(), order.trackingNumber()
            ));
        }
    }
}

