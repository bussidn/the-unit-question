package gateway

import domain.PaymentRequest
import domain.PaymentResult

/**
 * Gateway interface for payment processing.
 * Injected into services - easy to mock!
 */
interface PaymentGateway {
    fun process(request: PaymentRequest): PaymentResult
    fun refund(transactionId: String): Boolean
}

