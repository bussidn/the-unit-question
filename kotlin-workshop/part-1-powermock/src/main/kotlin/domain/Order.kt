package domain

import java.util.UUID

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    BACKORDER
}

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
)

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val items: List<OrderItem>,
    val status: OrderStatus = OrderStatus.PENDING,
    val totalPrice: Double = 0.0
) {
    fun confirm(totalPrice: Double): Order = copy(
        status = OrderStatus.CONFIRMED,
        totalPrice = totalPrice
    )
    
    fun reject(): Order = copy(status = OrderStatus.REJECTED)
    
    fun backorder(): Order = copy(status = OrderStatus.BACKORDER)
}

