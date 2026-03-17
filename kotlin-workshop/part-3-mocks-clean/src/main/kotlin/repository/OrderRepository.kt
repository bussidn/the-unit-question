package repository

import domain.Order
import domain.OrderStatus

interface OrderRepository {
    fun findById(orderId: String): Order?
    fun updateStatus(orderId: String, status: OrderStatus)
}

