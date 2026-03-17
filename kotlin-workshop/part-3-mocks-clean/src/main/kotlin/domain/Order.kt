package domain

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: Double
)

data class Order(
    val id: String,
    val customerId: String,
    val items: List<OrderItem>,
    val status: OrderStatus = OrderStatus.PENDING,
    val transactionId: String? = null,
    val trackingNumber: String? = null
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

