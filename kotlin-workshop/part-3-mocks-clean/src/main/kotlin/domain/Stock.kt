package domain

data class Stock(
    val productId: String,
    val availableQuantity: Int
)

data class StockReservation(
    val productId: String,
    val quantity: Int,
    val reserved: Boolean
)

