package domain

data class PaymentRequest(
    val orderId: String,
    val amount: Double,
    val customerId: String
)

data class PaymentResult(
    val transactionId: String,
    val status: PaymentStatus
)

enum class PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}

