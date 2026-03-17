package service

/**
 * Payment service interface for domain operations.
 */
interface PaymentService {
    fun refundPayment(transactionId: String): Boolean
}

