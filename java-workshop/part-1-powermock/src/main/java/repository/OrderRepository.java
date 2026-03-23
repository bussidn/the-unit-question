package repository;

import domain.Order;
import domain.OrderStatus;
import infrastructure.Database;

public final class OrderRepository {
    private OrderRepository() {
    }

    public static Order findById(String orderId) {
        return Database.getOrder(orderId);
    }

    public static void updateStatus(String orderId, OrderStatus status) {
        Database.updateOrderStatus(orderId, status);
    }
}