package domain

enum class PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}

data class PaymentResult(
    val orderId: String,
    val status: PaymentStatus,
    val transactionId: String? = null
)

