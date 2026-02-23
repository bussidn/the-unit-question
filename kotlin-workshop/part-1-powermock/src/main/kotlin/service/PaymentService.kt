package service

import domain.PaymentResult
import domain.PaymentStatus
import infrastructure.PaymentGateway

/**
 * Payment service with hardcoded PaymentGateway dependency (object/static)
 * This forces us to use mockkObject to test it in isolation
 */
class PaymentService {
    
    fun processPayment(orderId: String, customerId: String, amount: Double): PaymentResult {
        if (amount <= 0) {
            return PaymentResult(orderId, PaymentStatus.FAILED)
        }
        
        val gatewayResult = PaymentGateway.processPayment(customerId, amount)
        
        return gatewayResult.copy(orderId = orderId)
    }
    
    fun refundPayment(transactionId: String): Boolean {
        return PaymentGateway.refund(transactionId)
    }
}

