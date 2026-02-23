package infrastructure

import domain.PaymentResult
import domain.PaymentStatus
import java.util.UUID

/**
 * Static methods - another thing that requires PowerMock-style mocking
 */
object PaymentGateway {
    
    fun processPayment(customerId: String, amount: Double): PaymentResult {
        // In real life, this would call an external payment provider
        return if (amount > 0) {
            PaymentResult(
                orderId = "",
                status = PaymentStatus.SUCCESS,
                transactionId = UUID.randomUUID().toString()
            )
        } else {
            PaymentResult(
                orderId = "",
                status = PaymentStatus.FAILED
            )
        }
    }
    
    fun refund(transactionId: String): Boolean {
        // Simulate refund
        return true
    }
}

