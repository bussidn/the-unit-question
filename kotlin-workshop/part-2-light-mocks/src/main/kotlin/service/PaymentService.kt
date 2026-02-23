package service

import domain.Order
import domain.PaymentRequest
import domain.PaymentResult
import domain.PaymentStatus
import gateway.PaymentGateway

/**
 * Payment service with injected gateway.
 * Clean architecture - easy to test with mocks!
 */
class PaymentService(
    private val paymentGateway: PaymentGateway
) {

    fun processPayment(order: Order): PaymentResult {
        val total = order.items.sumOf { it.quantity * it.unitPrice }
        val request = PaymentRequest(
            orderId = order.id,
            amount = total,
            customerId = order.customerId
        )
        return paymentGateway.process(request)
    }

    fun refundPayment(transactionId: String): Boolean {
        return paymentGateway.refund(transactionId)
    }
}

