package repository;

import domain.Order;
import domain.OrderStatus;

public interface OrderRepository {
    Order findById(String orderId);

    void updateStatus(String orderId, OrderStatus status);
}