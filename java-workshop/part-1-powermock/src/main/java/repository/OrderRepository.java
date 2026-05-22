package repository;

import domain.Order;
import domain.OrderStatus;
import infrastructure.Database;

public class OrderRepository {

    public Order findById(String orderId) {
        return Database.getOrder(orderId);
    }

    public void updateStatus(String orderId, OrderStatus status) {
        Database.updateOrderStatus(orderId, status);
    }

    public void save(Order order) {
        Database.saveOrder(order);
    }
}