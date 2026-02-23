package domain

data class ShippingRequest(
    val orderId: String,
    val customerId: String,
    val items: List<ShippingItem>
)

data class ShippingItem(
    val productId: String,
    val quantity: Int
)

data class ShippingConfirmation(
    val orderId: String,
    val trackingNumber: String,
    val estimatedDelivery: String
)

